package com.example.jioleh.listings;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.chat.MessageChat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class JioActivityRepository {

    private databaseOperations databaseOperations;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference jioActivityColRef = firebaseFirestore.collection("activities");

    //List to store activities
    private List<JioActivity> allActivities = new ArrayList<>();

    //Pagination
    private DocumentSnapshot lastVisible = null;
    private int limit = 20;

    //private MutableLiveData<List<JioActivity>> Activities = new MutableLiveData<>();

    JioActivityRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
    }

    public void getJioActivityData() {
        jioActivityColRef
                .whereEqualTo("expired", false)// only show activities that are not expired on home page
                .whereEqualTo("cancelled", false) //only show activities that are not cancelled(this includes activities that are confirmed)
                .orderBy("time_created", Query.Direction.DESCENDING)// order by time activity is created
                .limit(limit)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            //List of documents
                            List<DocumentSnapshot> lst = queryDocumentSnapshots.getDocuments();
                            allActivities.clear();

                            //copy documents to JioActivity Object and store in list we created
                            for (DocumentSnapshot doc : lst) {
                                JioActivity jio = null;
                                if (doc.exists()) {
                                    jio = doc.toObject(JioActivity.class);
                                    allActivities.add(jio);
                                }
                            }

                    /*put into MutableLiveData
                    ** this is actually redundant coz viewModel will change it into a mutableLiveData
                    Activities.setValue(allActivities);
                     */
                            // order by time activity is created
                            //pass into viewModel
                            databaseOperations.jioActivityDataAdded(allActivities);
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });
    }

    //Pagination
    public void getMoreActivities() {
        if (lastVisible != null) {
            jioActivityColRef
                    .whereEqualTo("expired", false)// only show activities that are not expired on home page
                    .whereEqualTo("cancelled", false) //only show activities that are not cancelled(this includes activities that are confirmed)
                    .orderBy("time_created", Query.Direction.DESCENDING)// order by time activity is created
                    .startAfter(lastVisible)
                    .limit(limit)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<JioActivity> new_input = queryDocumentSnapshots.toObjects(JioActivity.class);
                            allActivities.addAll(new_input);

                            databaseOperations.jioActivityDataAdded(allActivities);

                            if (queryDocumentSnapshots.size() - 1 < 0) {
                                lastVisible = null;
                            } else {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }
                        }
                    });
        }
    }
}
