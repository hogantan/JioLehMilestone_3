package com.example.jioleh.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {
    private TextInputLayout email;
    private TextInputLayout password;
    private Button login;
    private TextView forgotpassword;
    private Toolbar toolbar;

    private ProgressDialog progressBar;

    private FirebaseAuth database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        initialiseToolbar();
        initialise();

        login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUser(email.getEditText().getText().toString(), password.getEditText().getText().toString());
                }
            });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(LoginPage.this, ForgotPasswordPage.class);
                    startActivity(nextActivity);
                }
        });
    }

    private void initialise() {
        progressBar = new ProgressDialog(LoginPage.this);
        email = findViewById(R.id.tilLoginEmail);
        password = findViewById(R.id.tilLoginPassword);
        login = findViewById(R.id.btnLoginSignIn);
        forgotpassword = findViewById(R.id.tvForgotPasswordLogin);
        database = FirebaseAuth.getInstance();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTempTopBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }



    private void checkUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPage.this,
                    "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {

            //Setting Details of Loading Screen
            progressBar.setTitle("Logging in");
            progressBar.setMessage("Please wait while we check your credentials");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            database.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.dismiss();
                        checkEmailVerification();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        progressBar.dismiss();
                        Toast.makeText(LoginPage.this,
                                "Unregistered email address",
                                Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                        progressBar.dismiss();
                        Toast.makeText(LoginPage.this, "Too many unsuccessful login attempts, please try again later", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.dismiss();
                        Toast.makeText(LoginPage.this,
                                "Incorrect password" ,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkEmailVerification() {
        FirebaseUser currentUser = database.getCurrentUser();
        boolean isVerified = currentUser.isEmailVerified();
        if (isVerified) {
            Toast.makeText(LoginPage.this,
                    "Login successful", Toast.LENGTH_SHORT).show();
            firstTimeUserCheck();
        } else {
            Toast.makeText(LoginPage.this,
                    "Account is not verified, please check email",
                    Toast.LENGTH_SHORT).show();
            database.signOut(); //sign out unverified user
        }
    }

    public void firstTimeUserCheck() {
        String userID = database.getCurrentUser().getUid();
        DocumentReference documentReference = FirebaseFirestore
                .getInstance().collection("users").document(userID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserProfile user = documentSnapshot.toObject(UserProfile.class);
                assert user != null;
                if(user.getIsNewUser()) {
                    Intent newUserActivity = new Intent(LoginPage.this, FirstTimeUserPage.class);
                    startActivity(newUserActivity);
                } else {
                    Intent nextActivity = new Intent(LoginPage.this, PostLoginPage.class);
                    nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(nextActivity);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.dismiss();
    }
}
