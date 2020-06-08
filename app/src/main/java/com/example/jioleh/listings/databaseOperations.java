package com.example.jioleh.listings;

import java.util.List;

//An interface between Repository and ViewModel, where ViewModel will implement
public interface databaseOperations {

    void jioActivityDataAdded(List<JioActivity> allActivities);

    void onError(Exception e);
}
