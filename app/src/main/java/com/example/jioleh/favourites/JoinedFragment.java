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

import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class JoinedFragment extends Fragment {

    private View currentView;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;

    private FavouriteFragmentViewModel viewModel;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_joined, container, false);
        initialise();
        initialiseRecyclerView();

        //this is to update when an activity expires but it does not get reflected since join and like fragment does not listen to field data of activity
        //Third line of check
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Second line of check
                viewModel.checkActivityExpiry();
                viewModel.checkActivityCancelledConfirmed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return currentView;
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
    }
}