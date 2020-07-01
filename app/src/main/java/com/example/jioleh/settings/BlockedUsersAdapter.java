package com.example.jioleh.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.BlockedUsersHolder> {
    private List<UserProfile> listOfBlockedUsers;

    public BlockedUsersAdapter() {
        this.listOfBlockedUsers = new ArrayList<>();
    }

    public void setData(List<UserProfile> listOfBlockedUsers) {
        this.listOfBlockedUsers = listOfBlockedUsers;
    }

    @NonNull
    @Override
    public BlockedUsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row =  LayoutInflater.from(parent.getContext()).inflate(R.layout.blocked_user_single_layout, parent, false);
        return new BlockedUsersHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUsersHolder holder, int position) {
        UserProfile blockedUser = listOfBlockedUsers.get(position);

        holder.tv_username.setText(blockedUser.getUsername());
        String UImg = blockedUser.getImageUrl();

        if (UImg != null&& !UImg.equals("") ) {
            Picasso.get().load(UImg).into(holder.iv_user_profile_pic);
        }

    }


    @Override
    public int getItemCount() {
        return listOfBlockedUsers.size();
    }

    static class BlockedUsersHolder extends RecyclerView.ViewHolder {

        ImageView iv_user_profile_pic;
        TextView tv_username;

        public BlockedUsersHolder(@NonNull View itemView) {
            super(itemView);
            iv_user_profile_pic = itemView.findViewById(R.id.iv_blocked_user_image);
            tv_username = itemView.findViewById(R.id.tv_blocked_user_username);
        }
    }
}
