package com.example.jioleh.userprofile;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    private ReviewDataOps databaseOperations;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public ReviewRepository(){
    }

    public ReviewRepository(ReviewDataOps databaseOperations){
        this.databaseOperations = databaseOperations;
    }

    public void getReviews(String uid) {
        firebaseFirestore.collection("users")
                .document(uid)
                .collection("Reviews")
                .orderBy("timeOfPost", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //handle exceptions
                        }
                        List<Review> listOfReviews = new ArrayList<>();
                        if (queryDocumentSnapshots != null) {

                            List<DocumentSnapshot> lst = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot doc : lst) {
                                if (doc.exists()) {
                                    Review review = doc.toObject(Review.class);
                                    listOfReviews.add(review);
                                }
                            }


                        }
                        databaseOperations.reviewsAdded(listOfReviews);
                    }
                });

    }

}
