package com.example.jioleh.userprofile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Review> lst;
    private float avg;
    private String uid;
    private String viewer_uid;
    private ViewGroup viewGroup;

    public ReviewAdapter(){
        this.lst = new ArrayList<>();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setViewer_uid(String viewer_uid) {
        this.viewer_uid = viewer_uid;
    }

    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
            return new ReviewHolder(row);
        } else if(viewType == TYPE_HEADER) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_header_average_rating, parent, false);
            return new ReviewHeader(row);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReviewHeader) {
            calculateAverage();
            ReviewHeader head = (ReviewHeader) holder;
            head.rb.setRating(avg);
            head.totalReviews.setText(lst.size() + " Ratings");
            head.avgRating.setText(String.format("%.2f",avg));

        } else if (holder instanceof ReviewHolder) {
            ReviewHolder rv = (ReviewHolder) holder;

            Review review = lst.get(position-1);
            String UImg = review.getFrom_userImg();

            if (!UImg.equals("") && UImg != null) {
                Picasso.get().load(UImg).into(rv.iv_reviewer_profilePic);
            }

            // Server time stamp from firestore may be null
            // because the task may not be completed yet but
            // local changes continues first
            if(review.getTimeOfPost()!=null) {
                rv.date.setText(timeStampToString(review.getTimeOfPost()));
            }

            rv.review_words.setText(review.getWordsOfReview());
            rv.reviewer_username.setText(review.getFrom_username());
            rv.review_rating.setText(String.format("%.1f", (review.getRating())));
            rv.reviewer_uid = review.getFrom_uid();

            if (review.getFrom_uid().equals(this.viewer_uid)) {
                rv.delete_review.setVisibility(View.VISIBLE);
                rv.delete_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(viewGroup.getContext())
                                .setTitle("Delete Review")
                                .setMessage("Do you want to delete this review?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(uid)
                                                .collection("Reviews")
                                                .document(review.getDocumentId())
                                                .delete();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }


                });


            }


        }

    }

    @Override
    public int getItemCount() {
        return lst.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void calculateAverage() {
        avg= 0;
        for (Review review: lst) {
            avg += review.getRating();

        }
        if(lst.size()!=0 && lst.size()!=1) {
            avg = avg / (lst.size());
        }
    }

    private  String timeStampToString(Date date) {
        String ans = DateFormat.format("dd-MM-yy",date).toString();
        return ans;
    }

    public void setData(List<Review> lst ) {
        this.lst = lst;
    }

    static class ReviewHolder extends RecyclerView.ViewHolder{

        ImageView iv_reviewer_profilePic;
        TextView review_words, reviewer_username, date,review_rating,delete_review;
        String reviewer_uid;

        ReviewHolder(@NonNull View itemView) {
            super(itemView);
            iv_reviewer_profilePic = itemView.findViewById(R.id.review_row_user_img);
            review_words = itemView.findViewById(R.id.comment_content);
            reviewer_username = itemView.findViewById(R.id.review_row_username);
            date = itemView.findViewById(R.id.review_row_date);
            review_rating = itemView.findViewById(R.id.review_row_rating);
            delete_review = itemView.findViewById(R.id.review_delete);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context currentContext = reviewer_username.getContext();
                    Intent nextActivity;
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (myUid.equals(reviewer_uid)){
                        // dont do anything since users clicking on their own name
                    } else {
                        nextActivity = new Intent(currentContext, OtherUserView.class);
                        nextActivity.putExtra("username", reviewer_username.getText().toString());
                        nextActivity.putExtra("user_id", reviewer_uid);
                        currentContext.startActivity(nextActivity);
                    }

                }
            });
        }

    }

    static class ReviewHeader extends RecyclerView.ViewHolder{

        RatingBar rb;
        TextView totalReviews;
        TextView avgRating;

        public ReviewHeader(@NonNull View itemView) {
            super(itemView);
            this.totalReviews = itemView.findViewById(R.id.review_header_numOfRatings);
            this.avgRating = itemView.findViewById(R.id.review_header_averageRating);
            this.rb = itemView.findViewById(R.id.review_average_ratingBar);
        }
    }


}
