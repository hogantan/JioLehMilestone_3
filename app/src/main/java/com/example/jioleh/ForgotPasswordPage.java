package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

// Note: Resetting password overrules email verification since the password
// reset procedure is sent to the user's email. In this sense, the user
// has already verified himself if he has not done so.
// e.g. Registering for an account and not verifying but resetting password would
// mean users do not have to click any verification confirmation to verify their account.
public class ForgotPasswordPage extends AppCompatActivity {

    TextInputLayout user_email;
    Button reset_password;

    private ProgressDialog progressBar;

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

                    //Setting Details of Loading Screen
                    progressBar.setTitle("Loading");
                    progressBar.setMessage("Contacting with server");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();

                    database.sendPasswordResetEmail(user_email.getEditText().getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.dismiss();
                                    openResetDialog();
                                } else {
                                    progressBar.dismiss();
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
        initialiseActionBar();
        progressBar = new ProgressDialog(ForgotPasswordPage.this);
        user_email = findViewById(R.id.tilForgotPasswordEmail);
        reset_password = findViewById(R.id.btnResetPassword);
        database = FirebaseAuth.getInstance();
    }

    //Action Bar Settings
    private void initialiseActionBar() {
        ActionBar top_bar = getSupportActionBar();

        //Setting background colour
        ColorDrawable light_green = new ColorDrawable(Color.parseColor("#00ffce"));
        top_bar.setBackgroundDrawable(light_green);

        //Setting Title text
        top_bar.setTitle(Html.fromHtml("<font color='#202124'>Password Reset </font>"));

        //Setting Top left logo
        final Drawable upArrow =  ContextCompat.getDrawable(ForgotPasswordPage.this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(ForgotPasswordPage.this, R.color.baseBlack), PorterDuff.Mode.SRC_ATOP);
        ForgotPasswordPage.this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    // Pop-up window when reset button is pressed
    private void openResetDialog() {
        ResetPasswordConfirmationDialog popupDialog = new ResetPasswordConfirmationDialog();
        popupDialog.show(getSupportFragmentManager(), "popupDialog");
    }

    private boolean checkUserInputDetails() {
        String inputEmail = user_email.getEditText().getText().toString();
        if (inputEmail.isEmpty()) {
            Toast.makeText(ForgotPasswordPage.this, "Please fill up all the required fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}