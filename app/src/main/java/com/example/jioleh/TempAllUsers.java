package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TempAllUsers extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView users_list;
    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_all_users);
        initialiseToolbar();
        initialise();
        setUpRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }

    private void initialise() {
        users_list = findViewById(R.id.rvUsersList);
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("users");
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTempTopBar);
        toolbar.setTitle("TempAllUsers");
        setSupportActionBar(toolbar);
    }

    private void setUpRecyclerView() {

        Query query = reference.orderBy("username", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<UserProfile> options
                = new FirestoreRecyclerOptions.Builder<UserProfile>()
                .setQuery(query, UserProfile.class)
                .build();

        adapter = new UsersAdapter(options);

        users_list.setHasFixedSize(true);
        users_list.setLayoutManager(new LinearLayoutManager(this));
        users_list.setAdapter(adapter);
    }
}
