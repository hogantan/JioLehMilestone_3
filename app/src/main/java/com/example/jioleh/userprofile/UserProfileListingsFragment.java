package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.R;
import com.example.jioleh.favourites.FavouritesAdapter;
import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class UserProfileListingsFragment extends Fragment {
    private View currentView;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;

    private FirebaseFirestore datastore;
    private ArrayList<JioActivity> list_of_activities = new ArrayList<>();
    private List<String> list_of_id = new ArrayList<>();

    private List<Task<DocumentSnapshot>> lst = new ArrayList<>();


    private String uid;

    public UserProfileListingsFragment() {
        // Required empty public constructor
    }

    public UserProfileListingsFragment(String uid){
        this.uid = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView= inflater.inflate(R.layout.fragment_user_profile_listings, container, false);
        initialise();
        initialiseRecyclerView();
        getHostingActivities();
        return currentView;
    }

    private void initialise() {
        datastore = FirebaseFirestore.getInstance();
    }

    private void initialiseRecyclerView() {
        adapter = new FavouritesAdapter();
        recyclerView = currentView.findViewById(R.id.userProfile_listings_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void getHostingActivities() {

        datastore.collection("users")
                .document(uid)
                .collection("activities_listed")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot != null) {
                                lst.add(add(documentSnapshot.getId()));
                            }
                        }


                        Tasks.whenAllSuccess(lst).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0; i < lst.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    JioActivity jioActivity = snapshot.toObject(JioActivity.class);
                                    if(jioActivity!=null) {
                                        list_of_activities.add(jioActivity);
                                    }
                                }
                                adapter.setData(list_of_activities);


                            }
                        });
                    }
                });


    }


    private Task<DocumentSnapshot> add(String id) {
        return datastore.collection("activities").document(id).get();



    }
}

