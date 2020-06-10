package com.example.jioleh.favourites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.R;
import com.example.jioleh.listings.ActivityAdapter;
import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

//Exactly the same as joined fragment just that collection directing different
//basically any "joined" is change into "liked"
public class LikedFragment extends Fragment {

    private View currentView;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    private FirebaseUser currentUser;
    private FirebaseFirestore datastore;
    private ArrayList<JioActivity> list_of_activities = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_tasks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_liked, container, false);
        initialise();
        initialiseRecyclerView();
        return currentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        list_of_tasks.clear();
        list_of_activities.clear();
        getLiked();
    }

    private void initialise() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();
        emptyText = currentView.findViewById(R.id.tvFavouriteEmpty);
    }

    private void getLiked() {
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("liked")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            list_of_tasks.add(getActivity(documentSnapshot.getId()));
                        }

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
                                if (list_of_activities.isEmpty()) {
                                    emptyText.setText("You have not like any activities!");
                                } else {
                                    emptyText.setText("");
                                }
                            }
                        });
                    }
                });
    }

    private Task<DocumentSnapshot> getActivity(String uid) {
        return datastore.collection("activities")
                .document(uid)
                .get();
    }

    private void initialiseRecyclerView() {
        adapter = new ActivityAdapter();
        recyclerView = currentView.findViewById(R.id.rvFavouriteLiked);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}