package com.example.jioleh.userprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.listings.JioActivity;

import java.util.List;

public class UserProfileListingViewModel extends ViewModel implements databaseOperations {

    private MutableLiveData<List<JioActivity>> listOfActivities = new MutableLiveData<>();
    private userProfileRepository repository;
    private String current_uid;

    public UserProfileListingViewModel(String current_uid) {
        this.current_uid = current_uid;
        this.repository = new userProfileRepository(this);
        this.repository.getActivities(current_uid);
    }

    public LiveData<List<JioActivity>> getListOfActivities() {
        return listOfActivities;
    }

    public void refreshActivities() {
        this.repository.getActivities(current_uid);
    }

    @Override
    public void activitiesDataAdded(List<JioActivity> allActivities) {
        this.listOfActivities.setValue(allActivities);
    }

    @Override
    public void onError(Exception e) {

    }
}
