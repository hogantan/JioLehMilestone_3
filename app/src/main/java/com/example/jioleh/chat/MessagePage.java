package com.example.jioleh.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.OtherUserView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagePage extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView username;
    private ImageButton send;
    private ImageView receiverImage;
    private EditText input_message;
    private RecyclerView recyclerView;
    private DocumentSnapshot lastVisible;
    private List<MessageChat> messages;
    private String channelId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int limit = 20;


    private MessageAdapter adapter;

    private FirebaseAuth database;
    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    private Intent intent;

    private String sender;
    private String receiver;

    private static final int SINGLE = 1;
    private static final int DOUBLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        initialise();
        initialiseToolbar();
        initialiseRecyclerView();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSend();
            }
        });

        receiverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserProfile();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserProfile();
            }
        });

        //Third line of check
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessages();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages(); // this is to ensure if both users are chatting in realtime, then sent messages will appear on screen immediately
    }

    private void goToUserProfile() {
        Intent nextActivity = new Intent(MessagePage.this, OtherUserView.class);
        nextActivity.putExtra("username", username.getText().toString());
        nextActivity.putExtra("user_id", receiver);
        startActivity(nextActivity);
    }

    private void onClickSend() {
        final String input = input_message.getText().toString();
        //does not allow sending empty text
        if(!input.isEmpty()) {
            locateChatChannel(input);
        } else {
            Toast.makeText(MessagePage.this,
                    "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }
    }

    //Brief description of how chat works
    //For example, there are two users, user X and user Y and that they have never chat before.
    //User X messages User Y --> this creates ONE chat channel that is shared between both users.
    //User X deletes his chat with User Y on his end, this deletes the openchats in User X's collection with User Y but User Y still retains his collection and the chat channel ID.
    //User X messages User Y again, this creates a NEW chat channel between User X and Y. However, User X now uses the NEW chat channel BUT User Y still uses the old channel.
    //This means that, this new message that User X send will be updated in both chat channels.
    //User Y messages User X, this will not create any new channels but use the two existing ones and updating both with the new message.
    //If User Y deletes his chat with User X on his end, the old chat channel will be removed entirely from the database as nobody is using it anymore.
    //Deleting chat from database not done automatically here because it might lead to out of memory error.
    private void locateChatChannel(final String input) {
        //checking to see whether there exists a chat channel between the two users
        datastore.collection("users")
                .document(sender)
                .collection("openchats")
                .document(receiver)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();

                    datastore.collection("users")
                            .document(receiver)
                            .collection("openchats")
                            .document(sender)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Object senderChannel = document.get("channelId");
                            Object receiverChannel = documentSnapshot.get("channelId");
                            //if both users have an open chat channel between each other
                            if (documentSnapshot.exists() && senderChannel != null) {
                                //if both users share the same chat channel
                                if (senderChannel.toString().equals(receiverChannel.toString())) {
                                    MessageChat message = new MessageChat(sender,
                                            receiver, input, documentSnapshot.get("channelId").toString()
                                            , convertDateFormat(Calendar.getInstance().getTime()));

                                    sendMessage(message);
                                }
                                //if both users DO NOT share the same chat channel
                                else {
                                    MessageChat message1 = new MessageChat(sender,
                                            receiver, input, receiverChannel.toString()
                                            , convertDateFormat(Calendar.getInstance().getTime()));

                                    MessageChat message2 = new MessageChat(sender,
                                            receiver, input, senderChannel.toString()
                                            , convertDateFormat(Calendar.getInstance().getTime()));

                                    sendMessage(message1);
                                    sendMessage(message2);
                                }
                            }//if sender does not have an open chat channel with the receiver but the receiver has an open chat channel with sender
                            else if (document.get("channelId") == null && documentSnapshot.exists()) {
                                MessageChat message1 = new MessageChat(sender,
                                        receiver, input, receiverChannel.toString()
                                        , convertDateFormat(Calendar.getInstance().getTime()));

                                String chatChannelID = openChatChannel(sender, receiver, SINGLE);
                                MessageChat message2 = new MessageChat(sender,
                                        receiver, input, chatChannelID
                                        , convertDateFormat(Calendar.getInstance().getTime()));

                                sendMessage(message1);
                                sendMessage(message2);
                            }
                            //if receiver does not have an open chat channel with the sender but the sender has an open chat channel with receiver
                            else if (document.get("channelId") != null && !documentSnapshot.exists()) {
                                String chatChannelID = openChatChannel(receiver, sender, SINGLE);
                                MessageChat message1 = new MessageChat(sender,
                                        receiver, input, chatChannelID
                                        , convertDateFormat(Calendar.getInstance().getTime()));

                                MessageChat message2 = new MessageChat(sender,
                                        receiver, input, senderChannel.toString()
                                        , convertDateFormat(Calendar.getInstance().getTime()));

                                sendMessage(message1);
                                sendMessage(message2);
                            }
                            //if both users DO NOT have a chat channel between each other
                            else {
                                String chatChannelID = openChatChannel(sender, receiver, DOUBLE);
                                MessageChat message = new MessageChat(sender,
                                        receiver, input, chatChannelID
                                        , convertDateFormat(Calendar.getInstance().getTime()));

                                sendMessage(message);
                            }
                            getMessages(); //to display first message
                        }
                    });
                } else {
                    Toast.makeText(MessagePage.this, "Error", Toast.LENGTH_SHORT).show();
                }
                input_message.setText("");
            }
        });
    }

    //Goes into specific chat channel and adds the message in
    private void sendMessage(MessageChat message) {
        datastore.collection("chats")
                .document(message.getChannelID())
                .collection("messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(MessagePage.this,
                                    "Message sent failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Create new chat channel UID that is saved under openchats under the user
    private String openChatChannel(String sender, String receiver, int type) {
        //Creating a new chat channel UID
        DocumentReference ref = datastore.collection("chats").document();

        //Creating a new field for users to store channel id
        HashMap<String, String> input_user_firestore = new HashMap<>();
        input_user_firestore.put("channelId", ref.getId());

        if (type == SINGLE) {
            //Open channel on sender storage
            datastore.collection("users")
                    .document(sender)
                    .collection("openchats")
                    .document(receiver)
                    .set(input_user_firestore);

            //to indicate chat channel only own by one user ti facilitate delete
            HashMap<String, String> input = new HashMap<>();
            input.put("single", "yes");
            datastore.collection("chats")
                    .document(ref.getId())
                    .set(input);
        } else {
            //Open channel on sender storage
            datastore.collection("users")
                    .document(sender)
                    .collection("openchats")
                    .document(receiver)
                    .set(input_user_firestore);

            //Open channel on receiver storage
            datastore.collection("users")
                    .document(receiver)
                    .collection("openchats")
                    .document(sender)
                    .set(input_user_firestore);
        }

        return ref.getId();
    }

    //Goes to the specific chat channel and retrieves all messages
    //and orders them by date sent and displaying to users via the MessageAdapter
    //Retrieves each messsage one by one and ordering them, upon retrieving each
    //message DOCUMENT, convert to MessageChat class so that the MessageAdapter can
    //display it
    private void getMessages() {

        //check to see if current user (Sender) has blocked the other user (receiver)
        datastore.collection("users")
                .document(sender)
                .collection("blocked users")
                .document(receiver)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Toast.makeText(MessagePage.this, "You have blocked the user", Toast.LENGTH_SHORT).show();

                                input_message.setEnabled(false);
                                send.setEnabled(false);
                                send.setVisibility(View.GONE);
                                input_message.setText("You have blocked the user");
                            } else {
                                datastore.collection("users")
                                        .document(sender)
                                        .collection("openchats")
                                        .document(intent.getStringExtra("user_id"))
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.get("channelId") != null) {
                                            channelId = documentSnapshot.get("channelId").toString();
                                            datastore.collection("chats")
                                                    .document(channelId)
                                                    .collection("messages")
                                                    .orderBy("dateSent", Query.Direction.DESCENDING)
                                                    .limit(limit)
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                            } else {
                                                                messages = queryDocumentSnapshots.toObjects(MessageChat.class);
                                                                Collections.reverse(messages);
                                                                adapter.setData(messages);
                                                                adapter.notifyDataSetChanged();
                                                                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                                                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                            }

                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    //To prevent retrieving all messages which could be quite expensive when chat has many messages
    public void refreshMessages() {
        if (lastVisible != null) {
            datastore.collection("chats")
                    .document(channelId)
                    .collection("messages")
                    .orderBy("dateSent", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(limit)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<MessageChat> new_input = queryDocumentSnapshots.toObjects(MessageChat.class);
                            Collections.reverse(new_input);
                            new_input.addAll(messages);
                            messages = new_input;
                            adapter.setData(messages);
                            adapter.notifyDataSetChanged();

                            if (queryDocumentSnapshots.size() - 1 < 0) {
                                lastVisible = null;
                            } else {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No more messages to load", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertDateFormat(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return formatter.format(date);
    }

    private void initialise() {
        send = findViewById(R.id.ibSendButton);
        input_message = findViewById(R.id.etSendMessage);
        receiverImage = findViewById(R.id.civMessageImage);
        intent = getIntent();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();
        database = FirebaseAuth.getInstance();
        sender = currentUser.getUid();
        receiver = intent.getStringExtra("user_id");
        swipeRefreshLayout = findViewById(R.id.swipeContainer);

    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbMessageTopBar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Setting details in toolbar
        username = findViewById(R.id.tvMessageUsername);
        String updateUsername = intent.getStringExtra("username");
        username.setText(updateUsername);
        String imageUrl = intent.getStringExtra("image_url");
        if (imageUrl!=null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(receiverImage);
        }
    }

    private void initialiseRecyclerView() {
        adapter = new MessageAdapter();
        recyclerView = findViewById(R.id.rvMessageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
