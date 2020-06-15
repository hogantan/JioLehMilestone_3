package com.example.jioleh.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

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

    private MessageAdapter adapter;

    private FirebaseAuth database;
    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        initialiseToolbar();
        initialise();
        initialiseRecyclerView();

        username = findViewById(R.id.tvMessageUsername);
        String updateUsername = intent.getStringExtra("username");
        username.setText(updateUsername);
        String imageUrl = intent.getStringExtra("image_url");
        Picasso.get().load(imageUrl).into(receiverImage);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages(); // this is to ensure if both users are chatting in realtime, then sent messages will appear on screen immediately
    }

    private void goToUserProfile() {
        if (intent.getBooleanExtra("not_from_other_user_view", false)) {
            onBackPressed();
        } else {
            Intent nextActivity = new Intent(MessagePage.this, OtherUserView.class);
            nextActivity.putExtra("username", username.getText().toString());
            nextActivity.putExtra("user_id", intent.getStringExtra("user_id"));
            nextActivity.putExtra("not_from_message_page", true);
            startActivity(nextActivity);
        }
    }

    private void onClickSend() {
        final String input = input_message.getText().toString();
        //does not allow sending empty text
        if(!input.isEmpty()) {
            //checking to see whether there exists a chat channel between the two users
            datastore.collection("users")
                    .document(currentUser.getUid())
                    .collection("openchats")
                    .document(intent.getStringExtra("user_id"))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        //if the chat channel exists
                        if (document.exists()) {
                            MessageChat message = new MessageChat(currentUser.getUid(),
                                    intent.getStringExtra("user_id"), input,
                                    document.get("channelId").toString());
                            sendMessage(message);
                        }
                        //if the chat channel does not exist
                        else {
                            String chatChannelID = openChatChannel();
                            MessageChat message = new MessageChat(currentUser.getUid(),
                                    intent.getStringExtra("user_id"), input, chatChannelID);
                            sendMessage(message);
                        }
                        //to as to display text send on first send
                        getMessages();
                    } else {
                        Toast.makeText(MessagePage.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    input_message.setText("");
                }
            });
        } else {
            Toast.makeText(MessagePage.this,
                    "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }
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
    private String openChatChannel() {
        //Creating a new chat channel UID
        DocumentReference ref = datastore.collection("chats").document();

        //Creating a new field for users to store channel id
        HashMap<String, String> input_user_firestore = new HashMap<>();
        input_user_firestore.put("channelId", ref.getId());

        //Open channel on sender storage
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("openchats")
                .document(intent.getStringExtra("user_id"))
                .set(input_user_firestore);

        //Open channel on receiver storage
        //intent here refers to clicking on a UserHolder from the chat page
        datastore.collection("users")
                .document(intent.getStringExtra("user_id"))
                .collection("openchats")
                .document(currentUser.getUid())
                .set(input_user_firestore);

        return ref.getId();
    }

    //Goes to the specific chat channel and retrieves all messages
    //and orders them by date sent and displaying to users via the MessageAdapter
    //Retrieves each messsage one by one and ordering them, upon retrieving each
    //message DOCUMENT, convert to MessageChat class so that the MessageAdapter can
    //display it
    private void getMessages() {
                datastore.collection("users")
                .document(currentUser.getUid())
                .collection("openchats")
                .document(intent.getStringExtra("user_id"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.get("channelId") != null) {
                            String channelId = documentSnapshot.get("channelId").toString();
                            datastore.collection("chats")
                                    .document(channelId)
                                    .collection("messages")
                                    .orderBy("dateSent")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                            } else {
                                                List<MessageChat> messages
                                                        = queryDocumentSnapshots.toObjects(MessageChat.class);
                                                adapter.setData(messages);
                                                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void initialise() {
        send = findViewById(R.id.ibSendButton);
        input_message = findViewById(R.id.etSendMessage);
        receiverImage = findViewById(R.id.civMessageImage);
        intent = getIntent();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();
        database = FirebaseAuth.getInstance();
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
    }

    private void initialiseRecyclerView() {
        adapter = new MessageAdapter();
        recyclerView = findViewById(R.id.rvMessageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
