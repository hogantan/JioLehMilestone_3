package com.example.jioleh.chat;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.jioleh.R;
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


    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    private ArrayList<UserProfile> list_of_profiles = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_task = new ArrayList<>();
    private ArrayList<String> list_of_uid = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.chat_fragment,container,false);
        initialise();
        initialiseRecyclerView();
        return currentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        list_of_task.clear();
        list_of_profiles.clear();
        list_of_uid.clear();
        getUsers();
    }

    private void initialise() {
        recyclerView = currentView.findViewById(R.id.rvUsersList);
        datastore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initialiseRecyclerView() {
        adapter = new OpenChatsAdapter();
        recyclerView = currentView.findViewById(R.id.rvUsersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getUsers() {
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("openchats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();
                            updateView(list_of_documents);
                        }
                    }
                });
    }

    private void updateView(List<DocumentSnapshot> list_of_documents) {
        for(DocumentSnapshot documentSnapshot : list_of_documents) {
            list_of_uid.add(documentSnapshot.getId());
        }

        for(String uid : list_of_uid) {
            list_of_task.add(getUser(uid));
        }

        Tasks.whenAllSuccess(list_of_task).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                for (int i = 0; i < list_of_task.size(); i++) {
                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                    UserProfile userProfile = snapshot.toObject(UserProfile.class);
                    list_of_profiles.add(userProfile);
                }
                adapter.setData(list_of_profiles, list_of_uid);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //Get a completable future task
    private Task<DocumentSnapshot> getUser(String uid) {
        return datastore.collection("users")
                .document(uid)
                .get();
    }
}
