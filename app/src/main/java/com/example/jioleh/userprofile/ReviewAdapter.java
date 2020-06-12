package com.example.jioleh.userprofile;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {


    private List<Review> lst;

    public ReviewAdapter(List<Review> lst){
        this.lst = lst;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row,parent,false);
        return new ReviewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Review review = lst.get(position);
        String UImg = review.getFrom_userImg();

        if (!UImg.equals("") && UImg != null) {
            Picasso.get().load(UImg).into(holder.iv_reviewer_profilePic);
        }

        holder.review_words.setText(review.getWordsOfReview());
        holder.reviewer_username.setText(review.getFrom_username());
        //holder.date.setText(timeStampToString(review.getTimeOfPost()));

    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    public ReviewAdapter() {
    }

    class ReviewHolder extends RecyclerView.ViewHolder{

        ImageView iv_reviewer_profilePic;
        TextView review_words, reviewer_username, date;

        ReviewHolder(@NonNull View itemView) {
            super(itemView);
            iv_reviewer_profilePic = itemView.findViewById(R.id.comment_user_img);
            review_words = itemView.findViewById(R.id.comment_content);
            reviewer_username = itemView.findViewById(R.id.comment_username);
            date = itemView.findViewById(R.id.comment_date);
        }
    }

    private  String timeStampToString(Date date) {

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String ans =df.format("hh:mm",date).toString();
        return ans;
    }

    public void setData(List<Review> lst ) {
        this.lst = lst;
    }
}
