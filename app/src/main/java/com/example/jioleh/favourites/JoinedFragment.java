package com.example.jioleh.favourites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.R;
import com.example.jioleh.listings.ActivityAdapter;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.listings.ViewParticipantsAdapter;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JoinedFragment extends Fragment {

    private View currentView;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;

    private FirebaseUser currentUser;
    private FirebaseFirestore datastore;
    private ArrayList<JioActivity> list_of_activities = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_tasks = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_joined, container, false);
        initialise();
        initialiseRecyclerView();
        return currentView;
    }

    private void initialise() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();
        emptyText = currentView.findViewById(R.id.tvFavouriteEmpty);
    }


    //Since joined or liked tabs are only updated if user goes into an ViewJioActivity, there
    //should not be any change in data when user is on this fragment
    //Thus, only need to get data when data sees this fragment which is why onStart is used
    @Override
    public void onStart() {
        super.onStart();
        //the following has to be cleared as since they are initialised onCreate which is only called once
        //an alternative can be to create a new list everytime onStart as well
        list_of_tasks.clear();
        list_of_activities.clear();
        getJoined();
    }

    //To locate the activities that the user has joined
    private void getJoined() {
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("joined")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();

                        //Adding a list of completable futures
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            list_of_tasks.add(getActivity(documentSnapshot.getId()));
                        }

                        //Waiting for completable futures to complete
                        Tasks.whenAllSuccess(list_of_tasks).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0; i < list_of_tasks.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    JioActivity jioActivity = snapshot.toObject(JioActivity.class);
                                    list_of_activities.add(jioActivity);
                                }
                                adapter.setData(list_of_activities);
                                adapter.notifyDataSetChanged();

                                //Visual text
                                if (list_of_activities.isEmpty()) {
                                    emptyText.setText("You have not join any activities!");
                                } else {
                                    emptyText.setText("");
                                }
                            }
                        });
                    }
                });
    }

    //Get a completable future task
    private Task<DocumentSnapshot> getActivity(String uid) {
        return datastore.collection("activities")
                .document(uid)
                .get();
    }

    private void initialiseRecyclerView() {
        adapter = new FavouritesAdapter();
        recyclerView = currentView.findViewById(R.id.rvFavouriteJoined);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }
}