package com.example.jioleh.favourites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class JoinLikePagerAdapter extends FragmentPagerAdapter {
    private int numTabs;

    public JoinLikePagerAdapter(@NonNull FragmentManager fm, int numTabs){
        super(fm);
        this.numTabs = numTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new JoinedFragment();
            case 1:
                return new LikedFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Joined";
            case 1:
                return "Liked";
            default:
                return null;
        }
    }
}
