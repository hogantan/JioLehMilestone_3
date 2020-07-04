package com.example.jioleh.userprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class userProfileViewModel extends ViewModel {

    private userProfileRepository repository;

    public userProfileViewModel() {
        this.repository = new userProfileRepository();
    }

    public LiveData<UserProfile> getUser(String uid){
        return repository.getUser(uid);
    }
}
