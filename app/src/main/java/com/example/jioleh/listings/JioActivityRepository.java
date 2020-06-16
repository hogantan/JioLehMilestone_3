package com.example.jioleh.listings;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

class JioActivityRepository {

    private databaseOperations databaseOperations;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference jioActivityColRef = firebaseFirestore.collection("activities");
    //private MutableLiveData<List<JioActivity>> Activities = new MutableLiveData<>();

    JioActivityRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;

    }


    void getJioActivityData() {
        jioActivityColRef
                .orderBy("time_created", Query.Direction.DESCENDING) // order by time activity is created
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if ( e != null) {
                    //handle exceptions
                    databaseOperations.onError(e);
                }
                if (queryDocumentSnapshots != null) {
                    //new list to store activities
                    List<JioActivity> allActivities = new ArrayList<>();

                    //List of documents
                    List<DocumentSnapshot> lst = queryDocumentSnapshots.getDocuments();

                    //copy documents to JioActivity Object and stall in list we created
                    for(DocumentSnapshot doc : lst) {
                       JioActivity jio =doc.toObject(JioActivity.class);

                       if (jio!=null){ // avoided null activities here****************
                           allActivities.add(jio);
                       }
                    }

                    /*put into MutableLiveData
                    ** this is actually redundant coz viewModel will change it into a mutableLiveData
                    Activities.setValue(allActivities);
                     */

                    //pass into viewModel
                    databaseOperations.jioActivityDataAdded(allActivities);
                }
            }
        });

    }




}
