package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class UserProfileViewPagerAdapter extends FragmentStateAdapter {

    String uid;

    public UserProfileViewPagerAdapter(@NonNull FragmentActivity fa, String uid) {
        super(fa);
        this.uid = uid;
    }
    public UserProfileViewPagerAdapter(@NonNull Fragment fragment, String uid) {
        super(fragment);
        this.uid = uid;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserProfileAboutMeFragment(uid);
            case 1:
                return new UserProfileListingsFragment();
            case 2:
                return new UserProfileReviewsFragment(uid);
            default:
                return null;
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}

