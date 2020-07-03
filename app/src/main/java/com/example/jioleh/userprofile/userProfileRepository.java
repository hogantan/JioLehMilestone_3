package com.example.jioleh.userprofile;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class userProfileRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    userProfileRepository(){

    }

    public LiveData<UserProfile> getUser(String uid) {
        final MutableLiveData<UserProfile> userLiveData = new MutableLiveData<>();
        firebaseFirestore.collection("users")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot!=null && documentSnapshot.exists()) {
                            userLiveData.setValue(documentSnapshot.toObject(UserProfile.class));
                        } else if (e != null) {
                            //handle exceptions
                        }
                    }
                });
        return userLiveData;
    }
}
