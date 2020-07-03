package com.example.jioleh.favourites;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.R;
import com.example.jioleh.listings.ActivityAdapter;
import com.example.jioleh.listings.JioActivity;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//Exactly the same as joined fragment just that collection directing different
//basically any "joined" is change into "liked"
public class LikedFragment extends Fragment {

    private View currentView;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;

    private LinesOfChecks linesOfChecks = new LinesOfChecks();

    private FavouriteFragmentViewModel viewModel;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_liked, container, false);
        initialise();
        initialiseRecyclerView();

        return currentView;
    }

    private void initialise() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        swipeRefreshLayout = currentView.findViewById(R.id.swipeContainer);
        emptyText = currentView.findViewById(R.id.tvFavouriteEmpty);
    }

    private void initialiseRecyclerView() {
        adapter = new FavouritesAdapter();
        recyclerView = currentView.findViewById(R.id.rvFavouriteLiked);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new FavouriteFragmentViewModel(currentUser.getUid(), "liked");

        //observe for changes in database
        viewModel.getListOfActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> activities) {
                adapter.setData(activities, false, true);
                adapter.notifyDataSetChanged();

                //Display empty text message
                if(adapter.getItemCount() == 0) {
                    emptyText.setText("You have not like any activities!");
                } else {
                    emptyText.setText("");
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                linesOfChecks.checkActivityExpiry();
                linesOfChecks.checkActivityCancelledConfirmed();

                viewModel.refreshActivities();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
