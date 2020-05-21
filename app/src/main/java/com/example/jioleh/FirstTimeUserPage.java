package com.example.jioleh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirstTimeUserPage extends AppCompatActivity {

    private EditText et_username;
    private EditText et_contact;
    private EditText et_gender;
    private EditText et_age;
    private EditText et_bio;
    private Button btn_CreateProfile;
    private FirebaseFirestore documentReference = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_page);
        initialise();



    }

    private void initialise() {
        et_username = findViewById(R.id.username);
        et_contact = findViewById(R.id.contact);
        et_gender = findViewById(R.id.gender);
        et_age = findViewById(R.id.age);
        et_bio = findViewById(R.id.bio);
        btn_CreateProfile = findViewById(R.id.btn_createProfile);
    }

    public void createProfile(View v) {
        String username = et_username.getText().toString();
        String contact = et_contact.getText().toString();
        String gender = et_gender.getText().toString();
        String age = et_age.getText().toString();
        String bio = et_bio.getText().toString();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();

        UserProfile user = new UserProfile(username,contact,gender,age,bio);
        documentReference.collection("users").document(userID).set(user, SetOptions.merge());

        startActivity(new Intent(this,PostLoginPage.class));


    }
}
