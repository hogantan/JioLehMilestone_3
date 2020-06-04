package com.example.jioleh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class PostingPage
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private Spinner type_of_activity;
    private Button setTime;
    private Button setDate;
    private TextView time_of_activity;
    private TextView date_of_activity;

    private int currentHour;
    private int currentMinute;
    private int currentYear;
    private int currentMonth;
    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_page);

        initialise();
        initialiseSpinners();

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock();
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender();
            }
        });
    }

    private void openClock() {
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

                        time_of_activity.setText(hour_out + ":" + minute_out);
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    private void openCalender() {
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        // Launch Time Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date_of_activity.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    private void initialise() {
        type_of_activity = findViewById(R.id.spTypeActivity);
        time_of_activity = findViewById(R.id.tvTime);
        date_of_activity = findViewById(R.id.tvDate);
        setTime = findViewById(R.id.btnSetTime);
        setDate = findViewById(R.id.btnSetDate);
    }

    private void initialiseSpinners() {
        ArrayAdapter<CharSequence> type_activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.type_activity, android.R.layout.simple_spinner_item);
        type_activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_of_activity.setAdapter(type_activity_adapter);
        type_of_activity.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Please select one")) {
            //do nothing
        } else {
            //do something
            String text = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
