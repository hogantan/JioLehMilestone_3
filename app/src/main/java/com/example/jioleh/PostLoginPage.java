package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostLoginPage extends AppCompatActivity {

    private Toolbar toolbar;
    //private DrawerLayout drawer;
    private FirebaseAuth database;

    private BottomNavigationView bottom_nav_view;

    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login_page);
        initialise();
        initialiseToolbar();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        /*
        //put the navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //actionBar Toggle i.e. it will turn
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        //navigationView.setCheckedItem(R.id.nav_home);
         */

        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.bab_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.bab_chat:
                        selectedFragment = new ChatFragment();
                        break;
                    case R.id.bab_favourite:
                        selectedFragment = new FavouriteFragment();
                        break;
                    case R.id.bab_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                    case R.id.bab_post:
                        selectedFragment = new PostFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });



        /*
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            //navigationView.setCheckedItem(R.id.nav_home);

            String UID = database.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        populateUserDetailsToNavHeader(userProfile);
                }
            });

         */


    }

    private void initialise() {
        //drawer = findViewById(R.id.drawer_layout);
        database = FirebaseAuth.getInstance();

        bottom_nav_view = findViewById(R.id.bnvBtmNavBar);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /*
    //different actions based on what item is selected on the navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_signout:
                database.signOut();
                startActivity(new Intent(PostLoginPage.this, MainActivity.class));
                finish();
                break;

            case R.id.nav_settings:
                startActivity(new Intent(PostLoginPage.this,SettingsPage.class));

            case R.id.nav_chat:
                startActivity(new Intent(PostLoginPage.this, ChatPage.class));

                break;
            case R.id.nav_profile:
                startActivity(new Intent(PostLoginPage.this,ProfilePage.class));
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
        }
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

     */

    /*
    //if drawer open we press back, simply close the drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

     */

    /*

    private void populateUserDetailsToNavHeader(UserProfile userProfile) {
        TextView nav_header_username = findViewById(R.id.nav_header_username);
        TextView nav_header_email = findViewById(R.id.nav_header_email);
        ImageView nav_header_profilePic = findViewById(R.id.nav_header_profile_pic);

        nav_header_username.setText(userProfile.getUsername());
        nav_header_email.setText(database.getCurrentUser().getEmail());

        //if there is uploaded image url then retrieve
        if (userProfile.getImageUrl()!="" && userProfile.getImageUrl()!=null) {
            Picasso.get().load(userProfile.getImageUrl()).into(nav_header_profilePic);
        }
     */

    }
