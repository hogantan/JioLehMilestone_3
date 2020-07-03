package com.example.jioleh.userprofile;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class userProfileRepository{

    private databaseOperations databaseOperations;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<JioActivity> list_of_activities = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_tasks = new ArrayList<>();

    //used by UserProfileViewModel
    public userProfileRepository(){
    }

    //used by UserProfileListingViewModel
    public userProfileRepository(databaseOperations databaseOperations){
        this.databaseOperations = databaseOperations;
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

    public void getActivities(String current_uid) {

        firebaseFirestore.collection("users")
                .document(current_uid)
                .collection("activities_listed")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        list_of_tasks.clear();
                        list_of_activities.clear();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                list_of_tasks.add(getActivity(documentSnapshot.getId()));
                            }
                        }

                        Tasks.whenAllSuccess(list_of_tasks).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0; i < list_of_tasks.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    JioActivity jioActivity = snapshot.toObject(JioActivity.class);
                                    if(jioActivity!=null) {
                                        list_of_activities.add(jioActivity);
                                    }
                                }

                                //Arranges activities based on actual event date and time to show user the most upcoming events
                                Collections.sort(list_of_activities, new Comparator<JioActivity>() {
                                    @Override
                                    public int compare(JioActivity o1, JioActivity o2) {
                                        return o1.getEvent_timestamp().compareTo(o2.getEvent_timestamp());
                                    }
                                });
                                databaseOperations.activitiesDataAdded(list_of_activities);
                            }
                        });
                    }
                });
    }

    private Task<DocumentSnapshot> getActivity(String id) {
        return firebaseFirestore.collection("activities").document(id).get();
    }
}
