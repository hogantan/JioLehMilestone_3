package com.example.jioleh.chat;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.lifecycle.Observer;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.jioleh.R;
        import com.example.jioleh.listings.JioActivity;
        import com.example.jioleh.listings.JioActivityViewModel;
        import com.example.jioleh.userprofile.UserProfile;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.gms.tasks.Tasks;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QuerySnapshot;

        import java.util.ArrayList;
        import java.util.List;

//Temporary view of Chat fragment
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private View currentView;
    private OpenChatsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ChatFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.chat_fragment,container,false);
        initialise();
        initialiseRecyclerView();
        return currentView;
    }

    private void initialise() {
        recyclerView = currentView.findViewById(R.id.rvUsersList);
        swipeRefreshLayout = currentView.findViewById(R.id.swipeContainer);
    }

    private void initialiseRecyclerView() {
        adapter = new OpenChatsAdapter();
        recyclerView = currentView.findViewById(R.id.rvUsersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ChatFragmentViewModel(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //observe for changes in database
        viewModel.getListOfUserProfiles().observe(getViewLifecycleOwner(), new Observer<List<?>[]>() {
            @Override
            public void onChanged(List<?>[] userProfiles) {
                adapter.setData((List<UserProfile>)userProfiles[0], (List<String>)userProfiles[1]);
                adapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Second line of check
                viewModel.refreshProfiles();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
