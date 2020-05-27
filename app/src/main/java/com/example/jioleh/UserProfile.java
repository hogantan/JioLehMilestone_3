package com.example.jioleh;

public class UserProfile {
    private String username;
    private boolean isNewUser;
    private String contact;
    private String gender;
    private String age;
    private String bio;
    private String imageUrl;


    public UserProfile() {
    }//must have for firestore
    //needs ALL getter methods implemented

    UserProfile(boolean isNewUser) {
        this.username = "";
        this.isNewUser = isNewUser;
        this.contact = "";
        this.gender = "";
        this.age = "";
        this.bio = "";
    }

    UserProfile(String username, String contact, String gender, String age,
                String bio) {
        this.username = username;
        this.isNewUser = false;
        this.contact = contact;
        this.gender = gender;
        this.age = age;
        this.bio = bio;
        this.imageUrl ="";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public boolean getIsNewUser() {
        return this.isNewUser;
    }

    public String getContact() {
        return contact;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getBio() {
        return bio;
    }
}
