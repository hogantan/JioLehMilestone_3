package com.example.jioleh.userprofile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jioleh.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileAboutMeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileAboutMeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tv_bio;
    private TextView tv_interests;
    private TextView tv_location;
    private TextView tv_contact;

    private String uid;

    public UserProfileAboutMeFragment() {
        // Required empty public constructor
    }

    public UserProfileAboutMeFragment(String uid) {
        // this is for user profile other view
        this.uid = uid;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileAboutMeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileAboutMeFragment newInstance(String param1, String param2) {
        UserProfileAboutMeFragment fragment = new UserProfileAboutMeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_about_me, container, false);

        tv_bio = view.findViewById(R.id.bio_fill);
        tv_interests = view.findViewById(R.id.Interest_box);
        tv_location = view.findViewById(R.id.tvAboutMeLocation);
        tv_contact = view.findViewById(R.id.tvAboutMeContact);


        userProfileViewModel viewModel = new ViewModelProvider(this).get(userProfileViewModel.class);

        viewModel.getUser(uid).observe(getViewLifecycleOwner(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                //fill up Bio and Interest box
                fillBioAndInterest(userProfile);
            }
        });


        return view;
    }

    private void fillBioAndInterest(UserProfile userProfile) {
        tv_bio.setText(userProfile.getBio());
        tv_interests.setText(userProfile.getInterests());
        tv_location.setText(userProfile.getLocation());
        tv_contact.setText(userProfile.getContact());
    }
}
