package com.example.jioleh;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button buttonSettings;
    private ViewPager2 viewPager2;
    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_age;
    private TextView tv_gender;
    private TextView tv_contact;
    private TextView tv_bio;
    private ImageView iv_userProfileImage;
    private Toolbar toolbar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment,container, false);
        ((PostLoginPage) getActivity()).getSupportActionBar().hide();
        initialise(view);
        fillWithUserDetails();
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SettingsPage.class));
            }
        });

        attachViewPagerWithFragments(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().hide();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    public void attachViewPagerWithFragments(View view) {
        ViewPager2 vp2 = view.findViewById(R.id.userProfile_viewPager);
        vp2.setAdapter(new UserProfileViewPagerAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.userProfile_tabLayout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, vp2, new TabLayoutMediator.TabConfigurationStrategy() {
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
        //tv_email = findViewById(R.id.tv_profilePageEmail);
        tv_age = view.findViewById(R.id.tv_profilePageAge);
        tv_gender = view.findViewById(R.id.tv_profilePageGender);
        //tv_contact = findViewById(R.id.tv_profilePageContact);
        //tv_bio = view.findViewById(R.id.textView4);
        iv_userProfileImage = view.findViewById(R.id.iv_userProfilePageImage);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        buttonSettings = view.findViewById(R.id.btn_settings);
        viewPager2 = view.findViewById(R.id.userProfile_viewPager);
    }

    private void fillWithUserDetails() {
        FirebaseUser currUser = firebaseAuth.getCurrentUser();
        String currUserUID = currUser.getUid();
        String currUserEmail = currUser.getEmail();


        DocumentReference docRef = firebaseFirestore.collection("users").document(currUserUID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserProfile userProfile = task.getResult().toObject(UserProfile.class);
                    assert userProfile != null;
                    tv_age.setText(userProfile.getAge());
                    tv_username.setText(userProfile.getUsername());
                    //tv_contact.setText(userProfile.getContact());
                    //tv_bio.setText(userProfile.getBio());
                    tv_gender.setText(userProfile.getGender());

                    if (userProfile.getImageUrl()!="" && userProfile.getImageUrl()!=null) {
                        Picasso.get().load(userProfile.getImageUrl()).into(iv_userProfileImage);
                    }

                } else {
                    Toast.makeText(ProfileFragment.this.getActivity(),"user details cannot be found",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
