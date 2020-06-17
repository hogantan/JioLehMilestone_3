package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.jioleh.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserProfileReviewsFragment extends Fragment {
    private String uid;
    private List<Review> lst;
    private float avg;

    public UserProfileReviewsFragment() {
        // Required empty public constructor
    }

    public UserProfileReviewsFragment(String uid) {
        this.uid = uid;
    }


    RecyclerView Rv_Review;
    RatingBar rb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile_reviews, container, false);

        Rv_Review = view.findViewById(R.id.review_recyclerView);
        initRv();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void initRv() {
        Rv_Review.setHasFixedSize(true);
        Rv_Review.setLayoutManager(new LinearLayoutManager(this.getContext()));
        lst = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users")
                .document(this.uid)
                .collection("Reviews")
                .orderBy("timeOfPost", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot dc : task.getResult()) {
                                Review review = dc.toObject(Review.class);
                                if (review != null) {
                                    lst.add(review);
                                }
                            }

                        }
                    }
                })
                .continueWithTask(new Continuation<QuerySnapshot, Task<Review>>() {
                    @Override
                    public Task<Review> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        ReviewAdapter reviewAdapter = new ReviewAdapter();
                        reviewAdapter.setData(lst);
                        Rv_Review.setAdapter(reviewAdapter);
                        return null;
                    }
                });


    }


}
