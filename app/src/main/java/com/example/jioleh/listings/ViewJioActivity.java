package com.example.jioleh.listings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.example.jioleh.userprofile.OtherUserView;
import com.example.jioleh.userprofile.UserProfile;
import com.example.jioleh.userprofile.YourOwnOtherUserView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//Flow of data in Firestore:
//Each user has a favourites collection which will consist of JioActivity IDs as documents
//Each document currently holds two fields, liked and joining that hold boolean values
//When join is click, toggle the liked field accordingly
//When Like is click, toggle the joining field accordingly
//When joining is click, go into activity and update number of current participants and list of current participants

public class ViewJioActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView displayImage;
    private TextView displayTitle;
    private TextView type_of_activity;
    private TextView location;
    private TextView host_name;
    private ImageView host_image;
    private TextView actual_date;
    private TextView actual_time;
    private TextView confirm_date;
    private TextView confirm_time;
    private TextView details;
    private ImageView editDetails;
    private TextView participants_counter;
    private TextView minimum;
    private View displayHost;
    private View displayParticipants;
    //private RecyclerView current_participants;
    private Button join;
    private Button like;
    private boolean buttonFlag;

    private Intent intent;
    private String activity_id;
    private String host_uid;
    private String host_imageUrl;
    private JioActivity currentActivity;

    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    //Use to set button color and text
    private static final int POSITIVE = 1;
    private static final int NEGATIVE = 2;

    //Use to determine how the update and set activity method functions
    private static final int JOIN = 1;
    private static final int LIKE = 2;

    //Use to determine how to update activity participants
    private static final int ADD = 1;
    private static final int REMOVE = 2;
    private int max_participants;
    private int current_participants;
    private ArrayList<String> list_of_participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jio);
        initialise();
        initialiseToolbar();
        //getStatus is called onCreate to determine what the visuals of the buttons are as well as to determine isFirstTime
        getButtonStatus(JOIN);
        getButtonStatus(LIKE);
        getActivityInfo();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fourth line of check when user clicks the join button
                if (checkIsExpired(currentActivity)) {
                    return;
                }

                if (checkIsCancelled(currentActivity)) {
                    return;
                }

                if (checkIsConfirmed(currentActivity)) {
                    return;
                }

                if (join.getText().toString().equals("Leave")) {
                    deleteActivity(JOIN);
                    setButtonVisuals(POSITIVE, join, JOIN);
                    updateParticipants(activity_id, REMOVE);
                } else {
                    setActivity(JOIN);
                    setButtonVisuals(NEGATIVE, join, JOIN);
                    updateParticipants(activity_id, ADD);
                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like.getHint().toString().equals("ekil")) {
                    deleteActivity(LIKE);
                    setButtonVisuals(POSITIVE, like, LIKE);
                } else {
                    setActivity(LIKE);
                    setButtonVisuals(NEGATIVE, like, LIKE);
                }
            }
        });

        displayParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(ViewJioActivity.this, ViewParticipants.class);
                nextActivity.putExtra("activity_id", activity_id);
                nextActivity.putExtra("host_uid", host_uid);
                startActivity(nextActivity);
            }
        });

        displayHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getUid().equals(host_uid)) {
                    Intent nextActivity = new Intent(ViewJioActivity.this, YourOwnOtherUserView.class);
                    nextActivity.putExtra("username", host_name.getText().toString());
                    nextActivity.putExtra("user_id", host_uid);
                    startActivity(nextActivity);
                } else {
                    Intent nextActivity = new Intent(ViewJioActivity.this, OtherUserView.class);
                    nextActivity.putExtra("username", host_name.getText().toString());
                    nextActivity.putExtra("user_id", host_uid);
                    startActivity(nextActivity);
                }
            }
        });

        if (currentUser.getUid().equals(host_uid)) {
            editDetails.setVisibility(ImageView.VISIBLE);
            editDetails.setClickable(true);

            editDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogEditDetails();
                }
            });
        }
    }

    private void  initialise() {
        displayImage = findViewById(R.id.ivViewDisplayImage);
        displayTitle = findViewById(R.id.tvViewDisplayTitle);
        type_of_activity = findViewById(R.id.tvViewDisplayTypeActivity);
        location = findViewById(R.id.tvViewDisplayLocation);
        host_name = findViewById(R.id.tvViewDisplayHostName);
        host_image = findViewById(R.id.civViewDisplayHostImage);
        actual_date = findViewById(R.id.tvViewDisplayActualDate);
        actual_time = findViewById(R.id.tvViewDisplayActualTime);
        confirm_date = findViewById(R.id.tvViewDisplayConfirmDate);
        confirm_time = findViewById(R.id.tvViewDisplayConfirmTime);
        details = findViewById(R.id.tvViewDisplayDetails);
        editDetails = findViewById(R.id.ivViewDisplayDetailsEdit);
        displayHost = findViewById(R.id.vViewDisplayHost);
        displayParticipants = findViewById(R.id.vViewDisplayParticipants);
        participants_counter = findViewById(R.id.tvViewDisplayParticipantsCounter);
        minimum = findViewById(R.id.tvViewDisplayMinimum);
        join = findViewById(R.id.btnViewJoin);
        like = findViewById(R.id.btnTopBarLike);
        buttonFlag = false;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        datastore = FirebaseFirestore.getInstance();

        //Fetching activity id from activity holder from the activity adapter class
        intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        datastore.collection("activities")
                .document(activity_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentActivity = documentSnapshot.toObject(JioActivity.class);

                        if (currentActivity.getReadParticipants().contains(currentUser.getUid())) {
                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("toRead", currentActivity.getToRead() - 1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (currentActivity.getToRead() == 0) {
                                                    datastore.collection("activities")
                                                            .document(activity_id)
                                                            .update("updated", false);
                                                }
                                            }
                                        }
                                    });

                            currentActivity.getReadParticipants().remove(currentUser.getUid());
                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("readParticipants", currentActivity.getReadParticipants());
                        }
                    }
                });
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

    //to prevent more than maximum participants
    //disables and enables the join button accordingly
    private void checkIsFull() {
        if (buttonFlag) {
            if (join.getText().toString().equals("Join")) {
                if (current_participants == max_participants) {
                    join.setEnabled(false);
                    join.setText("Full");
                    join.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegrey_button));
                }
            } else if (join.getText().toString().equals("Full")){
                if (current_participants < max_participants) {
                    join.setEnabled(true);
                    join.setText("Join");
                    join.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegreen_button));
                }
            } else {
                join.setEnabled(true);
            }
        }
    }

    //Second line of checking whether an activity has expired or not
    private boolean checkIsExpired(JioActivity currentActivity) {
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time
        if (currentActivity.getEvent_timestamp().before(currentDateTime)) {
            datastore.collection("activities")
                    .document(activity_id)
                    .update("expired", true);

            join.setEnabled(false);
            join.setText("Expired");
            join.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegrey_button));

            return true;
        } else {
            return false;
        }
    }

    //Second line of checking whether an activity is cancelled
    private boolean checkIsCancelled(JioActivity currentActivity) {
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time
        if (currentActivity.getDeadline_timestamp().before(currentDateTime)) {
            int minimum = currentActivity.getMin_participants();
            int current = current_participants;
            if (current < minimum) {
                datastore.collection("activities")
                        .document(activity_id)
                        .update("cancelled", true);
                join.setEnabled(false);
                join.setText("Cancelled");
                join.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basered_button));

                return true;
            }
        }
        return false;
    }

    //Second line of checking whether an activity is cancelled
    private boolean checkIsConfirmed(JioActivity currentActivity) {
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time
        if (currentActivity.getDeadline_timestamp().before(currentDateTime)) {
            int minimum = currentActivity.getMin_participants();
            int current = current_participants;
            if (current >= minimum) {
                datastore.collection("activities")
                        .document(activity_id)
                        .update("confirmed", true);
                join.setEnabled(false);
                join.setText("Confirmed");
                join.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegreen_button));

                return true;
            }
        }
        return false;
    }

    //Update number of current participants as well as the list of participants in the activity
    private void updateParticipants(final String activity_id, final int update) {
        int updatedNumberOfParticipants;
        ArrayList<String> updatedListParticipants;
        if (update == ADD) {
            updatedNumberOfParticipants = current_participants + 1;
            list_of_participants.add(currentUser.getUid());
            updatedListParticipants = list_of_participants;
        } else {
            updatedNumberOfParticipants = current_participants -1;
            list_of_participants.remove(currentUser.getUid());
            updatedListParticipants = list_of_participants;
        }

        datastore.collection("activities")
                .document(activity_id)
                .update("current_participants", updatedNumberOfParticipants);

        datastore.collection("activities")
                .document(activity_id)
                .update("participants", updatedListParticipants);
    }

    //To initialise collection and fields base on type if it did not exist
    private void setActivity(int type) {
        String collection_path = null;
        String field = "activity_id";
        if (type == JOIN) {
            collection_path = "joined";
        } else if (type == LIKE) {
            collection_path = "liked";
        } else {

        }
        HashMap<String, String> input_firestore = new HashMap<>();
        input_firestore.put(field, activity_id);
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection(collection_path)
                .document(activity_id)
                .set(input_firestore);
    }

    private void deleteActivity(int type) {
        String collection_path = null;
        String field = "activity_id";
        if (type == JOIN) {
            collection_path = "joined";
        } else if (type == LIKE) {
            collection_path = "liked";
        } else {

        }
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection(collection_path)
                .document(activity_id)
                .delete();
    }

    //To retrieve the user's current relationship with the activity and to set the
    //visuals of the like and join buttons accordingly
    private void getButtonStatus(final int type) {
        String collection_path = null;
        Button button = null;
        if (type == JOIN) {
            collection_path = "joined";
            button = join;
        } else if (type == LIKE) {
            collection_path = "liked";
            button = like;
        } else {
        }
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
                            setButtonVisuals(NEGATIVE, finalButton, type);
                        } else {
                            setButtonVisuals(POSITIVE, finalButton, type);
                        }
                        buttonFlag = true;
                        checkIsFull();
                        if (checkIsExpired(currentActivity)) {
                        } else {
                            checkIsCancelled(currentActivity);
                            checkIsConfirmed(currentActivity);
                        }
                    }
                });
    }

    //To retrieve data of the activity from firestore to set up the details of the UI variables
    private void getActivityInfo() {
        datastore.collection("activities")
                .document(activity_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        currentActivity = documentSnapshot.toObject(JioActivity.class);

                        if (currentActivity != null) {
                            if (!currentActivity.getImageUrl().equals("") && currentActivity.getImageUrl()!=null) {
                                Picasso.get().load(currentActivity.getImageUrl()).into(displayImage);
                            }

                            //live update of current participants as it listens for change in data
                            current_participants = currentActivity.getCurrent_participants();
                            list_of_participants = currentActivity.getParticipants();
                            max_participants = currentActivity.getMax_participants();

                            displayTitle.setText(currentActivity.getTitle());
                            type_of_activity.setText(currentActivity.getType_of_activity());
                            location.setText(currentActivity.getLocation());
                            actual_date.setText(convertDateFormat(currentActivity.getEvent_date()));
                            actual_time.setText(currentActivity.getEvent_time());
                            confirm_date.setText(convertDateFormat(currentActivity.getDeadline_date()));
                            confirm_time.setText(currentActivity.getEvent_time());
                            details.setText(currentActivity.getDetails());
                            participants_counter.setText(currentActivity.getCurrent_participants() + "/" + max_participants);
                            minimum.setText("Minimum required: " + currentActivity.getMin_participants() + "/" + currentActivity.getMax_participants());

                            host_uid = currentActivity.getHost_uid();
                            setUpHostInfo(host_uid);
                            checkIsFull(); //this will respond to live changes from the database

                            //Enable editing for host
                            if (currentUser.getUid().equals(host_uid)) {
                                editDetails.setVisibility(ImageView.VISIBLE);
                                editDetails.setClickable(true);

                                editDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialogEditDetails();
                                    }
                                });
                            }

                            getButtonStatus(JOIN);

                            //Second line of check
                            if (checkIsExpired(currentActivity)) {
                            } else {
                                checkIsCancelled(currentActivity);
                                checkIsConfirmed(currentActivity);
                            }
                        } else {
                            //This listens to activities being deleted
                            datastore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("joined")
                                    .document(activity_id)
                                    .delete();

                            datastore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("liked")
                                    .document(activity_id)
                                    .delete();
                        }
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
                        host_imageUrl = current_user.getImageUrl();

                        if (!host_imageUrl.equals("") && host_imageUrl!=null) {
                            Picasso.get().load(host_imageUrl).into(host_image);
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


    //Method to determine how the like and join button looks based one the direction and type
    private void setButtonVisuals(int direction, Button button, int type) {
        String text = null;
        String textInverse = null;
        if (type == JOIN) {
            text = "Join";
            textInverse = "Leave";
            if (direction == NEGATIVE) {
                button.setText(textInverse);
                button.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basered_button));
            } else {
                button.setText(text);
                button.setBackground(getResources().getDrawable(R.drawable.slightly_rounded_basegreen_button));
            }

        } else {
            text = "Like";
            textInverse = "ekil";
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

    private void alertDialogEditDetails() {
        final View view = getLayoutInflater().inflate(R.layout.alertdialog_edittext, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Addtional Details");

        AlertDialog dialog = builder.create();

        TextInputEditText textInputEditText = view.findViewById(R.id.etDialogText);
        textInputEditText.setText(details.getText().toString());

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datastore.collection("activities")
                        .document(activity_id)
                        .update("details", textInputEditText.getText().toString());

                datastore.collection("activities")
                        .document(activity_id)
                        .update("updated", true);

                datastore.collection("activities")
                        .document(activity_id)
                        .update("toRead", current_participants);

                datastore.collection("activities")
                        .document(activity_id)
                        .update("readParticipants", currentActivity.getParticipants());
            }
        });

        dialog.setView(view);
        dialog.show();
    }
}