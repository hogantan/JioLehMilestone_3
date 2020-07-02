package com.example.jioleh.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BlockedUsersScreen extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private BlockedUsersAdapter mAdapter;
    private List<UserProfile> myDataset;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users_screen);

        recyclerView = findViewById(R.id.blocked_users_rv);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getBlockedUsers();


    }

    private void getBlockedUsers() {
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myDataset = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUID)
                .collection("blocked users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()) {
                            if(documentSnapshot.exists()) {
                                UserProfile user = documentSnapshot.toObject(UserProfile.class);
                                myDataset.add(user);
                            }
                        }
                        mAdapter = new BlockedUsersAdapter();
                        mAdapter.setData(myDataset);

                        recyclerView.setAdapter(mAdapter);
                    }
                });
    }
}
