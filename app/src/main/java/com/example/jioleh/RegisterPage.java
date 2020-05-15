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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button register;
    private TextView login;
    private FirebaseAuth database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        initialise();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserInputDetails()) {
                    String user_email = email.getText().toString();
                    String user_password = password.getText().toString();
                    database.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent nextActivity = new Intent(RegisterPage.this, MainActivity.class);
                                startActivity(nextActivity);
                            } else {
                                Toast.makeText(RegisterPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(nextActivity);
            }
        });
    }

    private void initialise() {
        email = findViewById(R.id.etEmailRegister);
        password = findViewById(R.id.etPasswordRegister);
        register = findViewById(R.id.btnRegister);
        login = findViewById(R.id.tvAlreadyHaveAcc);
        database = FirebaseAuth.getInstance();
    }

    private boolean checkUserInputDetails() {
        String inputPassword = password.getText().toString();
        String inputEmail = email.getText().toString();

        if (inputPassword.isEmpty() || inputEmail.isEmpty()) {
            Toast.makeText(RegisterPage.this, "Please fill up all the required fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
