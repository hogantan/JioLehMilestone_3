package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout username;
    private TextInputLayout password;
    private Button register;
    private Toolbar toolbar;

    private ProgressDialog progressBar;

    private FirebaseAuth database;
    private FirebaseFirestore fireStore;
    private boolean isNewUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        initialise();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = username.getEditText().getText().toString();
                if (checkUserInputDetails()) {
                    String user_email = email.getEditText().getText().toString().trim();
                    String user_password = password.getEditText().getText().toString().trim();
                    database.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final String userID = database.getCurrentUser().getUid();
                                        DocumentReference documentReference = fireStore.collection("users").document(userID);
                                        UserProfile user = new UserProfile(userName, isNewUser);
                                        documentReference.set(user)
                                                .onSuccessTask(new SuccessContinuation<Void, Void>() {
                                            @NonNull
                                            @Override
                                            public Task<Void> then(@Nullable Void aVoid) throws Exception {
                                                sendEmailVerification();
                                                return null;
                                            }
                                        });

                                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException){
                                        Toast.makeText(RegisterPage.this,
                                                "Registration failed, password must have more than 6 characters",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterPage.this,
                                                "Registration failed, please contact administrator",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    }
                                });
                    }
                }
            });
    }

    private void initialise() {
        initialiseToolbar();
        progressBar = new ProgressDialog(RegisterPage.this);
        email = findViewById(R.id.tilRegisterEmail);
        username = findViewById(R.id.tilRegisterUsername);
        password = findViewById(R.id.tilRegisterPassword);
        register = findViewById(R.id.btnRegister);
        database = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbRegister);
        toolbar.setTitle("Register Account");
        setSupportActionBar(toolbar);
    }

    private boolean checkUserInputDetails() {
        String inputEmail = email.getEditText().getText().toString();
        String inputUsername = username.getEditText().getText().toString();
        String inputPassword = password.getEditText().getText().toString();
        if (inputPassword.isEmpty() || inputEmail.isEmpty()) {
            Toast.makeText(RegisterPage.this, "Please fill up all the required fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void sendEmailVerification() {
        FirebaseUser currentUser = database.getCurrentUser();

        //Setting Details of Loading Screen
        progressBar.setTitle("Creating Account");
        progressBar.setMessage("Please wait while we register your account ");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.dismiss();
                    openVerificationDialog();
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    progressBar.dismiss();
                    Toast.makeText(RegisterPage.this,
                            "Registration failed, email address is already registered",
                            Toast.LENGTH_LONG).show();
                } else {
                    progressBar.dismiss();
                    Toast.makeText(RegisterPage.this,
                            "Registration failed, please contact administrator",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Pop-up window when user successfully registers for an account
    // Refer to VerificationEmailSentDialog class
    private void openVerificationDialog() {
        database.signOut();
        VerificationEmailSentDialog popupDialog = new VerificationEmailSentDialog();
        popupDialog.show(getSupportFragmentManager(), "popupDialog");
    }
}
