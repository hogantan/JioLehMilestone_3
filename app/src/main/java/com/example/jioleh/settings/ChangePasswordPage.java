package com.example.jioleh.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordPage extends AppCompatActivity {

    private TextInputLayout old_password;
    private TextInputLayout new_password;
    private TextInputLayout confirm_new_password;
    private Button change_password;
    private Toolbar toolbar;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_page);

        initialise();

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String p_old = old_password.getEditText().getText().toString().trim();
                final String p_new = new_password.getEditText().getText().toString().trim();

                final String p_new_confirm = confirm_new_password.getEditText().getText().toString().trim();
                if (emptyFields(p_old, p_new, p_new_confirm)) {
                    Toast.makeText(ChangePasswordPage.this,
                            "Please fill in all fields",
                            Toast.LENGTH_SHORT).show();
                } else if (noMatch(p_new, p_new_confirm)) {
                    Toast.makeText(ChangePasswordPage.this,
                            "Please ensure that both new passwords are the same",
                            Toast.LENGTH_SHORT).show();
                } else if (tooShort(p_new, p_new_confirm)){
                    Toast.makeText(ChangePasswordPage.this,
                            "New password is too short",
                            Toast.LENGTH_SHORT).show();
                } else {
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), p_old);

                    //Setting Details of Loading Screen
                    progressBar.setTitle("Changing Password");
                    progressBar.setMessage("Please wait while we change your password");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();

                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                currentUser.updatePassword(p_new).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.dismiss();
                                            Toast.makeText(ChangePasswordPage.this,
                                                    "Password changed successfully",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent nextActivity = new Intent(ChangePasswordPage.this, PostLoginPage.class);
                                            nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(nextActivity);
                                            finish();
                                        } else {
                                            progressBar.dismiss();
                                            Toast.makeText(ChangePasswordPage.this,
                                                    "Password change failed, please contact administrator",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                progressBar.dismiss();
                                Toast.makeText(ChangePasswordPage.this,
                                        "Old password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initialise() {
        initialiseToolbar();
        progressBar = new ProgressDialog(ChangePasswordPage.this);
        old_password = findViewById(R.id.tilChangeOldPassword);
        new_password = findViewById(R.id.tilChangeNewPassword);
        confirm_new_password = findViewById(R.id.tilChangeConfirmNewPassword);
        change_password = findViewById(R.id.btnConfirmChangePassword);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTempTopBar);
        toolbar.setTitle("Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean emptyFields(String s1, String s2, String s3) {
        return s1.isEmpty() || s2.isEmpty() || s3.isEmpty();
    }

    private boolean noMatch(String s1, String s2) {
        return !s1.equals(s2);
    }

    private boolean tooShort(String s1, String s2) {
        return s1.length() < 6 || s2.length() < 6;
    }
}
