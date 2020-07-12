package com.example.jioleh.favourites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import org.w3c.dom.Text;

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
    private boolean removable;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        holder.setIsRecyclable(false);

        JioActivity activity = activities.get(position);
        if (activity != null) {
            holder.activity_id = activity.getActivityId();
            holder.host_uid = activity.getHost_uid();
            holder.setUpView(activity);
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //Used to determine who the receiver or sender is depending on current user

    public void setData(List<JioActivity> jioActivities, boolean deletable, boolean removable) {
        this.activities = jioActivities;
        this.deletable = deletable;
        this.removable = removable;
    }

     class FavouritesHolder extends RecyclerView.ViewHolder {

        private TextView expired;
        private TextView cancelled;
        private TextView confirmed;
        private ImageView displayImage;
        private TextView displayTitle;
        private TextView date;
        private TextView time;
        private TextView location;
        private TextView update;
        private String activity_id;
        private String host_uid;
        private Context currentContext;

        //Initialising the holder
        FavouritesHolder(@NonNull final View itemView) {
            super(itemView);
            expired = itemView.findViewById(R.id.tvExpired);
            cancelled = itemView.findViewById(R.id.tvCancel);
            confirmed = itemView.findViewById(R.id.tvConfirm);
            displayImage = itemView.findViewById(R.id.ivFavouritesImage);
            displayTitle = itemView.findViewById(R.id.tvFavouritesTitle);
            date = itemView.findViewById(R.id.tvFavouritesDate);
            time = itemView.findViewById(R.id.tvFavouritesTime);
            location = itemView.findViewById(R.id.tvFavouritesLocation);
            update = itemView.findViewById(R.id.tvNewUpdates);
            currentContext = displayImage.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check whether the activity exists
                    FirebaseFirestore.getInstance()
                            .collection("activities")
                            .document(activity_id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Intent nextActivity = new Intent(itemView.getContext(), ViewJioActivity.class);
                                        nextActivity.putExtra("activity_id", activity_id);
                                        nextActivity.putExtra("host_uid", host_uid);
                                        itemView.getContext().startActivity(nextActivity);
                                    } else {
                                        alertDialogDoesNotExist();
                                    }
                                }
                            });
                }
            });

            if (FavouritesAdapter.this.deletable) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        alertDialogDelete();
                        return true;
                    }
                });
            }

            if (FavouritesAdapter.this.removable) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (cancelled.getVisibility() == TextView.VISIBLE || expired.getVisibility() == TextView.VISIBLE) {
                            alertDialogRemove();
                            return true;
                        } else {
                            return  false;
                        }
                    }
                });
            }
        }

        //Setting the details in the holder
        void setUpView(JioActivity jioActivity) {
            setVisibility(jioActivity);

            if (!jioActivity.getImageUrl().equals("") && jioActivity.getImageUrl()!=null) {
                Picasso.get().load(jioActivity.getImageUrl()).into(displayImage);
            }

            if (jioActivity.isUpdated() && jioActivity.getReadParticipants().contains(currentUser.getUid())) {
                update.setVisibility(TextView.VISIBLE);
            } else {
                update.setVisibility(TextView.INVISIBLE);
            }

            displayTitle.setText(jioActivity.getTitle());
            date.setText(convertDateFormat(jioActivity.getEvent_date()));
            time.setText(jioActivity.getEvent_time());
            location.setText(jioActivity.getLocation());
        }

        private String convertDateFormat(String date) {
            Date new_date;
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
            try {
                new_date = formatter.parse(date);
                return formatter2.format(new_date);
            } catch (ParseException e) {
                return e.getLocalizedMessage();
            }
        }

        private void setVisibility(JioActivity jioActivity) {
            if (jioActivity.isExpired()) {
                confirmed.setVisibility(TextView.INVISIBLE);
                cancelled.setVisibility(TextView.INVISIBLE);
                expired.setVisibility(TextView.VISIBLE);
                return;
            }

            if (jioActivity.isCancelled()) {
                expired.setVisibility(TextView.INVISIBLE);
                confirmed.setVisibility(TextView.INVISIBLE);
                cancelled.setVisibility(TextView.VISIBLE);
                return;
            }

            if (jioActivity.isConfirmed()) {
                expired.setVisibility(TextView.INVISIBLE);
                cancelled.setVisibility(TextView.INVISIBLE);
                confirmed.setVisibility(TextView.VISIBLE);
                return;
            }

            confirmed.setVisibility(TextView.INVISIBLE);
            cancelled.setVisibility(TextView.INVISIBLE);
            expired.setVisibility(TextView.INVISIBLE);
        }

        private void alertDialogDelete() {
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

        private void alertDialogRemove() {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage("Do you want to remove this activity?")
                    .setTitle("Remove Activity");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final FirebaseFirestore datastore = FirebaseFirestore.getInstance();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    datastore.collection("users")
                            .document(currentUser.getUid())
                            .collection("joined")
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

        private void alertDialogDoesNotExist() {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage(displayTitle.getText().toString() + " has been removed.")
                    .setTitle("Activity Missing");

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }
}
