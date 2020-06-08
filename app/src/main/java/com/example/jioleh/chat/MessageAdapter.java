package com.example.jioleh.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

//Adapter used to convert MessageChats into display items
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    //Static fields to determine who is the receiver or the sender depending on current user
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<MessageChat> messages;

    MessageAdapter() {
        messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatBox;

        //Determines which layout to use depending on who is the receiver or sender
        if (viewType == MSG_RIGHT) {
         chatBox = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_right, parent, false);
        } else {
            chatBox = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_left, parent, false);
        }

        return new MessageHolder(chatBox);
    }

    //Position is determine by the order of date sent which is set when reading messages in MessagePage activity
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position) {
        holder.setUpView(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Used to determine who the receiver or sender is depending on current user
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(currentUser.getUid())) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    //Used after retrieving all messages from database
    //Setting the list of messages
    public void setData(List<MessageChat> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    //Message Holder holds the details of the message layout eg.message_item_left/right.xml
    static class MessageHolder extends RecyclerView.ViewHolder {

        private TextView messageSent;
        private TextView dateSent;

        //Initialising the holder
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageSent = itemView.findViewById(R.id.tvMessage);
        }

        //Setting the details in the holder
        public void setUpView(MessageChat message) {
            messageSent.setText(message.getText());
        }
    }
}
