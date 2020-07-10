package com.example.jioleh.chat;

import com.example.jioleh.userprofile.UserProfile;

import java.util.List;

public interface databaseOperations {

    void chatChannelsDataAdded(List<ChatChannel> allChatChannels);

    void onError(Exception e);
}
