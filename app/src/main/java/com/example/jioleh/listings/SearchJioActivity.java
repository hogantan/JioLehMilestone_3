package com.example.jioleh.listings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.R;
import com.example.jioleh.favourites.FavouritesAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SearchJioActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TextInputLayout title;
    private TextInputLayout location;
    private TextView date;
    private TextView time;
    private Spinner type;
    private String spinner_input = null;
    private Button search;
    private Button setTime;
    private Button setDate;
    private Button expand;
    private View viewLine;
    private TextView searchExpand;
    private RecyclerView results;
    private TextView resultsMessage;
    private ExpandableRelativeLayout expandableRelativeLayout;
    private ActivityAdapter adapter;

    private FirebaseFirestore datastore;

    private LinesOfChecks linesOfChecks;

    private boolean buttonFlag = false;
    private int currentHour;
    private int currentMinute;
    private int currentYear;
    private int currentMonth;
    private int currentDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_jio);
        initialise();
        initialiseToolbar();
        initialiseRecyclerView();
        initialiseSpinners();

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock(time);
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(date);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDatabase();
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableRelativeLayout.toggle();
                expandButtonToggle();
                viewLineToggle();
            }
        });

        searchExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableRelativeLayout.toggle();
                expandButtonToggle();
                viewLineToggle();
            }
        });

    }

    private void initialise() {
        title = findViewById(R.id.tilSearchTitle);
        location = findViewById(R.id.tilSearchLocation);
        date = findViewById(R.id.tvSearchDate);
        time = findViewById(R.id.tvSearchTime);
        type = findViewById(R.id.spSearchType);
        search = findViewById(R.id.btnSearch);
        setTime = findViewById(R.id.btnSetTime);
        setDate = findViewById(R.id.btnSetDate);
        expand = findViewById(R.id.btnExpand);
        viewLine = findViewById(R.id.viewLine);
        searchExpand = findViewById(R.id.tvSearchExpand);
        expandableRelativeLayout = findViewById(R.id.ellExpander);
        expandableRelativeLayout.collapse();
        resultsMessage = findViewById(R.id.tvSearchResultsMessage);
        datastore = FirebaseFirestore.getInstance();
        linesOfChecks = new LinesOfChecks();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("Search JioLeh");
        toolbar.setTitleTextColor(getResources().getColor(R.color.baseGreen));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiseRecyclerView() {
        adapter = new ActivityAdapter();
        results = findViewById(R.id.rvSearchResults);
        results.setHasFixedSize(true);
        results.setLayoutManager(new LinearLayoutManager(this));
        results.setAdapter(adapter);
    }

    private void initialiseSpinners() {
        ArrayAdapter<CharSequence> type_activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.type_activity, android.R.layout.simple_spinner_item);
        type_activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(type_activity_adapter);
        type.setOnItemSelectedListener(this);
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

    //Used for spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void expandButtonToggle() {
        if (buttonFlag) {
            expand.setBackground(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_basegreen));
            buttonFlag = false;
        } else {
            expand.setBackground(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_basegreen));
            buttonFlag = true;
        }
    }

    private void viewLineToggle() {
        if (buttonFlag) {
            viewLine.setVisibility(View.GONE);
        } else {
            viewLine.setVisibility(View.VISIBLE);
        }
    }

    private void searchDatabase() {
        //Third line of check
        linesOfChecks.checkActivityExpiry();
        linesOfChecks.checkActivityCancelledConfirmed();

        String title_input = title.getEditText().getText().toString();
        String location_input = location.getEditText().getText().toString();
        String date_input = date.getText().toString();
        String time_input = time.getText().toString();
        String type_input = spinner_input;

        Query query = datastore.collection("activities");

        //checking each individual user input

        //this checks each word of user input whether it fits with each title's word
        if (!title_input.isEmpty()) {
            List<String> location_words = Arrays.asList(convertSentenceToWordArray(title_input.toLowerCase()));
            for (String word : location_words) {
                query = query.whereEqualTo("title_map." + word, true);
            }
        }

        if (type_input != null) {
            query = query.whereEqualTo("type_of_activity", type_input);
        }

        //this checks each word or number of user input whether it fits with each location's word/number
        if (!location_input.isEmpty()) {
            List<String> location_words = Arrays.asList(convertSentenceToWordArray(location_input.toLowerCase()));
            for (String word : location_words) {
                query = query.whereEqualTo("location_map." + word, true);
            }
        }

        if (!date_input.isEmpty()) {
            query = query.whereEqualTo("event_date", date_input);
        }

        if (!time_input.isEmpty()) {
            query = query.whereEqualTo("event_time", time_input);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<JioActivity> list_of_activities = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    if (queryDocumentSnapshot.get("expired").equals(false) && queryDocumentSnapshot.get("cancelled").equals(false)) {
                        list_of_activities.add(queryDocumentSnapshot.toObject(JioActivity.class));
                    }
                }
                adapter.setData(list_of_activities);
                adapter.notifyDataSetChanged();

                if (list_of_activities.size() == 0) {
                    resultsMessage.setText("No matches");
                } else {
                    resultsMessage.setText("");
                }
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
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                time.setText("");
            }
        });
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
                        textView.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }, currentYear, currentMonth, currentDay);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                date.setText("");
            }
        });
        datePickerDialog.show();
    }

    private String[] convertSentenceToWordArray(String input) {
        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }
        return words;
    }
}