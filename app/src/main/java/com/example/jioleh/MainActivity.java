package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
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

    private Button sign_in;
    private Button sign_up;

    private FirebaseAuth database = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = database.getCurrentUser();

    @Override
    protected void onStart() {
        super.onStart();

        //Auto-Login
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
