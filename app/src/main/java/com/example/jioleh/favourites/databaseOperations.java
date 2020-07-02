package com.example.jioleh.favourites;

import com.example.jioleh.listings.JioActivity;

import java.util.List;

public interface databaseOperations {

    void activitiesDataAdded(List<JioActivity> allActivities);

    void onError(Exception e);
}
