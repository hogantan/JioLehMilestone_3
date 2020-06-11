package com.example.jioleh.userprofile;

public class UserProfile {
    private String username;
    private boolean isNewUser;
    private String contact;
    private String gender;
    private String age;
    private String bio;
    private String interests;
    private String location;
    private String imageUrl;

    public UserProfile() {
    }//must have for firestore
    //needs ALL getter methods implemented

    public UserProfile(boolean isNewUser) {
        this.username = "";
        this.isNewUser = isNewUser;
        this.contact = "";
        this.gender = "";
        this.age = "";
        this.bio = "";
        this.imageUrl="";
        this.interests = "";
        this.location="";
    }

    public UserProfile(String username, String contact, String gender, String age,
                       String bio, String interests, String location) {
        this.username = username;
        this.isNewUser = false;
        this.contact = contact;
        this.gender = gender;
        this.age = age;
        this.bio = bio;
        this.interests = interests;
        this.location = location;
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

    public String getInterests() { return interests;}

    public String getLocation() { return location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
