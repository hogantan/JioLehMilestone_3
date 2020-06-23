package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jioleh.chat.ChatFragment;
import com.example.jioleh.favourites.FavouriteFragment;
import com.example.jioleh.listings.HomeFragment;
import com.example.jioleh.post.PostingPage;
import com.example.jioleh.post.PostFragment;
import com.example.jioleh.settings.EditProfilePage;
import com.example.jioleh.userprofile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostLoginPage extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton jio;
    private TextView toolbar_title;
    private BottomNavigationView bottom_nav_view;

    private FirebaseAuth database;
    private FirebaseFirestore firebaseFirestore;

    Fragment frag1 = new HomeFragment();
    Fragment frag2 = new ChatFragment();
    Fragment frag3 = new FavouriteFragment();
    Fragment frag4 = new PostFragment();
    Fragment frag5 = new ProfileFragment();
    FragmentManager fm = getSupportFragmentManager();

    Fragment active = frag1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login_page);
        initialiseFragments();
        initialise();
        checkIfFromEditProfile();
        initialiseToolbar();

        jio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(PostLoginPage.this, PostingPage.class);
                startActivity(nextActivity);
            }
        });


        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.bab_home:
                        selectedFragment = frag1;
                        toolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_background));
                        toolbar_title.setText("JioLeh");

                        break;
                    case R.id.bab_chat:
                        selectedFragment = frag2;
                        toolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_background));
                        toolbar_title.setText("Messages");

                        break;
                    case R.id.bab_favourite:
                        selectedFragment = frag3;
                        toolbar.setBackground(getResources().getDrawable(R.color.baseBlack));
                        toolbar_title.setText("Favourites");

                        break;
                    case R.id.bab_post:
                        //Intent nextActivity = new Intent(PostLoginPage.this, PostingPage.class);
                        //startActivity(nextActivity);
                        break;
                    case R.id.bab_profile:
                        selectedFragment =frag5;
                        toolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_background));
                        toolbar_title.setText("My Profile");
                        break;
                }
                if (selectedFragment != null) {
                    fm.beginTransaction().hide(active).show(selectedFragment).commit();
                    active = selectedFragment;
                }
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }

    private void initialise() {
        database = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        bottom_nav_view = findViewById(R.id.bnvBtmNavBar);
        toolbar_title = findViewById(R.id.tbTitle);
        jio = findViewById(R.id.btnJio);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }

    private void initialiseFragments() {
        fm.beginTransaction().add(R.id.fragment_container, frag5, "5").hide(frag5).commit();
        fm.beginTransaction().add(R.id.fragment_container, frag4, "4").hide(frag4).commit();
        fm.beginTransaction().add(R.id.fragment_container, frag3, "3").hide(frag3).commit();
        fm.beginTransaction().add(R.id.fragment_container, frag2, "2").hide(frag2).commit();
        fm.beginTransaction().add(R.id.fragment_container,frag1, "1").commit();
    }

    private void checkIfFromEditProfile() {
        String intentFragment = getIntent().getStringExtra("loadProfileFrag");
        if(intentFragment != null && intentFragment.equals("profilePage")) {
            fm.beginTransaction().hide(active).show(frag5).commit();
            active = frag5;
        }
    }
}
