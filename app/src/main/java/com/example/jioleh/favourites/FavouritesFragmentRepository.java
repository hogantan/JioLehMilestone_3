package com.example.jioleh.favourites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
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

public class FavouritesFragmentRepository {

    private databaseOperations databaseOperations;

    private FirebaseFirestore datastore = FirebaseFirestore.getInstance();
    private ArrayList<JioActivity> list_of_activities = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_tasks = new ArrayList<>();

    public FavouritesFragmentRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
    }

    //To locate the activities that the user has joined
    public void getActivities(String current_uid, String type) {
        datastore.collection("users")
                .document(current_uid)
                .collection(type)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //listens to joined for changes which will only occur if there is a change in activities collection which is prompted by ViewJioActivity Listener
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();

                        list_of_tasks.clear();
                        list_of_activities.clear();

                        //Adding a list of completable futures
                        System.out.println("snapshots size = " + snapshots.size());
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            if (documentSnapshot.exists()) {
                                list_of_tasks.add(getActivity(documentSnapshot.getId(), current_uid, type));
                            }
                        }

                        //Waiting for completable futures to complete
                        Tasks.whenAllSuccess(list_of_tasks).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {

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

    public void refreshActivities(String current_uid, String type) {
        datastore.collection("users")
                .document(current_uid)
                .collection(type)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();

                        list_of_tasks.clear();
                        list_of_activities.clear();

                        //Adding a list of completable futures
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            if (documentSnapshot.exists()) {
                                list_of_tasks.add(getActivity(documentSnapshot.getId(), current_uid, type));
                            } else {
                                //remove from joined/liked
                            }
                        }

                        //Waiting for completable futures to complete
                        Tasks.whenAllSuccess(list_of_tasks).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {

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

    //Get a completable future task
    private Task<DocumentSnapshot> getActivity(final String activity_uid, final String user_uid, String type) {
        return datastore.collection("activities")
                .document(activity_uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            list_of_activities.add(documentSnapshot.toObject(JioActivity.class));
                        } else {
                            //removes from current user joined activities
                            datastore.collection("users")
                                    .document(user_uid)
                                    .collection(type)
                                    .document(activity_uid)
                                    .delete();
                        }
                    }
                });
    }
}
