package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.jioleh.favourites.FavouriteFragmentViewModel;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class UserProfileListingsFragment extends Fragment {
    private View currentView;
    private RecyclerView recyclerView;
    private TextView emptyMsg;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavouritesAdapter adapter;
    private UserProfileListingViewModel viewModel;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private LinesOfChecks linesOfChecks = new LinesOfChecks();
    private String uid;
    private boolean deletable;

    public UserProfileListingsFragment() {
        // Required empty public constructor
    }

    public UserProfileListingsFragment(String uid){
        this.uid = uid;
        //this is to differentiate between otheruserview and your own view of your profile
        //basically you should not be able to delete another users activities
        this.deletable = currentUser.getUid().equals(uid);
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

        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new UserProfileListingViewModel(uid);

        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, deletable, false);
                adapter.notifyDataSetChanged();
                checkEmptyMsg();
            }
        });

        //this is to update when an activity expires but it does not get reflected since join and like fragment does not listen to field data of activity
        //Third line of check
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Second line of check
                linesOfChecks.checkActivityExpiry();
                linesOfChecks.checkActivityCancelledConfirmed();

                viewModel.refreshActivities();
                checkEmptyMsg();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initialise() {
        swipeRefreshLayout = currentView.findViewById(R.id.swipeContainer);
    }

    private void initialiseRecyclerView() {
        adapter = new FavouritesAdapter();
        recyclerView = currentView.findViewById(R.id.userProfile_listings_rv);
        emptyMsg = currentView.findViewById(R.id.tvEmptyMsg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void checkEmptyMsg() {
        if (adapter.getItemCount() < 1) {
            emptyMsg.setVisibility(TextView.VISIBLE);
        } else {
            emptyMsg.setVisibility(TextView.INVISIBLE);
        }
    }
}

