package com.example.jioleh.chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.listings.ViewJioActivity;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class OpenChatsAdapter extends RecyclerView.Adapter<OpenChatsAdapter.OpenChatsHolder> {

    private List<UserProfile> profiles;
    private List<String> list_of_uid;

    public OpenChatsAdapter() {
        profiles = new ArrayList<>();
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
        notifyDataSetChanged();
    }

    static class OpenChatsHolder extends RecyclerView.ViewHolder {

        private ImageView displayImage;
        private String imageUrl;
        private TextView username;
        private String user_id;

        //Initialising the holder
        OpenChatsHolder(@NonNull final View itemView) {
            super(itemView);
            displayImage = itemView.findViewById(R.id.ivUserImage);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);

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
        }

        //Setting the details in the holder
        void setUpView(UserProfile userProfile) {
            if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl()!=null) {
                imageUrl = userProfile.getImageUrl();
                Picasso.get().load(imageUrl).into(displayImage);
            }
            username.setText(userProfile.getUsername());
        }
    }
}