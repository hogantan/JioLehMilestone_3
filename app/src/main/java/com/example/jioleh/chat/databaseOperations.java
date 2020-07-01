package com.example.jioleh.chat;

import com.example.jioleh.userprofile.UserProfile;

import java.util.List;

public interface databaseOperations {

    void userProfileDataAdded(List<?>[] allUserProfiles);

    void onError(Exception e);
}
