package com.example.jioleh.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button sign_in;
    private Button sign_up;

    private FirebaseAuth database = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = database.getCurrentUser();


    @Override
    protected void onStart() {
        super.onStart();

        //Auto-Login
        if (currentUser != null) {
            Toast.makeText(this, currentUser.getUid(), Toast.LENGTH_SHORT).show();
            Intent nextActivity = new Intent(MainActivity.this, PostLoginPage.class);
            nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(nextActivity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(MainActivity.this, LoginPage.class);
                startActivity(nextActivity);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(MainActivity.this, RegisterPage.class);
                startActivity(nextActivity);
            }
        });

    }

    private void initialise() {
        sign_in = findViewById(R.id.btnSignIn);
        sign_up = findViewById(R.id.btnNeedAAccount);
    }
}
