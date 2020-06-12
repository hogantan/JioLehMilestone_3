package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jioleh.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserProfileReviewsFragment extends Fragment {
    private  String uid;
    private List<Review> lst;

    public UserProfileReviewsFragment() {
        // Required empty public constructor
    }

    public UserProfileReviewsFragment(String uid) {
        // Required empty public constructor
        this.uid = uid;
    }


    RecyclerView Rv_Review;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile_reviews, container, false);

        Rv_Review = view.findViewById(R.id.review_recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initRv();
    }

    public void initRv() {
        Rv_Review.setLayoutManager(new LinearLayoutManager(this.getContext()));

        FirebaseFirestore.getInstance().collection("users")
                .document(this.uid)
                .collection("Reviews")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){

                        } else if(queryDocumentSnapshots !=null){
                            lst = new ArrayList<>();
                            List<DocumentSnapshot> lst1 = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot dc: lst1) {
                                Review review = dc.toObject(Review.class);
                                if(review!=null) {
                                    lst.add(review);
                                }
                            }
                        }
                    }
                });
        ReviewAdapter reviewAdapter = new ReviewAdapter(lst);
        Rv_Review.setAdapter(reviewAdapter);

    }
}
