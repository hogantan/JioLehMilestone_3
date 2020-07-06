package com.example.jioleh.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.listings.ViewJioActivity;
import com.example.jioleh.post.PostingPage;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class OpenChatsAdapter extends RecyclerView.Adapter<OpenChatsAdapter.OpenChatsHolder> {

    private List<UserProfile> profiles;
    private List<String> list_of_uid;

    public OpenChatsAdapter() {
        this.profiles = new ArrayList<>();
    }


    @NonNull
    @Override
    public OpenChatsAdapter.OpenChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View profileBox = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_users_layout, parent, false);;
        return new OpenChatsHolder(profileBox);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenChatsAdapter.OpenChatsHolder holder, int position) {
        holder.setIsRecyclable(false);
        UserProfile profile = profiles.get(position);
        holder.user_id = list_of_uid.get(position);
        holder.setUpView(profile);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    //Used to determine who the receiver or sender is depending on current user

    public void setData(List<UserProfile> userProfiles, List<String> list_of_uid) {
        this.profiles = userProfiles;
        this.list_of_uid = list_of_uid;
    }

    class OpenChatsHolder extends RecyclerView.ViewHolder {

        private ImageView displayImage;
        private TextView username;
        private TextView last_msg;
        private String user_id;
        private String imageUrl;
        private Context currentContext;

        //Initialising the holder
        OpenChatsHolder(@NonNull final View itemView) {
            super(itemView);
            displayImage = itemView.findViewById(R.id.civUserImage);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);
            last_msg = itemView.findViewById(R.id.tvSingleUsersLastMsg);
            currentContext = displayImage.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(itemView.getContext(), MessagePage.class);
                    nextActivity.putExtra("username", username.getText().toString());
                    nextActivity.putExtra("user_id", user_id);
                    nextActivity.putExtra("image_url", imageUrl);
                    itemView.getContext().startActivity(nextActivity);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    alertDialog();
                    return true;
                }
            });
        }

        //Setting the details in the holder
        void setUpView(UserProfile userProfile) {
            if (userProfile != null) {
                if (userProfile.getImageUrl()!=null && !userProfile.getImageUrl().equals("")) {
                    imageUrl = userProfile.getImageUrl();
                    Picasso.get().load(imageUrl).into(displayImage);
                } else {
                    displayImage.setImageDrawable(currentContext.getResources().getDrawable(R.drawable.default_picture));
                }
                username.setText(userProfile.getUsername());

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                //check if this user has blocked the other users
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.getUid())
                        .collection("blocked users")
                        .document(user_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    //means the user is blocked
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        //user is blocked
                                        last_msg.setText("This user has been blocked by you");
                                    } else {
                                        getLastMessage();
                                    }
                                }
                            }
                        });
            }
        }

        private void getLastMessage() {
            //Getting last message to display
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .collection("openchats")
                    .document(user_id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String chatId = documentSnapshot.get("channelId").toString();
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("chats")
                                    .document(chatId)
                                    .collection("messages")
                                    .orderBy("dateSent", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots.size() > 0) {
                                                last_msg.setText(queryDocumentSnapshots.getDocuments().get(0).get("text").toString());

                                                if (!queryDocumentSnapshots.getDocuments().get(0).get("sender").toString().equals(currentUser.getUid())) {
                                                    last_msg.setTextColor(last_msg.getContext().getResources().getColor(R.color.baseWhite));
                                                }

                                            }
                                        }
                                    });
                        }
                    });
        }

        //Prompts user whether to delete chat with other user on his end
        private void alertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage("Do you want to delete the chat with " + username.getText().toString() + "?")
                    .setTitle("Delete Chat");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final FirebaseFirestore datastore = FirebaseFirestore.getInstance();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    datastore.collection("users")
                            .document(currentUser.getUid())
                            .collection("openchats")
                            .document(user_id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    final String chatId = documentSnapshot.get("channelId").toString();

                                    //To delete openchats between current user and the other person
                                    datastore.collection("users")
                                            .document(currentUser.getUid())
                                            .collection("openchats")
                                            .document(user_id)
                                            .delete();

                                    //The following to to set flags in the database to determine whether a chat can be fully deleted backend
                                    datastore.collection("chats")
                                            .document(chatId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    System.out.println(documentSnapshot.get("delete"));
                                                    if (documentSnapshot.get("shared") != null) {
                                                        HashMap<String, String> input = new HashMap<>();
                                                        input.put("confirmDelete", "yes");
                                                        datastore.collection("chats")
                                                                .document(chatId)
                                                                .set(input, SetOptions.merge());
                                                    } else if (documentSnapshot.get("single") != null) {
                                                        HashMap<String, String> input = new HashMap<>();
                                                        input.put("confirmDelete", "yes");
                                                        datastore.collection("chats")
                                                                .document(chatId)
                                                                .set(input, SetOptions.merge());
                                                    } else {
                                                        //to indicate chat channel only own by one user ti facilitate delete
                                                        HashMap<String, String> input = new HashMap<>();
                                                        input.put("shared", "yes");
                                                        datastore.collection("chats")
                                                                .document(chatId)
                                                                .set(input);
                                                    }
                                                }
                                            });
                                }
                            });
                }
            });

            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }
}
