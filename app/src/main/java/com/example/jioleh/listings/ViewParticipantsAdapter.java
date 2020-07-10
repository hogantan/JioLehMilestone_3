package com.example.jioleh.listings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.example.jioleh.userprofile.YourOwnOtherUserView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewParticipantsAdapter extends RecyclerView.Adapter<ViewParticipantsAdapter.ParticipantsHolder> {
    private List<UserProfile> list_of_participants;
    private List<String> list_of_uid;
    private String host_uid;
    private String activity_id;

    ViewParticipantsAdapter() {
        list_of_participants = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewParticipantsAdapter.ParticipantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_participant_layout, parent, false);
        return new ParticipantsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewParticipantsAdapter.ParticipantsHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.user_id = list_of_uid.get(position);
        holder.position = position;
        holder.setUpView(list_of_participants.get(position));
    }

    @Override
    public int getItemCount() {
        return list_of_participants.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setData(List<UserProfile> users, List<String> list_of_uid, String host_uid, String activity_id) {
        this.list_of_participants = users;
        this.list_of_uid = list_of_uid;
        this.host_uid = host_uid;
        this.activity_id = activity_id;
    }

    class ParticipantsHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private CircleImageView displayImage;
        private String user_id;
        private String imageUrl;
        private String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        private Context currentContext;
        private int position;

        public ParticipantsHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);
            displayImage = itemView.findViewById(R.id.civUserImage);
            currentContext = username.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user_id)) {
                        Intent nextActivity = new Intent(itemView.getContext(), YourOwnOtherUserView.class);
                        nextActivity.putExtra("username", username.getText().toString());
                        nextActivity.putExtra("user_id", user_id);
                        itemView.getContext().startActivity(nextActivity);
                    } else {
                        Intent nextActivity = new Intent(itemView.getContext(), OtherUserView.class);
                        nextActivity.putExtra("username", username.getText().toString());
                        nextActivity.putExtra("user_id", user_id);
                        itemView.getContext().startActivity(nextActivity);
                    }
                }
            });
        }

        public void setUpView(UserProfile userProfile) {
            if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl() != null) {
                imageUrl = userProfile.getImageUrl();
                Picasso.get().load(imageUrl).into(displayImage);
            } else {
                displayImage.setImageDrawable(currentContext.getResources().getDrawable(R.drawable.default_picture));
            }

            username.setText(userProfile.getUsername());

            //Allows host to kick participants
            if (currentUserUid.equals(host_uid)) {
                if (!currentUserUid.equals(user_id)) {
                    itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            alertDialogKickParticipant();
                            return true;
                        }
                    });
                }
            }
        }

        private void alertDialogKickParticipant() {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage("Do you want to kick " + username.getText().toString() + " from the activity?")
                    .setTitle("Kick Participant");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    updateParticipants(activity_id);
                }
            });

            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }

        //Update number of current participants as well as the list of participants in the activity
        private void updateParticipants(final String activity_id) {

            FirebaseFirestore datastore = FirebaseFirestore.getInstance();

            datastore.collection("users")
                    .document(user_id)
                    .collection("joined")
                    .document(activity_id)
                    .delete();

            datastore.collection("activities")
                    .document(activity_id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            JioActivity currentActivity = documentSnapshot.toObject(JioActivity.class);

                            int updatedNumberOfParticipants = currentActivity.getCurrent_participants() - 1;
                            ArrayList<String> updatedListParticipants = currentActivity.getParticipants();
                            updatedListParticipants.remove(user_id);

                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("current_participants", updatedNumberOfParticipants);

                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("participants", updatedListParticipants);

                            list_of_uid.remove(position);
                            list_of_participants.remove(position);
                            notifyDataSetChanged();
                        }
                    });
        }
    }
}
