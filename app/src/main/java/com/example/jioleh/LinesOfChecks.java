package com.example.jioleh;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LinesOfChecks {

    public LinesOfChecks(){
    }

    //An expired event can still be viewed in joined and liked but not on Home fragment, this is to
    //make the latest activities make more sense
    //Checking of an activity goes in three lines:
    //First line : when user opens the app which runs the method below. (refer to HomeFragment)
    //Second line : when users selects an activity, it will check the expiry with current time (refer to ViewJioActivity)
    //Third line : a scroll refresh at the latest activities and favourites fragment screen as well as when a search is done that is used by users (refer to HomeFragment, JoinedFragment and LikedFragment)
    //This ensures that the interfaces that are used by users most often triggers to expiry check
    //Fourth line: when user attempts to join or leave an activity (refer to ViewJioActivity)
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
        CollectionReference jioActivityColRef = FirebaseFirestore.getInstance().collection("activities");

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

    //A cancelled event would mean that an activity has failed to acquire the minimum number of participants required by the confirmation deadline
    //A cancelled event would mean that the event would not accept anymore participants joining and leaving in order words the activity goes into an inactive state.
    //On the other hand, a confirmed event would refer to an event that is confirmed.
    //A confirmed event would mean that the event has succeeded in acquire the minimum number of participants required by the confirmation deadline.
    //Similarly, a confirmed event would not accept anymore participants joining or leaving
    //To Note: there are two boolean fields in a JioActivity, confirmed and cancelled(This is because a non cancelled event does not mean a confirmed event, activity is of a neutral state before the deadline)
    //In other words, an activity cannot have both confirmed and cancelled to be true but can have both confirmed and cancelled to be false(neutral state)
    //The main difference between cancelled events and confirmed events is that a confirmed event will be shown on the latest activities list on the Home Fragment unlike a cancelled event.
    //This is because a confirmed event might not have the optimum number of participants required, for example a basketball game 4v4 which requires minimum 6 to at least run a 3v3, but on the confirmation
    //deadline 7 people confirmed in participating. This would lead to an unbalance in team sizes, by allowing such confirmed activities being shown on the latest activities list, it still allows the host
    //to communicate to other possible participants via the about details section of ViewJioActivity. Alternatively, the host could post another activity to look for more participants.
    public void checkActivityCancelledConfirmed() {
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time
        CollectionReference jioActivityColRef = FirebaseFirestore.getInstance().collection("activities");

        jioActivityColRef.whereLessThan("deadline_timestamp", currentDateTime)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot: list_of_documents) {
                            int minimum = Integer.parseInt(documentSnapshot.get("min_participants").toString());
                            int current = Integer.parseInt(documentSnapshot.get("current_participants").toString());
                            if (current < minimum) {
                                jioActivityColRef.document(documentSnapshot.getId())
                                        .update("cancelled", true);
                            } else {
                                jioActivityColRef.document(documentSnapshot.getId())
                                        .update("confirmed", true);
                            }
                        }
                    }
                });
    }
}
