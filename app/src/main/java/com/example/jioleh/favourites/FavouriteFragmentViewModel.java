package com.example.jioleh.favourites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jioleh.listings.JioActivity;

import java.util.List;

public class FavouriteFragmentViewModel extends ViewModel implements databaseOperations{

    private MutableLiveData<List<JioActivity>> listOfActivities = new MutableLiveData<>();
    private FavouritesFragmentRepository repository;

    private String current_uid;
    private String type;

    public FavouriteFragmentViewModel(String current_uid, String type) {
        this.current_uid = current_uid;
        this.type = type;
        repository = new FavouritesFragmentRepository(this);
        repository.getActivities(current_uid, type);
    }

    public LiveData<List<JioActivity>> getListOfActivities() {
        return listOfActivities;
    }

    public void refreshActivities() {
        repository.getActivities(current_uid, type);
    }

    @Override
    public void activitiesDataAdded(List<JioActivity> allActivities) {
        this.listOfActivities.setValue(allActivities);
    }

    @Override
    public void onError(Exception e) {

    }
}
