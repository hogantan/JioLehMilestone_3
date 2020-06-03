package com.example.jioleh;

import java.util.ArrayList;

public class JioActivity {

    private String title;
    private String time_created;
    private String location;
    private String type_of_activity;
    private String host_uid;
    private String pre_deadline;
    private String deadline;
    private String details;
    private int current_participants;
    private int min_participants;
    private int max_participants;
    private boolean isFull;
    private ArrayList<String> participants;

    public boolean isFull() {
        if (current_participants == max_participants) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isCancelled() {
        if (current_participants < min_participants) {
            return true;
        } else {
            return false;
        }
    }

    public void addParticipant(String uid) {
        if (!isFull()) {
            this.participants.add(uid);
        }
    }

    public JioActivity(String title,String location, String type_of_activity, String host_uid,
                       String pre_deadline, String deadline, String details, int min_participants,
                       int max_participants) {
        this.title = title;
        this.time_created = time_created;
        this.location = location;
        this.type_of_activity = type_of_activity;
        this.host_uid = host_uid;
        this.pre_deadline = pre_deadline;
        this.deadline = deadline;
        this.details = details;
        this.min_participants = min_participants;
        this.max_participants = max_participants;
    }

    public JioActivity(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
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

    public String getPre_deadline() {
        return pre_deadline;
    }

    public void setPre_deadline(String pre_deadline) {
        this.pre_deadline = pre_deadline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
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

}
