package com.example.jioleh.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockedUsersScreen extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private BlockedUsersAdapter mAdapter;
    private List<Map<String, Object>> myDataset;
    private List<String> myUserId;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users_screen);
        initialiseTb();

        recyclerView = findViewById(R.id.blocked_users_rv);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getBlockedUsers();
    }

    private void getBlockedUsers() {
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUID)
                .collection("blocked users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        myDataset = new ArrayList<>();
                        myUserId = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()) {
                            if(documentSnapshot.exists()) {
                                Map<String, Object> user = documentSnapshot.getData();
                                myDataset.add(user);
                                myUserId.add(documentSnapshot.getId());
                            }
                        }
                        mAdapter = new BlockedUsersAdapter();
                        mAdapter.setData(myDataset,myUserId);

                        recyclerView.setAdapter(mAdapter);
                    }
                });
    }

    private void initialiseTb() {
        toolbar = findViewById(R.id.blocked_users_top_bar);
        toolbar.setTitle("Blocked Users");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
