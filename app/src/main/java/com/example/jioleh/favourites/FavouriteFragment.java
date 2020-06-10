package com.example.jioleh.favourites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.jioleh.R;
import com.example.jioleh.chat.ChatFragment;
import com.example.jioleh.listings.HomeFragment;
import com.google.android.material.tabs.TabLayout;

public class FavouriteFragment extends Fragment {

    private View currentView;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private JoinLikePagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.favourite_fragment,container, false);
        initialise();
        return currentView;
    }

    private void initialise() {
        tabLayout = currentView.findViewById(R.id.tlJoinedLiked);
        viewPager = currentView.findViewById(R.id.vpFavouriteViewPager);
        pagerAdapter = new JoinLikePagerAdapter(getChildFragmentManager(),2);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
