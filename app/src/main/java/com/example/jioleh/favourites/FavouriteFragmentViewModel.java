package com.example.jioleh.favourites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jioleh.listings.JioActivity;

import java.util.List;

public class FavouriteFragmentViewModel extends ViewModel implements databaseOperations{

    private MutableLiveData<List<JioActivity>> listOfActivities = new MutableLiveData<>();
    private FavouritesFragmentRepository repository;

    public FavouriteFragmentViewModel(String current_uid, String type) {
        repository = new FavouritesFragmentRepository(this);
        repository.getActivities(current_uid, type);
    }

    public LiveData<List<JioActivity>> getListOfActivities() {
        return listOfActivities;
    }

    public void checkActivityExpiry() {
        repository.checkActivityExpiry();
    }

    public void checkActivityCancelledConfirmed() { repository.checkActivityCancelledConfirmed();}

    @Override
    public void activitiesDataAdded(List<JioActivity> allActivities) {
        this.listOfActivities.setValue(allActivities);
    }

    @Override
    public void onError(Exception e) {

    }
}
