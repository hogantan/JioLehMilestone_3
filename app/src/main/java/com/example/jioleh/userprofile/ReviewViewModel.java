package com.example.jioleh.userprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ReviewViewModel extends ViewModel implements ReviewDataOps {

    private MutableLiveData<List<Review>> listOfReviews = new MutableLiveData<>();
    private ReviewRepository repository;

    public ReviewViewModel() {
        this.repository = new ReviewRepository(this);
    }

    public void getReviews(String uid) {
        this.repository.getReviews(uid);
    }

    LiveData<List<Review>> getListOfReviews() {
        return listOfReviews;
    }

    @Override
    public void reviewsAdded(List<Review> listOfReviews) {
        this.listOfReviews.setValue(listOfReviews);
    }

    @Override
    public void onError(Exception e) {
        //handle exceptions
    }

}
