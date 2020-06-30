package com.example.jioleh.userprofile;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    private String wordsOfReview;
    private String from_uid;
    private String from_userImg;
    private String from_username;
    private float rating;
    private String documentId;

    @ServerTimestamp
    private Date timeOfPost;

    public Review() {
    }

    public Review(String wordsOfReview, String from_uid, String from_userImg, String from_username, float rating) {
        this.wordsOfReview = wordsOfReview;
        this.from_uid = from_uid;
        this.from_userImg = from_userImg;
        this.from_username = from_username;
        this.rating = rating;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Review(String wordsOfReview, float rating) {
        this.wordsOfReview = wordsOfReview;
        this.rating = rating;
    }

    public String getWordsOfReview() {
        return wordsOfReview;
    }

    public float getRating() {
        return rating;
    }

    public String getFrom_uid() {
        return from_uid;
    }

    public String getFrom_userImg() {
        return from_userImg;
    }

    public String getFrom_username() {
        return from_username;
    }

    public Date getTimeOfPost() {
        return timeOfPost;
    }

    public void setTimeOfPost(Date date) { this.timeOfPost = date;}


}
