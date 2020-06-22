package com.example.jioleh.listings;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class JioActivity {


    private String activityId; //use fireStore document id
    private String title;
    private String location;
    private String type_of_activity;
    private String host_uid;
    private String event_date;
    private String event_time;
    private String deadline_date;
    private String deadline_time;
    private String details;
    private String imageUrl;
    private int current_participants;
    private int min_participants;
    private int max_participants;
    private ArrayList<String> participants;
    private ArrayList<String> title_array;
    @ServerTimestamp
    private Date time_created;

    private GeoPoint geoPoint;

    public JioActivity(String title, String location, String type_of_activity,
                       String host_uid, String event_date, String event_time, String deadline_date,
                       String deadline_time, String details, int min_participants,
                       int max_participants) {
        this.title = title;
        this.location = location;
        this.type_of_activity = type_of_activity;
        this.host_uid = host_uid;
        this.event_date = event_date;
        this.event_time = event_time;
        this.deadline_date = deadline_date;
        this.deadline_time = deadline_time;
        this.details = details;
        this.min_participants = min_participants;
        this.max_participants = max_participants;
        this.participants = new ArrayList<>();
        this.title_array = new ArrayList<>();
        fillArray(this.title_array, this.title);
        this.current_participants = 0;
        this.imageUrl = "";
    }

    private void fillArray(ArrayList<String> array, String input) {
        String[] words = input.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }
        array.addAll(Arrays.asList(words));
        array.add("");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof JioActivity) {
            JioActivity other = (JioActivity) obj;
            return this.title.equals(other.title) &&
                            this.location.equals(other.location) &&
                            this.type_of_activity.equals(other.type_of_activity) &&
                            this.host_uid.equals(other.host_uid) &&
                            this.event_date.equals(other.event_date) &&
                            this.event_time.equals(other.event_time) &&
                            this.deadline_date.equals(other.deadline_date) &&
                            this.deadline_time.equals(other.deadline_time) &&
                            this.details.equals(other.details) &&
                            this.min_participants == other.min_participants &&
                            this.max_participants == other.max_participants &&
                            this.current_participants == other.current_participants &&
                            this.imageUrl.equals(other.imageUrl);

        } else {
            return false;
        }
    }

    public JioActivity(){}

    public ArrayList<String> getTitle_array() {
        return title_array;
    }

    public void setTitle_array(ArrayList<String> title_array) {
        this.title_array = title_array;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public boolean isSameId(JioActivity other) {
        return this.activityId.equals(other.activityId);
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType_of_activity() {
        return type_of_activity;
    }

    public void setType_of_activity(String type_of_activity) {
        this.type_of_activity = type_of_activity;
    }

    public String getHost_uid() {
        return host_uid;
    }

    public void setHost_uid(String host_uid) {
        this.host_uid = host_uid;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getCurrent_participants() {
        return current_participants;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCurrent_participants(int current_participants) {
        this.current_participants = current_participants;
    }

    public int getMin_participants() {
        return min_participants;
    }

    public void setMin_participants(int min_participants) {
        this.min_participants = min_participants;
    }

    public int getMax_participants() {
        return max_participants;
    }

    public void setMax_participants(int max_participants) {
        this.max_participants = max_participants;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }


    public String getDeadline_date() {
        return deadline_date;
    }

    public void setDeadline_date(String deadline_date) {
        this.deadline_date = deadline_date;
    }

    public String getDeadline_time() {
        return deadline_time;
    }

    public void setDeadline_time(String deadline_time) {
        this.deadline_time = deadline_time;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
