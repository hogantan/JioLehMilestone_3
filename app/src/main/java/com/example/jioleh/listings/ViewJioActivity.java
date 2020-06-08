package com.example.jioleh.listings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewJioActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView displayImage;
    private TextView displayTitle;
    private TextView type_of_activity;
    private TextView location;
    private TextView host_name;
    private ImageView host_image;
    private TextView actual_datetime;
    private TextView confirm_datetime;
    private TextView details;
    private TextView participants_counter;
    private TextView minimum;
    private RecyclerView current_participants;

    private Intent intent;

    private FirebaseFirestore datastore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jio);
        initialise();
        initialiseToolbar();
        getActivityInfo();
    }

    private void  initialise() {
        displayImage = findViewById(R.id.ivViewDisplayImage);
        displayTitle = findViewById(R.id.tvViewDisplayTitle);
        type_of_activity = findViewById(R.id.tvViewDisplayTypeActivity);
        location = findViewById(R.id.tvViewDisplayLocation);
        host_name = findViewById(R.id.tvViewDisplayHostName);
        host_image = findViewById(R.id.civViewDisplayHostImage);
        actual_datetime = findViewById(R.id.tvViewDisplayActualDateTime);
        confirm_datetime = findViewById(R.id.tvViewDisplayConfirmDateTime);
        details = findViewById(R.id.tvViewDisplayDetails);
        current_participants = findViewById(R.id.rvViewDisplayCurrentParticipants);
        participants_counter = findViewById(R.id.tvViewDisplayParticipantsCounter);
        minimum = findViewById(R.id.tvViewDisplayMinimum);

        datastore = FirebaseFirestore.getInstance();

        intent = getIntent();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getActivityInfo() {
        String activity_id = intent.getStringExtra("activity_id");

        datastore.collection("activities")
                .document(activity_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        JioActivity current_activity = documentSnapshot.toObject(JioActivity.class);

                        if (!current_activity.getImageUrl().equals("") && current_activity.getImageUrl()!=null) {
                            Picasso.get().load(current_activity.getImageUrl()).into(displayImage);
                        }

                        displayTitle.setText(current_activity.getTitle());
                        type_of_activity.setText(current_activity.getType_of_activity());
                        location.setText(current_activity.getLocation());
                        host_name.setText(current_activity.getHost_uid());
                        actual_datetime.setText("Date: " + convertDateFormat(current_activity.getEvent_date())
                                + "\n" + "\n" + "Time: " +current_activity.getEvent_time());
                        confirm_datetime.setText("Date: " + convertDateFormat(current_activity.getDeadline_date())
                                + "\n" + "\n" + "Time: " + current_activity.getEvent_time());
                        details.setText(current_activity.getDetails());
                        participants_counter.setText(current_activity.getCurrent_participants() + "/" + current_activity.getMax_participants());
                        minimum.setText("Minimum required: " + current_activity.getMin_participants());

                        setUpHostInfo(current_activity.getHost_uid());
                    }
                });
    }

    private void setUpHostInfo(String uid) {
        datastore.collection("users")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        UserProfile current_user = documentSnapshot.toObject(UserProfile.class);

                        if (!current_user.getImageUrl().equals("") && current_user.getImageUrl()!=null) {
                            Picasso.get().load(current_user.getImageUrl()).into(host_image);
                        }

                        host_name.setText(current_user.getUsername());
                    }
                });
    }

    private String convertDateFormat(String date) {
        Date new_date = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
        try {
            new_date = formatter.parse(date);
            return formatter2.format(new_date);
        } catch (ParseException e) {
            return e.getLocalizedMessage();
        }
    }
}