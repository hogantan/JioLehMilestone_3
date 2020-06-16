package com.example.jioleh.chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

//Temporary adapter used to display all users in firebase so as to facilitate chat testing
public class UsersAdapter extends FirestoreRecyclerAdapter<UserProfile, UsersAdapter.UserHolder> {

    public UsersAdapter(@NonNull FirestoreRecyclerOptions<UserProfile> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull UserProfile model) {
        holder.username.setText(model.getUsername());
        holder.user_id = getSnapshots().getSnapshot(position).getId();
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_users_layout, parent, false);
        return new UserHolder(v);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView last_msg;
        private String user_id;

        public UserHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvSingleUsersUsername);
            last_msg = itemView.findViewById(R.id.tvSingleUsersLastMsg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(itemView.getContext(), OtherUserView.class);
                    nextActivity.putExtra("username", username.getText().toString());
                    nextActivity.putExtra("user_id", user_id);
                    itemView.getContext().startActivity(nextActivity);
                }
            });
        }
    }
}
