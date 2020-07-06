package com.example.jioleh.listings;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityHolder> {


    private List<JioActivity> activities;

    public ActivityAdapter() {
        activities = new ArrayList<>();
    }

    @NonNull
    @Override
    public ActivityAdapter.ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View activityBox = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_activity_layout, parent, false);
        return new ActivityHolder(activityBox);
    }


    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ActivityHolder holder, int position) {
        JioActivity activity = activities.get(position);
        holder.activity_id = activity.getActivityId();
        holder.setUpView(activity);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }


    public void setData(List<JioActivity> jioActivities) {

            this.activities = jioActivities;

        //can do a submitList with listAdapter to diff changes
        //so that only item that change is updated
        //current implementation is that the whole list will be refreshed

        //notifying data changes done in observer in fragment
        //notifyDataSetChanged();
    }




    static class ActivityHolder extends RecyclerView.ViewHolder {

        private ImageView displayImage;
        private TextView displayTitle;
        private TextView date;
        private TextView time;
        private TextView location;
        private TextView currentParticipants;
        private String activity_id;

        //Initialising the holder
        ActivityHolder(@NonNull final View itemView) {
            super(itemView);
            displayImage = itemView.findViewById(R.id.ivHomeActivityDisplayImage);
            displayTitle = itemView.findViewById(R.id.tvHomeActivityDisplayTitle);
            date = itemView.findViewById(R.id.tvHomeActivityDisplayDate);
            time = itemView.findViewById(R.id.tvHomeActivityDisplayTime);
            location = itemView.findViewById(R.id.tvHomeActivityDisplayLocation);
            currentParticipants = itemView.findViewById(R.id.tvHomeActivityCurrentParticipants);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(itemView.getContext(), ViewJioActivity.class);
                    nextActivity.putExtra("activity_id", activity_id);
                    itemView.getContext().startActivity(nextActivity);
                }
            });
        }

        //Setting the details in the holder
        void setUpView(JioActivity jioActivity) {
            if (jioActivity.isExpired()) {

            } else {
                if (!jioActivity.getImageUrl().equals("") && jioActivity.getImageUrl()!=null) {
                    Picasso.get().load(jioActivity.getImageUrl()).into(displayImage);
                }
                displayTitle.setText(jioActivity.getTitle());
                date.setText(convertDateFormat(jioActivity.getEvent_date()));
                time.setText(jioActivity.getEvent_time());
                location.setText(jioActivity.getLocation());
                currentParticipants.setText(jioActivity.getCurrent_participants() + "/" + jioActivity.getMax_participants());
            }
        }

        private String convertDateFormat(String date) {
            Date new_date = new Date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
            try {
                new_date = formatter.parse(date);
                return formatter2.format(new_date);
            } catch (ParseException e) {
                return e.getLocalizedMessage();
            }
        }
    }

}
