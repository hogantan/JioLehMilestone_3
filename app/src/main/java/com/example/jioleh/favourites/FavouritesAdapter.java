package com.example.jioleh.favourites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.listings.ViewJioActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesHolder> {

    private List<JioActivity> activities;
    private boolean deletable;

    public FavouritesAdapter() {
        activities = new ArrayList<>();
    }

    @NonNull
    @Override
    public FavouritesAdapter.FavouritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View activityBox = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourites_item_left, parent, false);;

        // here we override the inflated view's height to be half the recyclerview size
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) activityBox.getLayoutParams();
        layoutParams.width = (parent.getWidth() / 2) - layoutParams.leftMargin - layoutParams.rightMargin;
        activityBox.setLayoutParams(layoutParams);

        return new FavouritesHolder(activityBox);
    }


    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.FavouritesHolder holder, int position) {
        JioActivity activity = activities.get(position);
        if (activity != null) {
            holder.activity_id = activity.getActivityId();
            holder.setUpView(activity);
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    //Used to determine who the receiver or sender is depending on current user

    public void setData(List<JioActivity> jioActivities, boolean deletable) {
        this.activities = jioActivities;
        this.deletable = deletable;
    }

     class FavouritesHolder extends RecyclerView.ViewHolder {

        private ImageView displayImage;
        private TextView displayTitle;
        private TextView date;
        private TextView time;
        private TextView location;
        private String activity_id;
         private Context currentContext;

        //Initialising the holder
        FavouritesHolder(@NonNull final View itemView) {
            super(itemView);
            displayImage = itemView.findViewById(R.id.ivFavouritesImage);
            displayTitle = itemView.findViewById(R.id.tvFavouritesTitle);
            date = itemView.findViewById(R.id.tvFavouritesDate);
            time = itemView.findViewById(R.id.tvFavouritesTime);
            location = itemView.findViewById(R.id.tvFavouritesLocation);
            currentContext = displayImage.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(itemView.getContext(), ViewJioActivity.class);
                    nextActivity.putExtra("activity_id", activity_id);
                    itemView.getContext().startActivity(nextActivity);
                }
            });

            if (FavouritesAdapter.this.deletable) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        alertDialog();
                        return true;
                    }
                });
            }
        }

        //Setting the details in the holder
        void setUpView(JioActivity jioActivity) {
            if (!jioActivity.getImageUrl().equals("") && jioActivity.getImageUrl()!=null) {
                Picasso.get().load(jioActivity.getImageUrl()).into(displayImage);
            }
            displayTitle.setText(jioActivity.getTitle());
            date.setText(convertDateFormat(jioActivity.getEvent_date()));
            time.setText(jioActivity.getEvent_time());
            location.setText(jioActivity.getLocation());
        }

        private String convertDateFormat(String date) {
            Date new_date;
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
            try {
                new_date = formatter.parse(date);
                return formatter2.format(new_date);
            } catch (ParseException e) {
                return e.getLocalizedMessage();
            }
        }

        private void alertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage("Do you want to delete this activity?")
                    .setTitle("Delete Activity");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final FirebaseFirestore datastore = FirebaseFirestore.getInstance();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    datastore.collection("activities")
                            .document(activity_id)
                            .delete();

                    datastore.collection("users")
                            .document(currentUser.getUid())
                            .collection("activities_listed")
                            .document(activity_id)
                            .delete();
                }
            });

            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

}
