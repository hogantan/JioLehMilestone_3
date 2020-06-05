package com.example.jioleh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HomeFragment extends Fragment {

    private EditText searchBar;
    private Button searchIcon;
    private RecyclerView activity_list;

    private FirebaseFirestore datastore;
    private ActivityAdapter adapter;
    private View currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_home,container,false);
        initialise();
        initialiseRecyclerView();


        return currentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivities();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().show();
        }
    }

    private void initialise() {
        searchBar = currentView.findViewById(R.id.etSearchActivity);
        datastore = FirebaseFirestore.getInstance();
    }

    private void getActivities() {
        datastore.collection("activities")
                .orderBy("time_created", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                        } else {
                            List<JioActivity> activities
                                    = queryDocumentSnapshots.toObjects(JioActivity.class);
                            adapter.setData(activities);
                            //activity_list.smoothScrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                });
    }

    private void initialiseRecyclerView() {
        adapter = new ActivityAdapter();
        activity_list = currentView.findViewById(R.id.rvActivityList);
        activity_list.setHasFixedSize(true);
        activity_list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        activity_list.setAdapter(adapter);
    }
}
