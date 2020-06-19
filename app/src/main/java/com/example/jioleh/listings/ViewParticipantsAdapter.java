package com.example.jioleh.listings;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.example.jioleh.userprofile.YourOwnOtherUserView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewParticipantsAdapter extends RecyclerView.Adapter<ViewParticipantsAdapter.ParticipantsHolder> {
    private List<UserProfile> list_of_participants;
    private List<String> list_of_uid;

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
        holder.setUpView(list_of_participants.get(position));
        holder.user_id = list_of_uid.get(position);
    }

    @Override
    public int getItemCount() {
        return list_of_participants.size();
    }

    public void setData(List<UserProfile> users, List<String> list_of_uid) {
        this.list_of_participants = users;
        this.list_of_uid = list_of_uid;
        notifyDataSetChanged();
    }

    static class ParticipantsHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView displayImage;
        private String user_id;
        private String imageUrl;

        public ParticipantsHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);
            displayImage = itemView.findViewById(R.id.ivUserImage);

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
            if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl()!=null) {
                imageUrl = userProfile.getImageUrl();
                Picasso.get().load(imageUrl).into(displayImage);
            }
            username.setText(userProfile.getUsername());
        }
    }
}
