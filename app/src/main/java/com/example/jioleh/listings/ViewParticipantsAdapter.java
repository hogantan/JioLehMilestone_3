package com.example.jioleh.listings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewParticipantsAdapter extends RecyclerView.Adapter<ViewParticipantsAdapter.ParticipantsHolder> {
    private List<UserProfile> list_of_participants;

    ViewParticipantsAdapter() {
        list_of_participants = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewParticipantsAdapter.ParticipantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_users_layout, parent, false);
        return new ParticipantsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewParticipantsAdapter.ParticipantsHolder holder, int position) {
        holder.setUpView(list_of_participants.get(position));
    }

    @Override
    public int getItemCount() {
        return list_of_participants.size();
    }

    public void setData(List<UserProfile> users) {
        this.list_of_participants = users;
        notifyDataSetChanged();
    }

    static class ParticipantsHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView userImage;

        public ParticipantsHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);
            userImage = itemView.findViewById(R.id.ivUserImage);
        }

        public void setUpView(UserProfile userProfile) {
            username.setText(userProfile.getUsername());
            if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl()!=null) {
                Picasso.get().load(userProfile.getImageUrl()).into(userImage);
            } else {
                userImage.setBackgroundResource(R.drawable.ic_add_box_green);
            }
        }
    }
}
