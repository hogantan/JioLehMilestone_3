package com.example.jioleh.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jioleh.userprofile.UserProfile;

import java.util.List;

public class ChatFragmentViewModel extends ViewModel implements databaseOperations{

    private MutableLiveData<List<?>[]> listOfProfilesAndUids = new MutableLiveData<>();
    private ChatFragmentRepository repository;

    public ChatFragmentViewModel(String current_uid) {
        repository = new ChatFragmentRepository(this);
        repository.getUserProfiles(current_uid);
    }

    public LiveData<List<?>[]> getListOfUserProfiles() {
        return listOfProfilesAndUids;
    }

    @Override
    public void userProfileDataAdded(List<?>[] allUserProfiles) {
        listOfProfilesAndUids.setValue(allUserProfiles);
    }

    @Override
    public void onError(Exception e) {

    }
}
