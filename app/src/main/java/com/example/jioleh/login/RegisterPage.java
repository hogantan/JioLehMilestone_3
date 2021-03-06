package com.example.jioleh.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterPage extends AppCompatActivity {

    private TextInputLayout email;
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

                //Setting Details of Loading Screen
                progressBar.setTitle("Creating Account");
                progressBar.setMessage("Please wait while we register your account ");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();

                if (checkUserInputDetails()) {
                    String user_email = email.getEditText().getText().toString();
                    String user_password = password.getEditText().getText().toString();
                    database.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final String userID = database.getCurrentUser().getUid();
                                        DocumentReference documentReference = fireStore.collection("users").document(userID);
                                        UserProfile user = new UserProfile(isNewUser);
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
                                        progressBar.dismiss();
                                        Toast.makeText(RegisterPage.this,
                                                "Registration failed, password must have more than 6 characters",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.dismiss();
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
        password = findViewById(R.id.tilRegisterPassword);
        register = findViewById(R.id.btnRegister);
        database = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTempTopBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private boolean checkUserInputDetails() {
        String inputEmail = email.getEditText().getText().toString();
        String inputPassword = password.getEditText().getText().toString();
        if (inputPassword.isEmpty() || inputEmail.isEmpty()) {
            progressBar.dismiss();
            Toast.makeText(RegisterPage.this, "Please fill up all the required fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void sendEmailVerification() {
        FirebaseUser currentUser = database.getCurrentUser();

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
