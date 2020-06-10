package com.example.jioleh.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button sign_in;
    private Button sign_up;

    private FirebaseAuth database = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = database.getCurrentUser();


    @Override
    protected void onStart() {
        super.onStart();

        //Auto-Login
        //10/6 buggy interaction when deleting account from firebase eg. if i sign in as account A
        //then close the app and delete account A in firebase then i still can auto login but keeps crashing
        if (currentUser != null) {
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
