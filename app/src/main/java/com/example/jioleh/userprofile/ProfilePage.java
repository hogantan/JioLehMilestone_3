package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {

    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_age;
    private TextView tv_gender;
    private TextView tv_contact;
    private TextView tv_bio;
    private ImageView iv_userProfileImage;
    private Toolbar toolbar;
    private Button btn_settings;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        initialise();
        initialiseToolbar();
        fillWithUserDetails();
        /*
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, SettingsPage.class));
            }
        });

         */
    }

    public void initialise() {
        tv_username = findViewById(R.id.tv_profilePageUsername);
        //tv_email = findViewById(R.id.tv_profilePageEmail);
        tv_age = findViewById(R.id.tv_profilePageAge);
        tv_gender = findViewById(R.id.tv_profilePageGender);
        //tv_contact = findViewById(R.id.tv_profilePageContact);
        //tv_bio = findViewById(R.id.tv_profilePageBio);
        iv_userProfileImage = findViewById(R.id.iv_userProfilePageImage);
        //btn_settings = findViewById(R.id.btn_settings);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbProfilePage);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fillWithUserDetails() {
        FirebaseUser currUser = firebaseAuth.getCurrentUser();
        String currUserUID = currUser.getUid();
        String currUserEmail = currUser.getEmail();
        //tv_email.setText(currUserEmail);

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
                    Toast.makeText(ProfilePage.this,"user details cannot be found",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
