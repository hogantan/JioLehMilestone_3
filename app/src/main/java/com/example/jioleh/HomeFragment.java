package com.example.jioleh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private Button findActivities;
    private Button postActivities;
    private Button findNearBy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home,container,false);

        findActivities = view.findViewById(R.id.btn_findActivities);
        postActivities = view.findViewById(R.id.btn_postActivities);
        findNearBy = view.findViewById(R.id.btn_findNearby);

        findActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postingPage = new Intent(getActivity(), FindActivitiesPage.class);
                startActivity(postingPage);
            }
        });
        postActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postingPage = new Intent(getActivity(), PostActivitiesPage.class);
                startActivity(postingPage);
            }
        });

        return view;
    }
}
