package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Note: Resetting password overrules email verification since the password
// reset procedure is sent to the user's email. In this sense, the user
// has already verified himself if he has not done so.
// e.g. Registering for an account and not verifying but resetting password would
// mean users do not have to click any verification confirmation to verify their account.
public class ForgotPasswordPage extends AppCompatActivity {
    EditText user_email;
    Button reset_password;
    FirebaseAuth database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);
        initialise();
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserInputDetails()) {
                    database.sendPasswordResetEmail(user_email.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    openResetDialog();
                                } else {
                                    Toast.makeText(ForgotPasswordPage.this,
                                            "Email address has not been registered",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            }
        });
    }

    private void initialise() {
        user_email = findViewById(R.id.etEmailUserForgot);
        reset_password = findViewById(R.id.btnResetPassword);
        database = FirebaseAuth.getInstance();
    }

    // Pop-up window when reset button is pressed
    private void openResetDialog() {
        ResetPasswordConfirmationDialog popupDialog = new ResetPasswordConfirmationDialog();
        popupDialog.show(getSupportFragmentManager(), "popupDialog");
    }

    private boolean checkUserInputDetails() {
        String inputEmail = user_email.getText().toString();
        if (inputEmail.isEmpty()) {
            Toast.makeText(ForgotPasswordPage.this, "Please fill up all the required fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
