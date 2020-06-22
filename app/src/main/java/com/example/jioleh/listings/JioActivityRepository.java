package com.example.jioleh.listings;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

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
import java.util.Date;
import java.util.List;

class JioActivityRepository {

    private databaseOperations databaseOperations;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference jioActivityColRef = firebaseFirestore.collection("activities");
    //private MutableLiveData<List<JioActivity>> Activities = new MutableLiveData<>();

    JioActivityRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;

    }

    public void getJioActivityData() {
        checkActivityExpiry(); //first line of expiry check
        jioActivityColRef
                .whereEqualTo("expired", false)// only show activities that are not expired on home page
                .orderBy("time_created", Query.Direction.DESCENDING)// order by time activity is created
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //handle exceptions
                            databaseOperations.onError(e);
                        }
                        if (queryDocumentSnapshots != null) {
                            //new list to store activities
                            List<JioActivity> allActivities = new ArrayList<>();

                            //List of documents
                            List<DocumentSnapshot> lst = queryDocumentSnapshots.getDocuments();

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

                            //pass into viewModel
                            databaseOperations.jioActivityDataAdded(allActivities);
                        }
                    }
                });
    }

    //An expired event can still be viewed in joined and liked but not on Home fragment, this is to
    //make the latest activities make more sense
    //Checking of an activity goes in three lines:
    //First line : when user opens the app which runs the method below.
    //Second line : when users selects an activity, it will check the expiry with current time\
    //Third line : a scroll refresh at the latest activities and favourites fragment screen as well as when a search is done that is used by users
    //This ensures that the interfaces that are used by users most often triggers to expiry check
    //Possible Issues : Expiry are not recorded exactly, which could lead to pollution of activities that are expired but not recorded, this is under the assumption that users starting the app is low and that nobody i clicking activivties
    //A possible way to rectify this is to implement the refresh function for users, although this might mean higher number of expiry checks, it still does not gurantee activities are expired at exact time.
    //To make a query run every 5 second on the background of our app is too expensive for users and our database in terms of reads.
    //One way to resolve this is to use some cloud function that will trigger on the exact expiry date and time, however the problem with this is that, this triggers have a limit themselves. For example,
    //Cloud Tasks(a firebase app that allows such scheduling) only allows for scheduled events of maximum 30 days and that there is a maximum number of requests possible as well, this is not to mention that
    //Cloud Tasks itself is a whole different aspect and enw thing to learn.
    //With the many constraints in mind, the three lines of check should suffice in our case since we do not have any servers whatsoever to handle future requests such as expiry od data
    //First line of check when user opens the application
     public void checkActivityExpiry() {
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time

        jioActivityColRef.whereLessThan("event_timestamp", currentDateTime)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot: list_of_documents) {
                            jioActivityColRef.document(documentSnapshot.getId())
                                    .update("expired", true);
                        }
                    }
                });
    }
}
