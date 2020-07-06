package com.example.jioleh.userprofile;

import com.example.jioleh.listings.JioActivity;

import java.util.List;

public interface ReviewDataOps {

    void reviewsAdded(List<Review> listOfReviews);

    void onError(Exception e);
}
