package com.example.jioleh.listings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class JioActivityViewModel extends ViewModel implements databaseOperations {

    private MutableLiveData<List<JioActivity>> listOfJioActivities = new MutableLiveData<>();
    JioActivityRepository repository;

    public JioActivityViewModel() {
        //if using viewModel for other queries in other frag/act remove this
        //because this constructor just activates the method getJioActivityData whenever
        //instantiated. This is to invoke method jioActivityDataAdded to set the list<JioActivity>
        //to MutableLiveData

        repository = new JioActivityRepository(this);
        repository.getJioActivityData();
    }


    LiveData<List<JioActivity>> getListOfJioActivities() {
        return listOfJioActivities;
    }

    public void checkActivityExpiry() {
        repository.checkActivityExpiry();
    }

    @Override
    public void jioActivityDataAdded(List<JioActivity> allActivities) {
        listOfJioActivities.setValue(allActivities);
    }

    @Override
    public void onError(Exception e) {
        //handle exceptions
    }

    //mutableLiveDAta already can set values, so this method useless
    //left here for reference
    /*
    public void setListOfJioActivities(MutableLiveData<List<JioActivity>> listOfJioActivities) {
        this.listOfJioActivities = listOfJioActivities;
    }

     */

    //considering caching values that have been fetched from database
    /*  private LiveData<List<JioActivity>> allJioActivities;
        private Map<String, JioActivity> cache = new HashMap<String, JioActivity>();
    */


}
