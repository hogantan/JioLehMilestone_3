package com.example.jioleh.chat;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.jioleh.R;
        import com.example.jioleh.chat.UsersAdapter;
        import com.example.jioleh.userprofile.UserProfile;
        import com.firebase.ui.firestore.FirestoreRecyclerOptions;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.Query;

//Temporary view of Chat fragment
public class ChatFragment extends Fragment {

    private RecyclerView users_list;
    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private UsersAdapter adapter;
    private View currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.chat_fragment,container,false);
        initialise();
        setUpRecyclerView();
        return currentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initialise() {
        users_list = currentView.findViewById(R.id.rvUsersList);
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("users");
    }

    private void setUpRecyclerView() {

        Query query = reference.orderBy("username", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<UserProfile> options
                = new FirestoreRecyclerOptions.Builder<UserProfile>()
                .setQuery(query, UserProfile.class)
                .build();

        adapter = new UsersAdapter(options);

        users_list.setHasFixedSize(true);
        users_list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        users_list.setAdapter(adapter);
    }

}
