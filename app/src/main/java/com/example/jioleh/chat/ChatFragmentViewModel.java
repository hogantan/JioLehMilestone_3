package com.example.jioleh.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jioleh.userprofile.UserProfile;

import java.util.List;

public class ChatFragmentViewModel extends ViewModel implements databaseOperations{

    private MutableLiveData<List<ChatChannel>> listOfChatChannels = new MutableLiveData<>();
    private ChatFragmentRepository repository;
    private String current_uid;

    public ChatFragmentViewModel(String current_uid) {
        repository = new ChatFragmentRepository(this);
        repository.getChatChannels(current_uid);
        this.current_uid = current_uid;
    }

    public LiveData<List<ChatChannel>> getListOfChatChannels() {
        return listOfChatChannels;
    }

    public void refreshProfiles() {
        repository.getChatChannels(current_uid);
    }

    @Override
    public void chatChannelsDataAdded(List<ChatChannel> allChatChannels) {
        listOfChatChannels.setValue(allChatChannels);
    }

    @Override
    public void onError(Exception e) {

    }
}
