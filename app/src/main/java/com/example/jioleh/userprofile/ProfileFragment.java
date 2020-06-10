package com.example.jioleh.userprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.example.jioleh.settings.SettingsPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TextView tv_username, tv_age, tv_gender, tv_location;
    private ImageView iv_userProfileImage;


    private userProfileViewModel viewModel;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        //has option menu for action bar
        setHasOptionsMenu(true);

        initialise(view);
        
        //must come before viewpager coz we using uid to create the viewPager
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        //viewPager for different tabs on profile page
        attachViewPagerWithFragments(view);

        

        viewModel.getUser(uid).observe(getViewLifecycleOwner(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                //fill blanks with user details
                fillWithUserDetails(userProfile);
            }
        });

        return view;
    }


    public void attachViewPagerWithFragments(View view) {

        viewPager2.setAdapter(new UserProfileViewPagerAdapter(this, uid));

        TabLayout tabLayout = view.findViewById(R.id.userProfile_tabLayout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText("About Me");
                        break;
                    }
                    case 1: {
                        tab.setText("Listings");
                        break;
                    }
                    case 2: {
                        tab.setText("Reviews");
                        break;
                    }
                }

            }
        });

        tabLayoutMediator.attach();
    }

    public void initialise(View view) {
        tv_username = view.findViewById(R.id.tv_profilePageUsername);
        tv_age = view.findViewById(R.id.tv_profilePageAge);
        tv_gender = view.findViewById(R.id.tv_profilePageGender);
        tv_location = view.findViewById(R.id.tv_profilePageLocation);
        iv_userProfileImage = view.findViewById(R.id.iv_userProfilePageImage);
        viewPager2 = view.findViewById(R.id.userProfile_viewPager);
        viewModel = new ViewModelProvider(this).get(userProfileViewModel.class);
    }

    private void fillWithUserDetails(UserProfile userProfile) {

        assert userProfile != null;
        tv_age.setText(userProfile.getAge());
        tv_username.setText(userProfile.getUsername());
        tv_location.setText(userProfile.getLocation());
        tv_gender.setText(userProfile.getGender());

        if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl() != null) {
            Picasso.get().load(userProfile.getImageUrl()).into(iv_userProfileImage);
        } else {
            Toast.makeText(ProfileFragment.this.getActivity(),
                    "user details cannot be found", Toast.LENGTH_LONG).show();

        }
    }

    //to put in the settings icon on the top right of the action bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //onclick menu items for the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(getActivity(), SettingsPage.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
