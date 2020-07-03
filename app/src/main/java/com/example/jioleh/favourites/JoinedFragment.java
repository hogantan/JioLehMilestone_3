package com.example.jioleh.favourites;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> parent of ca2abdd... 3/7
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
import com.mapbox.mapboxsdk.plugins.annotation.Line;
>>>>>>> parent of 1e5a48b... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
=======
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
=======
import com.mapbox.mapboxsdk.plugins.annotation.Line;
>>>>>>> parent of 6761555... Merge pull request #56 from hogantan/master
=======
import com.mapbox.mapboxsdk.plugins.annotation.Line;
>>>>>>> parent of 7c873d5... Merge pull request #54 from hogantan/master
=======
import com.mapbox.mapboxsdk.plugins.annotation.Line;
>>>>>>> parent of fdc8f07... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
<<<<<<< HEAD
=======

>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======

>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of ca2abdd... 3/7
=======

>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"
import java.util.List;

public class JoinedFragment extends Fragment {

    private View currentView;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
    private LinesOfChecks linesOfChecks = new LinesOfChecks();

>>>>>>> parent of fdc8f07... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
    private FavouriteFragmentViewModel viewModel;
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of ca2abdd... 3/7
=======
=======
    private LinesOfChecks linesOfChecks = new LinesOfChecks();

>>>>>>> parent of 1e5a48b... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
=======
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
=======
    private LinesOfChecks linesOfChecks = new LinesOfChecks();

>>>>>>> parent of 6761555... Merge pull request #56 from hogantan/master
=======
    private LinesOfChecks linesOfChecks = new LinesOfChecks();

>>>>>>> parent of 7c873d5... Merge pull request #54 from hogantan/master
    private FavouriteFragmentViewModel viewModel;
>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_joined, container, false);
        initialise();
        initialiseRecyclerView();
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
        checkActivityExpiry();
        checkActivityCancelledConfirmed();
        getJoined();
<<<<<<< HEAD
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of ca2abdd... 3/7
=======
>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"

        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "joined");

        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, false, true);
                adapter.notifyDataSetChanged();

                //Display empty text message
                if(adapter.getItemCount() == 0) {
                    emptyText.setText("You have not join any activities!");
                } else {
                    emptyText.setText("");
                }
            }
        });

        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "joined");

        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, false, true);
                adapter.notifyDataSetChanged();

                //Display empty text message
                if(adapter.getItemCount() == 0) {
                    emptyText.setText("You have not join any activities!");
                } else {
                    emptyText.setText("");
                }
            }
        });

        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "joined");

        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, false, true);
                adapter.notifyDataSetChanged();

                //Display empty text message
                if(adapter.getItemCount() == 0) {
                    emptyText.setText("You have not join any activities!");
                } else {
                    emptyText.setText("");
                }
            }
        });

        //this is to update when an activity expires but it does not get reflected since join and like fragment does not listen to field data of activity
        //Third line of check
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Second line of check
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
                checkActivityExpiry();
                checkActivityCancelledConfirmed();
                getJoined();
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
                checkActivityExpiry();
                checkActivityCancelledConfirmed();
                getJoined();
>>>>>>> parent of ca2abdd... 3/7
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"
=======
=======
>>>>>>> parent of 6761555... Merge pull request #56 from hogantan/master
=======
>>>>>>> parent of 7c873d5... Merge pull request #54 from hogantan/master
=======
>>>>>>> parent of fdc8f07... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
                linesOfChecks.checkActivityExpiry();
                linesOfChecks.checkActivityCancelledConfirmed();

                viewModel.refreshActivities();
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of 1e5a48b... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
=======
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
=======
>>>>>>> parent of 6761555... Merge pull request #56 from hogantan/master
=======
>>>>>>> parent of 7c873d5... Merge pull request #54 from hogantan/master
=======
>>>>>>> parent of fdc8f07... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initialise() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        swipeRefreshLayout = currentView.findViewById(R.id.swipeContainer);
        emptyText = currentView.findViewById(R.id.tvFavouriteEmpty);
    }

    private void initialiseRecyclerView() {
        adapter = new FavouritesAdapter();
        recyclerView = currentView.findViewById(R.id.rvFavouriteJoined);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> parent of ca2abdd... 3/7
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
<<<<<<< HEAD
=======
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "joined");

=======
=======
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "joined");

<<<<<<< HEAD
>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"
=======
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, false, true);
                adapter.notifyDataSetChanged();

                //Display empty text message
                if(adapter.getItemCount() == 0) {
                    emptyText.setText("You have not join any activities!");
                } else {
                    emptyText.setText("");
                }
            }
        });
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of 7c73d04... Added load more messages feature to chat feature to prevent retrieving all messages when opening chat
=======
>>>>>>> parent of ca2abdd... 3/7
=======
>>>>>>> parent of e40b192... Revert "Merge pull request #53 from hogantan/2/7"
    }
=======
>>>>>>> parent of 1e5a48b... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
=======
    }
>>>>>>> parent of cdff690... Revert "Merge pull request #56 from hogantan/master"
=======
>>>>>>> parent of 6761555... Merge pull request #56 from hogantan/master
=======
>>>>>>> parent of 7c873d5... Merge pull request #54 from hogantan/master
=======
>>>>>>> parent of fdc8f07... Revert "Added load more messages feature to chat feature to prevent retrieving all messages when opening chat"
}