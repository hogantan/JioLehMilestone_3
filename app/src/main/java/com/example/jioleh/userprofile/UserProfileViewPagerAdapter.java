package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class UserProfileViewPagerAdapter extends FragmentStateAdapter {


    public UserProfileViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserProfileAboutMeFragment();
            case 1:
                return new UserProfileListingsFragment();
            case 2:
                return new UserProfileReviewsFragment();
            default:
                return null;
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}

