package com.example.jioleh.chat;

import java.util.Date;

public class ChatChannel {

    private String chatChannelId;
    private String user1_uid;
    private String user2_uid;
    private Date last_active;

    ChatChannel(){}

    ChatChannel(String user1, String user2, Date last_active, String chatChannelId) {
        this.user1_uid = user1;
        this.user2_uid = user2;
        this.last_active = last_active;
        this.chatChannelId = chatChannelId;
    }

    public String getUser1_uid() {
        return user1_uid;
    }

    public void setUser1_uid(String user1_uid) {
        this.user1_uid = user1_uid;
    }

    public String getUser2_uid() {
        return user2_uid;
    }

    public void setUser2_uid(String user2_uid) {
        this.user2_uid = user2_uid;
    }

    public Date getLast_active() {
        return last_active;
    }

    public void setLast_active(Date last_active) {
        this.last_active = last_active;
    }

    public String getChatChannelId() {
        return chatChannelId;
    }

    public void setChatChannelId(String chatChannelId) {
        this.chatChannelId = chatChannelId;
    }
}
