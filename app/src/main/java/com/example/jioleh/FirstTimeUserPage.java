package com.example.jioleh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirstTimeUserPage extends AppCompatActivity {

    private TextInputLayout til_username;
    private TextInputLayout til_contact;
    private TextInputLayout til_gender;
    private TextInputLayout til_age;
    private TextInputLayout til_bio;
    private Button btn_CreateProfile;
    private FirebaseFirestore documentReference = FirebaseFirestore.getInstance();
    private UserProfile user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_page);
        initialise();
    }

    private void initialise() {
        til_username = findViewById(R.id.username);
        til_contact = findViewById(R.id.contact);
        til_gender = findViewById(R.id.gender);
        til_age = findViewById(R.id.age);
        til_bio = findViewById(R.id.bio);
        btn_CreateProfile = findViewById(R.id.btn_createProfile);

    }

    public void createProfile(View v) {
        String username = til_username.getEditText().getText().toString();
        String contact = til_contact.getEditText().getText().toString();
        String gender = til_gender.getEditText().getText().toString();
        String age = til_age.getEditText().getText().toString();
        String bio = til_bio.getEditText().getText().toString();

        if (!validateUsername(username) | !validateContact(contact)) {
            alertDialog();
        } else {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userID = firebaseUser.getUid();

            UserProfile user = new UserProfile(username, contact, gender, age, bio);
            documentReference.collection("users").document(userID).set(user, SetOptions.merge());
            startActivity(new Intent(this, PostLoginPage.class));
        }
    }

    public boolean validateUsername(String username) {
        if (username.isEmpty()) {
            til_username.getEditText().setError("Field can't be empty");
            return false;
        } else {
            til_username.getEditText().setError(null);
            return true;
        }
    }

    public boolean validateContact(String contact) {
        if (contact.isEmpty()) {
            til_contact.getEditText().setError("Contact can't be empty");
            return  false;
        } else {
            til_contact.getEditText().setError(null);
            return true;
        }
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstTimeUserPage.this);

        builder.setMessage("Please fill up all fields")
                .setTitle("Setup Profile");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();

    }


}
