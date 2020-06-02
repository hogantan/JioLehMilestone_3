package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserProfileViewPagerAdapter extends FragmentStateAdapter {


    public UserProfileViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                UserProfileAboutMeFragment tab1 = new UserProfileAboutMeFragment();
                return tab1;
            case 1:
                UserProfileListingsFragment tab2 = new UserProfileListingsFragment();
                return tab2;
            case 2:
                UserProfileReviewsFragment tab3 = new UserProfileReviewsFragment();
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}

