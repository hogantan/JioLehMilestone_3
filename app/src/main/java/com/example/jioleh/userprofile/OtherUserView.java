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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

public class OtherUserView extends AppCompatActivity {

    private String uid;
    private String imageUrl;
    private userProfileViewModel viewModel;
    private TextView tv_username, tv_age, tv_gender;
    private ImageView iv_ProfilePic;
    private Button message;
    private Toolbar toolbar;

    private UserProfileViewPagerAdapter pagerAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_view);
        initialise();
        initialiseToolbar();
         

        final Intent intent = getIntent();
        uid = intent.getStringExtra("user_id");
        viewModel= new ViewModelProvider(this).get(userProfileViewModel.class);

        viewModel.getUser(uid).observe(this, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                fill(userProfile);
            }
        });
        
        initialiaseViewPagerAndTab();

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getBooleanExtra("not_from_message_page", false)) {
                    onBackPressed();
                } else {
                    Intent nextActivity = new Intent(OtherUserView.this, MessagePage.class);
                    nextActivity.putExtra("username", tv_username.getText().toString());
                    nextActivity.putExtra("user_id", uid);
                    nextActivity.putExtra("image_url", imageUrl);
                    nextActivity.putExtra("not_from_other_user_view", true);
                    startActivity(nextActivity);
                }
            }
        });
    }

    public void fill(UserProfile userProfile) {
        tv_username.setText(userProfile.getUsername());
        tv_age.setText(userProfile.getAge());
        tv_gender.setText(userProfile.getGender());
        imageUrl = userProfile.getImageUrl();
        if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl() != null) {
            Picasso.get().load(imageUrl).into(iv_ProfilePic);
        }
    }

    public void initialise(){
        tv_username = findViewById(R.id.tv_profilePageUsername);
        tv_age = findViewById(R.id.tv_profilePageAge);
        tv_gender = findViewById(R.id.tv_profilePageGender);
        iv_ProfilePic = findViewById(R.id.iv_userProfilePageImage);
        message = findViewById(R.id.message_other_user);
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
