package com.example.jioleh.chat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

//Base class that represents a single chat message

public class MessageChat {

    private String sender;
    private String receiver;
    private String text;
    private String channelID;
    private String date;

    @ServerTimestamp
    private Date dateSent;

    public MessageChat(String sender, String receiver, String text, String channelID, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.channelID = channelID;
        this.date = date;
    }

    public MessageChat(){
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
