package com.example.jioleh;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//Adapter used to convert MessageChats into display items
public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityHolder> {

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<JioActivity> activities;

    ActivityAdapter() {
        activities = new ArrayList<>();
    }

    @NonNull
    @Override
    public ActivityAdapter.ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View activityBox;
        activityBox = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_activity_layout, parent, false);
        return new ActivityHolder(activityBox);
    }

    //Position is determine by the order of date sent which is set when reading messages in MessagePage activity
    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ActivityHolder holder, int position) {
        holder.setUpView(activities.get(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    //Used after retrieving all messages from database
    //Setting the list of messages
    public void setData(List<JioActivity> jioActivities) {
        this.activities = jioActivities;
        notifyDataSetChanged();
    }

    //Message Holder holds the details of the message layout eg.message_item_left/right.xml
    static class ActivityHolder extends RecyclerView.ViewHolder {

        private ImageView displayImage;
        private TextView displayTitle;
        private TextView date;
        private TextView time;
        private TextView currentParticipants;

        //Initialising the holder
        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
            displayImage = itemView.findViewById(R.id.ivHomeActivityDisplayImage);
            displayTitle = itemView.findViewById(R.id.tvHomeActivityDisplayTitle);
            date = itemView.findViewById(R.id.tvHomeActivityDisplayDate);
            time = itemView.findViewById(R.id.tvHomeActivityDisplayTime);
            currentParticipants = itemView.findViewById(R.id.tvHomeActivityCurrentParticipants);
        }

        //Setting the details in the holder
        public void setUpView(JioActivity jioActivity) {
            if (jioActivity.getImageUrl()!="" && jioActivity.getImageUrl()!=null) {
                Picasso.get().load(jioActivity.getImageUrl()).into(displayImage);
            }
            displayTitle.setText(jioActivity.getTitle());
            date.setText("Date: " + jioActivity.getEvent_date());
            time.setText("Time: " + jioActivity.getEvent_time());
            currentParticipants.setText(jioActivity.getCurrent_participants() + "/" + jioActivity.getMax_participants());
        }
    }
}
