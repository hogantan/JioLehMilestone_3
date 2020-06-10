package com.example.jioleh.listings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//Flow of data in Firestore:
//Each user has a favourites collection which will consist of JioActivity IDs as documents
//Each document currently holds two fields, liked and attending that hold boolean values
//When attend is click, toggle the liked field accordingly
//When Like is click, toggle the attending field accordingly
//When attend is click, go into activity and update number of current participants and list of current participants

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
    private Button attend;
    private Button like;

    private Intent intent;
    private String activity_id;

    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    private boolean isFirstTime; //used to determine whether the user has a record of this activity in its collection

    //Use to set button color and text
    private static final int POSITIVE = 1;
    private static final int NEGATIVE = 2;

    //Use to determine how the update and set activity method functions
    private static final int ATTEND = 1;
    private static final int LIKE = 2;

    //Use to determine how to update activity participants
    private static final int ADD = 1;
    private static final int REMOVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jio);
        initialise();
        initialiseToolbar();
        getActivityInfo();

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attend.getText().toString().equals("Attend") && isFirstTime) {
                    setActivity(ATTEND); //set instead of update because first time no data
                    setButtonVisuals(NEGATIVE, attend, ATTEND);
                    updateParticipants(activity_id, ADD);
                } else if (attend.getText().toString().equals("Unattend")){
                    updateActivity(ATTEND, false);
                    setButtonVisuals(POSITIVE, attend, ATTEND);
                    updateParticipants(activity_id, REMOVE);
                } else {
                    updateActivity(ATTEND, true);
                    setButtonVisuals(NEGATIVE, attend, ATTEND);
                    updateParticipants(activity_id, ADD);
                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like.getHint().toString().equals("Like") && isFirstTime) {
                    setActivity(LIKE);
                    setButtonVisuals(NEGATIVE, like, LIKE);
                    System.out.println("here");
                } else if (like.getHint().toString().equals("Unlike")){
                    updateActivity(LIKE, false);
                    setButtonVisuals(POSITIVE, like, LIKE);
                    System.out.println("here2");
                } else {
                    updateActivity(LIKE, true);
                    setButtonVisuals(NEGATIVE, like, LIKE);
                    System.out.println("here3");
                }
            }
        });
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
        attend = findViewById(R.id.btnViewAttend);
        like = findViewById(R.id.btnTopBarLike);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();

        //Fetching activity id from activity holder from the activity adapter class
        intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");

        isFirstTime = false;

        //getStatus is called onCreate to determine what the visuals of the buttons are as well as to determine isFirstTime
        getStatusAttendOrLike(ATTEND);
        getStatusAttendOrLike(LIKE);
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

    //Update number of current participants as well as the list of participants in the activity
    private void updateParticipants(final String activity_id, final int update) {
        datastore.collection("activities")
                .document(activity_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        JioActivity current_activity = documentSnapshot.toObject(JioActivity.class);
                        int updatedNumberOfParticipants = 0;
                        ArrayList<String> updatedListParticipants = new ArrayList<>();
                        int number_participants = current_activity.getCurrent_participants();
                        ArrayList<String> list_participants = current_activity.getParticipants();
                        if (update == ADD) {
                            updatedNumberOfParticipants = number_participants + 1;
                            list_participants.add(currentUser.getUid());
                            updatedListParticipants = list_participants;
                        } else {
                            updatedNumberOfParticipants = number_participants -1;
                            list_participants.remove(currentUser.getUid());
                            updatedListParticipants = list_participants;
                        }

                        datastore.collection("activities")
                                .document(activity_id)
                                .update("current_participants", updatedNumberOfParticipants);

                        datastore.collection("activities")
                                .document(activity_id)
                                .update("participants", updatedListParticipants);
                    }
                });
    }


    //Update the fields of favourite collection based on type
    private void updateActivity(int type, boolean field_input) {
        String collection_path = "favourites";
        String field = null;
        boolean bool_input = field_input;
        if (type == ATTEND) {
            field = "attending";
        } else if (type == LIKE) {
            field = "liked";
        } else {

        }
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection(collection_path)
                .document(activity_id)
                .update(field, bool_input);
    }

    //To initialise collection and fields base on type if it did not exist
    private void setActivity(int type) {
        String collection_path = "favourites";
        String field = null;
        if (type == ATTEND) {
            field = "attending";
        } else if (type == LIKE) {
            field = "liked";
        } else {

        }
        HashMap<String, Boolean> input_firestore = new HashMap<>();
        input_firestore.put(field, true);
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection(collection_path)
                .document(activity_id)
                .set(input_firestore);
        setIsFirstTime(false);
    }

    //To retrieve the user's current relationship with the activity and to set the
    //visuals of the like and attend buttons accordingly
    private void getStatusAttendOrLike(final int type) {
        String collection_path = "favourites";
        String field = null;
        Button button = null;
        if (type == ATTEND) {
            field = "attending";
            button = attend;
        } else if (type == LIKE) {
            field = "liked";
            button = like;
        } else {
        }
        final String finalField = field;
        final Button finalButton = button;
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection(collection_path)
                .document(activity_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Boolean field_bool = documentSnapshot.getBoolean(finalField);
                            if (field_bool == null) {
                                setButtonVisuals(POSITIVE, finalButton, type);
                            } else if (field_bool.equals(true)) {
                                setButtonVisuals(NEGATIVE, finalButton, type);
                            } else {
                                setButtonVisuals(POSITIVE, finalButton, type);
                            }
                        } else {
                            setIsFirstTime(true);
                            setButtonVisuals(POSITIVE, finalButton, type);
                        }
                    }
                });
    }

    private void setIsFirstTime(boolean bool) {
        isFirstTime = bool;
    }

    //To retrieve data of the activity from firestore to set up the details of the UI variables
    private void getActivityInfo() {
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


    //To retrieve of the Host(person who posted the activity) and to set up and display the necessary details of the UI variables
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

    //Method to convert how date looks --> current format allows date to be seen as e.g. 24 June 2012
    private String convertDateFormat(String date) {
        Date new_date = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            new_date = formatter.parse(date);
            return formatter.format(new_date);
        } catch (ParseException e) {
            return e.getLocalizedMessage();
        }
    }


    //Method to determine how the like and attend button looks based one the direction and type
    private void setButtonVisuals(int direction, Button button, int type) {
        String text = null;
        String textInverse = null;
        if (type == ATTEND) {
            text = "Attend";
            textInverse = "Unattend";
            if (direction == NEGATIVE) {
                button.setText(textInverse);
                button.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basered_button));
            } else {
                button.setText(text);
                button.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegreen_button));
            }

        } else {
            text = "Like";
            textInverse = "Unlike";
            if (direction == NEGATIVE) {
                button.setHint(textInverse);
                Drawable img = getResources().getDrawable(R.drawable.ic_baseline_thumb_up_basegreen);
                img.setBounds(0, 0, 40, 40);
                button.setCompoundDrawables(img, null, null, null);
                button.setTextColor(getResources().getColor(R.color.baseGreen));
            } else {
                button.setHint(text);
                Drawable img = getResources().getDrawable(R.drawable.ic_baseline_thumb_up_white);
                img.setBounds(0, 0, 40, 40);
                button.setCompoundDrawables(img, null, null, null);
                button.setTextColor(getResources().getColor(R.color.baseWhite));
            }
        }
    }
}