package com.example.jioleh.listings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessageAdapter;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

//View participants is kept in another activity rather than to put it in ViewJioActivity
//because this saves the number of reads from firestore also to not clutter ViewJioActivity
public class ViewParticipants extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvListParticipants;
    private ViewParticipantsAdapter adapter;

    private FirebaseUser currentUser;
    private FirebaseFirestore datastore;

    private Intent intent;
    private String activity_id;

    //Used to manage locating of participants in datastore
    private ArrayList<UserProfile> list_of_participants = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participants);
        initialise();
        initialiseToolbar();
        initialiseRecyclerView();
        getParticipants();
    }

    private void getParticipants() {
        datastore.collection("activities")
                .document(activity_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        JioActivity currentActivity = documentSnapshot.toObject(JioActivity.class);

                        final ArrayList<String> list_of_uid = currentActivity.getParticipants();

                        //adding completable futures into list of tasks
                        for (String uid : list_of_uid) {
                            list_of_tasks.add(getUserProfile(uid));
                        }

                        //Waiting for completable futures to finish
                        Tasks.whenAllSuccess(list_of_tasks).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0 ; i < list_of_tasks.size() ; i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    UserProfile userProfile = snapshot.toObject(UserProfile.class);
                                    list_of_participants.add(userProfile);
                                }
                                adapter.setData(list_of_participants, list_of_uid);
                            }
                        });
                    }
                });
    }

    private Task<DocumentSnapshot> getUserProfile(String uid) {
        return datastore.collection("users")
                .document(uid)
                .get();
    }

    private void initialise() {
        rvListParticipants = findViewById(R.id.rvListOfParticipants);
        datastore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("Participants");
        toolbar.setTitleTextColor(getResources().getColor(R.color.baseGreen));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiseRecyclerView() {
        adapter = new ViewParticipantsAdapter();
        rvListParticipants = findViewById(R.id.rvListOfParticipants);
        rvListParticipants.setHasFixedSize(true);
        rvListParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvListParticipants.setAdapter(adapter);
    }
}