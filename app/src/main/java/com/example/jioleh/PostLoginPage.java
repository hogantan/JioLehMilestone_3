package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.firestore.auth.User;

public class PostLoginPage extends AppCompatActivity {

    private Toolbar toolbar;
    //private DrawerLayout drawer;
    private FirebaseAuth database;

    private BottomNavigationView bottom_nav_view;

    private FirebaseFirestore firebaseFirestore;

    final Fragment frag1 = new HomeFragment();
    final Fragment frag2 = new ChatFragment();
    final Fragment frag3 = new FavouriteFragment();
    final Fragment frag4 = new PostFragment();
    final Fragment frag5 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment active = frag1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login_page);
        initialiseFragments();
        initialise();
        initialiseToolbar();

            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
              //      new HomeFragment()).commit();



        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.bab_home:
                        selectedFragment = frag1;
                        PostLoginPage.this.getSupportActionBar().show();
                        break;
                    case R.id.bab_chat:
                        selectedFragment = frag2;
                        PostLoginPage.this.getSupportActionBar().show();
                        break;
                    case R.id.bab_favourite:
                        selectedFragment = frag3;
                        PostLoginPage.this.getSupportActionBar().show();
                        break;
                    case R.id.bab_post:
                        selectedFragment = frag4;
                        PostLoginPage.this.getSupportActionBar().show();
                        break;
                    case R.id.bab_profile:
                        selectedFragment = frag5;
                        PostLoginPage.this.getSupportActionBar().hide();
                        break;
                }
                fm.beginTransaction().hide(active).show(selectedFragment).commit();
                active = selectedFragment;
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }

    private void initialise() {
        database = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        bottom_nav_view = findViewById(R.id.bnvBtmNavBar);
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


}
