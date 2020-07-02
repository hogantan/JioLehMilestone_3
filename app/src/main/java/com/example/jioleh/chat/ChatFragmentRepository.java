package com.example.jioleh.chat;

import androidx.annotation.Nullable;

import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragmentRepository {

    databaseOperations databaseOperations;

    private List<UserProfile> listOfUserProfiles = new ArrayList<>();
    private List<String> list_of_uid = new ArrayList<>();
    private List<?>[] profiles_and_uids = new List<?>[2];
    private ArrayList<Task<DocumentSnapshot>> list_of_task = new ArrayList<>();
    private CollectionReference userProfilesColRef = FirebaseFirestore.getInstance().collection("users");

    public ChatFragmentRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
    }


    public void getUserProfiles(String current_uid) {
        userProfilesColRef
                .document(current_uid)
                .collection("openchats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();

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
                                    listOfUserProfiles.add(userProfile);
                                }
                                profiles_and_uids[0] = listOfUserProfiles;
                                profiles_and_uids[1] = list_of_uid;
                                databaseOperations.userProfileDataAdded(profiles_and_uids);
                            }
                        });
                    }
                });
    }

    //Get a completable future task
    private Task<DocumentSnapshot> getUser(String uid) {
        return userProfilesColRef.document(uid).get();
    }
}
