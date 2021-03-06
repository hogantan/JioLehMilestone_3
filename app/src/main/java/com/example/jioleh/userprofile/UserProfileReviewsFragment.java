package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivityViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    public UserProfileReviewsFragment() {
        // Required empty public constructor
    }

    public UserProfileReviewsFragment(String uid) {
        this.uid = uid;
    }

    private RecyclerView Rv_Review;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile_reviews, container, false);
        Rv_Review = view.findViewById(R.id.review_recyclerView);
        initRv();
        return view;
    }

    public void initRv() {
        Rv_Review.setHasFixedSize(true);
        Rv_Review.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ReviewAdapter reviewAdapter = new ReviewAdapter();
        reviewAdapter.setUid(UserProfileReviewsFragment.this.uid);
        reviewAdapter.setViewer_uid(FirebaseAuth.getInstance().getUid());
        Rv_Review.setAdapter(reviewAdapter);

        ReviewViewModel reviewVM = new ViewModelProvider(this).get(ReviewViewModel.class);
        reviewVM.getReviews(this.uid);
        reviewVM.getListOfReviews().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {

                reviewAdapter.setData(reviews);
                reviewAdapter.notifyDataSetChanged();


            }
        });
    }

}
