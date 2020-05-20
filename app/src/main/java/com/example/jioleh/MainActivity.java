package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private TextView register;
    private TextView forgotpassword;
    private FirebaseAuth database;
    private FirebaseFirestore fireStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser(email.getText().toString(), password.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(MainActivity.this, RegisterPage.class);
                startActivity(nextActivity);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(MainActivity.this, ForgotPasswordPage.class);
                startActivity(nextActivity);
            }
        });
    }

    private void initialise() {
        email = findViewById(R.id.etEmailAddress);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.tvRegister);
        forgotpassword = findViewById(R.id.tvForgotPassword);
        database = FirebaseAuth.getInstance();
    }

    private void checkUser(String email, String password) {
        database.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    checkEmailVerification();
                } else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                    Toast.makeText(MainActivity.this, "User does not exist",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Email address or password is incorrect",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkEmailVerification() {
        FirebaseUser currentUser = database.getCurrentUser();
        boolean isVerified = currentUser.isEmailVerified();
        String userID = currentUser.getUid();
        if (isVerified) {
                MainActivity.this.finish();
                startActivity(new Intent(MainActivity.this, PostLoginPage.class));
        } else {
            Toast.makeText(MainActivity.this,
                    "Account is not verified, please check email",
                    Toast.LENGTH_SHORT).show();
            database.signOut(); //sign out unverified user
        }
    }




}
