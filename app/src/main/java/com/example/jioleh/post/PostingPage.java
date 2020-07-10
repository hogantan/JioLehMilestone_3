package com.example.jioleh.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jioleh.location.LocationPicker;
import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PostingPage
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    //Dialog constants
    private static int ALERT_FIELDS = 1;
    private static int ALERT_MINMAX = 2;
    private static int ALERT_DATETIME = 3;

    //Image
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri = null;

    //Firebase
    private StorageReference storageReference;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageTask uploadTask;

    //Location Picker
    private final int PICK_LOCATION_REQUEST = 2;

    private Toolbar toolbar;
    private Button confirmPost;
    private TextInputLayout title_of_post;
    private ImageView displayImage;
    private ImageButton removeImage;
    private Spinner type_of_activity;
    private String spinner_input = null;
    private TextView location;
    private Button btn_select_location;
    private TextView time_of_activity;
    private TextView date_of_activity;
    private Button setTime;
    private Button setDate;
    private TextView time_of_deadline;
    private TextView date_of_deadline;
    private Button setTimeDeadline;
    private Button setDateDeadline;
    private EditText min_participants;
    private EditText max_participants;
    private EditText additional_details;

    private int currentHour;
    private int currentMinute;
    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_page);

        initialise();
        initialiseSpinners();
        initialiseToolbar();

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time_of_activity);
            }
        });

        time_of_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time_of_activity);
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date_of_activity);
            }
        });

        date_of_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date_of_activity);
            }
        });

        setTimeDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time_of_deadline);
            }
        });

        time_of_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time_of_deadline);
            }
        });

        setDateDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date_of_deadline);
            }
        });

        date_of_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date_of_deadline);
            }
        });

        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage();
            }
        });

        confirmPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createJioActivity();
            }
        });

        btn_select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostingPage.this, LocationPicker.class);
                startActivityForResult(intent, PICK_LOCATION_REQUEST);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostingPage.this, LocationPicker.class);
                startActivityForResult(intent, PICK_LOCATION_REQUEST);
            }
        });
    }

    private void initialise() {
        confirmPost = findViewById(R.id.btnConfirmPost);
        type_of_activity = findViewById(R.id.spTypeActivity);
        time_of_activity = findViewById(R.id.tvTime);
        date_of_activity = findViewById(R.id.tvDate);
        time_of_deadline = findViewById(R.id.tvDeadlineTime);
        date_of_deadline = findViewById(R.id.tvDeadlineDate);
        displayImage = findViewById(R.id.ivPostDisplayImage);
        setTime = findViewById(R.id.btnSetTime);
        setDate = findViewById(R.id.btnSetDate);
        setTimeDeadline = findViewById(R.id.btnSetTimeDeadline);
        setDateDeadline = findViewById(R.id.btnSetDateDeadline);
        removeImage = findViewById(R.id.btnRemoveImage);
        title_of_post = findViewById(R.id.tilDisplayTitle);
        location = findViewById(R.id.etLocation);
        btn_select_location = findViewById(R.id.post_activity_open_maps);
        min_participants = findViewById(R.id.etMinParticipants);
        max_participants = findViewById(R.id.etMaxParticipants);
        additional_details = findViewById(R.id.etAdditionalDetails);
        storageReference = FirebaseStorage.getInstance().getReference("jioActivityDisplayImage");
    }

    private void initialiseSpinners() {
        ArrayAdapter<CharSequence> type_activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.type_activity, android.R.layout.simple_spinner_item);
        type_activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_of_activity.setAdapter(type_activity_adapter);
        type_of_activity.setOnItemSelectedListener(this);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });
    }

    private void openClock(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String hour_out;
                        String minute_out;

                        if (hourOfDay < 10) {
                            hour_out = "0" + hourOfDay;
                        } else {
                            hour_out = String.valueOf(hourOfDay);
                        }

                        if (minute < 10) {
                            minute_out = "0" + minute;
                        } else {
                            minute_out = String.valueOf(minute);
                        }

                        textView.setText(hour_out + ":" + minute_out);
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    private void openCalender(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        // Launch Time Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        //fix to those file that is image
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ensure image not null and request matches our code (PICK_IMAGE_REQUEST)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            //get image uri
            mImageUri = data.getData();

            //load into imageView on app
            Picasso.get().load(mImageUri).into(displayImage);

        } else if (requestCode == PICK_LOCATION_REQUEST && resultCode == RESULT_OK
                && data != null ) {

            String returnedAddress = data.getStringExtra("Address_name");
            double latitude = data.getDoubleExtra("Latitude_double", 0);
            double longitude = data.getDoubleExtra("Longitude_double", 0);
            geoPoint = new GeoPoint(latitude,longitude);

            this.location.setText(returnedAddress);
        }
    }

    private void createJioActivity() {
        if (!validateTIL(title_of_post)  | !validateTV(time_of_activity)| !validateTV(location)
                | !validateTV(date_of_activity) | !validateTV(time_of_deadline) | !validateTV(date_of_deadline)
                | !validateET(min_participants) | !validateET(max_participants) | !validateET(additional_details)
                | !validateSpinner()) {
            alertDialog(ALERT_FIELDS);
        }  else {
            String title = title_of_post.getEditText().getText().toString();
            String location_input = location.getText().toString();
            String venue = location.getText().toString();
            String actualTime = time_of_activity.getText().toString();
            String actualDate = date_of_activity.getText().toString();
            String deadlineTime = time_of_deadline.getText().toString();
            String deadlineDate = date_of_deadline.getText().toString();
            String details = additional_details.getText().toString();
            String current_uid = currentUser.getUid();
            int min = Integer.parseInt(min_participants.getText().toString());
            int max =Integer.parseInt(max_participants.getText().toString());
            if (!checkMinMax(min, max)) {
                alertDialog(ALERT_MINMAX);
            } else if (!checkDate(deadlineDate + " " + deadlineTime, actualDate + " " + actualTime)) {
                alertDialog(ALERT_DATETIME);
            } else {
                JioActivity input_activity = new JioActivity(title, venue, spinner_input
                        , current_uid, actualDate, actualTime, deadlineDate, deadlineTime, details, min, max);

                input_activity.setGeoPoint(geoPoint);
                input_activity.setDeadline_timestamp(convertDate(deadlineDate, deadlineTime));
                input_activity.setEvent_timestamp(convertDate(actualDate, actualTime));
                input_activity.setTitle_map(fillMap(new HashMap<>(), title));
                input_activity.setLocation_map(fillMap(new HashMap<>(), location_input));
                confirmationDialog(input_activity, mImageUri);
            }
        }
    }

    private Date convertDate(String date, String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return formatter.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, Boolean> fillMap(HashMap<String, Boolean> map, String input) {
        String[] words = input.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
            map.put(words[i], true);
        }

        return map;
    }

    private boolean checkMinMax(int first, int second) {
        //minimum and maximum participants must be more than zero
        if (first <= 0 || second <= 0) {
            return false;
        }

        //this is to set a maximum to both minimum and maximum to ensure that participants array do not get too large which can affect database storage and time complexities when querying
        if (first > 50 || second > 50) {
            Toast.makeText(this, "Max Participants allowed is 50", Toast.LENGTH_SHORT).show();
            return false;
        }

        //maximum cannot be lesser than minimum
        if (second < first) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDate(String first, String second) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date currentDateTime = Calendar.getInstance().getTime(); //this gets both date and time
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = dateFormat.parse(first);
            d2 = dateFormat.parse(second);

            //either deadlines cannot be before the current date and time
            if (d1.compareTo(currentDateTime) < 0 || d2.compareTo(currentDateTime) < 0) {
                return false;
            }

            //actual deadline cannot be before the confirmation deadline
            if (d2.compareTo(d1) < 0) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateTIL(TextInputLayout til) {
        if (til.getEditText().getText().toString().isEmpty()) {
            til.getEditText().setError("Field cannot be empty");
            return false;
        } else {
            til.getEditText().setError(null);
            return true;
        }
    }

    private boolean validateET(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Field cannot be empty");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private boolean validateTV(TextView textView) {
        if (textView.getText().toString().isEmpty()) {
            textView.setError("Field cannot be empty");
            return false;
        } else {
            textView.setError(null);
            return true;
        }
    }

    private boolean validateSpinner() {
        if (spinner_input == null) {
            return false;
        } else {
            return true;
        }
    }

    public void uploadFile(final JioActivity jioActivity, Uri mImageUri) {

        //generate auto-id for jio activity
        final String activity_id = FirebaseFirestore.getInstance().collection("activities").document().getId();

        jioActivity.setActivityId(activity_id);

        //ensure that users have selected an image
        if (mImageUri != null) {

            //creating the file for current user based on uid
            final StorageReference fileRef = storageReference.child(activity_id);

            //uploading to firebase storage
            uploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //get the download url so that we can store in the userProfile object and retrieve when needed
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //put uri in the userProfile obj we created
                            jioActivity.setImageUrl(uri.toString());

                            //then put our obj into firestore
                            putInFirestore(activity_id, jioActivity);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostingPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            jioActivity.setImageUrl(getResources().getString(R.string.defaultImageUrl));
            putInFirestore(activity_id, jioActivity);
        }
    }

    public void putInFirestore(String activity_id, JioActivity jioActivity) {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection("activities");

        //put in JioActivity in firestore
        colRef.document(activity_id)
                .set(jioActivity);

        //put geoLocation into geoFirestore for nearby activity queries
        GeoFirestore geoFirestore = new GeoFirestore(colRef);
        geoFirestore.setLocation(activity_id,jioActivity.getGeoPoint());

        //Storing activity id in user who created it
        HashMap<String, String> input_to_user = new HashMap<>();
        input_to_user.put("Title", jioActivity.getTitle());
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("activities_listed")
                .document(activity_id)
                .set(input_to_user);

        Toast.makeText(this, "Your activity has been listed!", Toast.LENGTH_LONG).show();
    }

    private void confirmationDialog(final JioActivity jioActivity, final Uri image ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostingPage.this);

            builder.setMessage("Are you done with your post?")
                    .setTitle("Confirm Activity");

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                uploadFile(jioActivity, image);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void cancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostingPage.this);

        builder.setMessage("Do you want to delete this draft?")
                .setTitle("Delete draft");

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void alertDialog(int ALERT_TYPE) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostingPage.this);

        if (ALERT_TYPE == ALERT_FIELDS ) {
            builder.setMessage("Please fill up the required fields.")
                    .setTitle("Empty Fields");
        } else if (ALERT_TYPE == ALERT_MINMAX) {
            builder.setMessage("Please enter valid minimum and maximum number of participants.")
                    .setTitle("Number of Participants");
        } else if (ALERT_TYPE == ALERT_DATETIME) {
            builder.setMessage("Please enter valid time and date for actual activity of confirmation deadline.")
                    .setTitle("Time and Date");
        } else {}

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void removeImage() {
        Drawable defaultImage = getResources().getDrawable(R.drawable.ic_baseline_add_a_photo_basegreen);
        displayImage.setImageDrawable(defaultImage);
        mImageUri = null;
    }

    @Override
    public void onBackPressed() {
        cancelDialog();
    }

    //Used for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Please select one")) {
            spinner_input = null;
        } else {
            //do something
            spinner_input = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
