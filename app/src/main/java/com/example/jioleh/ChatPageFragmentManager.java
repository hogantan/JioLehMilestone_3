package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ChatPageFragmentManager extends FragmentPagerAdapter {

    private int number_of_tabs;

    public ChatPageFragmentManager(@NonNull FragmentManager fm, int number_of_tabs) {
        super(fm);
        this.number_of_tabs = number_of_tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return new FriendRequestsFragment();
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return this.number_of_tabs;
    }
}
