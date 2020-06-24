package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jioleh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class ReviewPage extends AppCompatActivity {

    private TextView tv_username_display;
    private RatingBar ratingBar;
    private EditText review_words;
    private Button btn_submit_review;
    private Toolbar tb_user_profile;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    userProfileViewModel viewModel;

    String username;
    String profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);
        initialise();
        initTb();

        Intent intent = getIntent();
        String other_username = intent.getStringExtra("username");
        String other_userId = intent.getStringExtra("user_id");
        tv_username_display.setText(other_username);

      viewModel = new ViewModelProvider(this).get(userProfileViewModel.class);

        fetchCurrentUserDetails(firebaseUser.getUid()).observe(ReviewPage.this, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                fill(userProfile);
            }
        });


        final DocumentReference reviewDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(other_userId)
                .collection("Reviews")
                .document();

        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                String reviewWord = review_words.getText().toString();
                String currentUid = firebaseUser.getUid();

                Review review = new Review(reviewWord, currentUid,profilePic,username,rating);
                review.setDocumentId(reviewDocRef.getId());

                reviewDocRef.set(review)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(ReviewPage.this, "Review submitted!",Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ReviewPage.this, "Error please try submitting again!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void fill(UserProfile userProfile) {
        username = userProfile.getUsername();
        profilePic = userProfile.getImageUrl();
    }

    public void initialise() {
        tv_username_display = findViewById(R.id.review_username);
        ratingBar = findViewById(R.id.review_rating_bar);
        review_words = findViewById(R.id.review_words);
        btn_submit_review = findViewById(R.id.review_submit);
        tb_user_profile = findViewById(R.id.review_top_bar);
    }

    public void initTb() {
        tb_user_profile.setTitle("Review");
        tb_user_profile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public LiveData<UserProfile> fetchCurrentUserDetails(String currentUserId) {
        return viewModel.getUser(currentUserId);

    }
}
