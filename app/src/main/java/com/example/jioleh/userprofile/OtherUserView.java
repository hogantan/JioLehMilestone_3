package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jioleh.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

public class OtherUserView extends AppCompatActivity {

    private String uid;
    private userProfileViewModel viewModel;
    private TextView tv_username, tv_age, tv_gender, tv_location;
    private ImageView iv_ProfilePic;
    private Toolbar toolbar;

    private UserProfileViewPagerAdapter pagerAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_view);
        initialise();
        initialiseToolbar();
         

        Intent intent = getIntent();
        uid = intent.getStringExtra("user_id");
        viewModel= new ViewModelProvider(this).get(userProfileViewModel.class);

        viewModel.getUser(uid).observe(this, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                fill(userProfile);
            }
        });
        
        initialiaseViewPagerAndTab();

    }

    public void fill(UserProfile userProfile) {
        tv_username.setText(userProfile.getUsername());
        tv_age.setText(userProfile.getAge());
        tv_gender.setText(userProfile.getGender());
        tv_location.setText(userProfile.getLocation());

        if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl() != null) {
            Picasso.get().load(userProfile.getImageUrl()).into(iv_ProfilePic);
        }
    }

    public void initialise(){
        tv_username = findViewById(R.id.tv_profilePageUsername);
        tv_age = findViewById(R.id.tv_profilePageAge);
        tv_gender = findViewById(R.id.tv_profilePageGender);
        tv_location = findViewById(R.id.tv_profilePageLocation);
        iv_ProfilePic = findViewById(R.id.iv_userProfilePageImage);

    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.include_top_app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiaseViewPagerAndTab() {
        viewPager2 = findViewById(R.id.userProfile_viewPager);
        pagerAdapter = new UserProfileViewPagerAdapter(this,uid);
        viewPager2.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.userProfile_tabLayout);

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




}
