package com.example.jioleh;

public class UserProfile {
    String username;
    boolean isNewUser;
    String contact;
    String gender;
    String age;
    String bio;


    public UserProfile(){}//must have for firestore
    //needs ALL getter methods implemented

    UserProfile(boolean isNewUser) {
        this.username = "";
        this.isNewUser =  isNewUser;
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

    }

    public String getUsername() {
        return username;
    }

    public boolean getIsNewUser(){
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
