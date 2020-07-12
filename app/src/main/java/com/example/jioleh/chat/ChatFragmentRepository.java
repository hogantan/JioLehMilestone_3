package com.example.jioleh.chat;

import androidx.annotation.Nullable;

import com.example.jioleh.listings.JioActivity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatFragmentRepository {

    databaseOperations databaseOperations;

    private List<ChatChannel> listOfChatChannels = new ArrayList<>();
    private ArrayList<Task<DocumentSnapshot>> list_of_task = new ArrayList<>();
    private CollectionReference userProfilesColRef = FirebaseFirestore.getInstance().collection("users");
    private CollectionReference chatsColRef = FirebaseFirestore.getInstance().collection("chats");

    public ChatFragmentRepository(databaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
    }


    public void getChatChannels(String current_uid) {
        userProfilesColRef
                .document(current_uid)
                .collection("openchats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();

                        list_of_task.clear();
                        listOfChatChannels.clear();

                        for(DocumentSnapshot documentSnapshot : list_of_documents) {
                            list_of_task.add(getChannel(documentSnapshot.get("channelId").toString()));
                        }

                        Tasks.whenAllSuccess(list_of_task).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0; i < list_of_task.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    if (snapshot.exists()) {
                                        ChatChannel chatChannel = snapshot.toObject(ChatChannel.class);
                                        listOfChatChannels.add(chatChannel);
                                    }
                                }

                                //Arrange base on latest active chat channel
                                Collections.sort(listOfChatChannels, new Comparator<ChatChannel>() {
                                    @Override
                                    public int compare(ChatChannel o1, ChatChannel o2) {
                                        return o2.getLast_active().compareTo(o1.getLast_active());
                                    }
                                });

                                databaseOperations.chatChannelsDataAdded(listOfChatChannels);
                            }
                        });
                    }
                });
    }

    public void refreshChatChannels(String current_uid) {
        userProfilesColRef
                .document(current_uid)
                .collection("openchats")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list_of_documents = queryDocumentSnapshots.getDocuments();

                        list_of_task.clear();
                        listOfChatChannels.clear();

                        for(DocumentSnapshot documentSnapshot : list_of_documents) {
                            list_of_task.add(getChannel(documentSnapshot.get("channelId").toString()));
                        }

                        Tasks.whenAllSuccess(list_of_task).addOnSuccessListener(new OnSuccessListener<List<? super DocumentSnapshot>>() {
                            @Override
                            public void onSuccess(List<? super DocumentSnapshot> snapShots) {
                                for (int i = 0; i < list_of_task.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) snapShots.get(i);
                                    if (snapshot.exists()) {
                                        ChatChannel chatChannel = snapshot.toObject(ChatChannel.class);
                                        listOfChatChannels.add(chatChannel);
                                    }
                                }

                                //Arrange base on latest active chat channel
                                Collections.sort(listOfChatChannels, new Comparator<ChatChannel>() {
                                    @Override
                                    public int compare(ChatChannel o1, ChatChannel o2) {
                                        return o2.getLast_active().compareTo(o1.getLast_active());
                                    }
                                });

                                databaseOperations.chatChannelsDataAdded(listOfChatChannels);
                            }
                        });
                    }
                });
    }

    //Get a completable future task
    private Task<DocumentSnapshot> getChannel(String chat_uid) {
        return chatsColRef.document(chat_uid).get();
    }
}
