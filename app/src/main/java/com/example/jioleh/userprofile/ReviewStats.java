package com.example.jioleh.userprofile;

public class ReviewStats {

    private float averageRating = 0;
    private float totalRating = 0;
    private int totalNumOfReviews = 0;

    public ReviewStats() {
        //required constructor for firestore
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getTotalNumOfReviews() {
        return totalNumOfReviews;
    }

    public float getTotalRating() {
        return totalRating;
    }

    private void addOneReview() {
        this.totalNumOfReviews++;
    }

    public void addRating(float rating) {
        this.totalRating += rating;
        addOneReview();
        calculateAverageRating();
    }

    private void calculateAverageRating() {
        this.averageRating = totalRating/totalNumOfReviews;
    }



}
