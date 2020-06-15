package com.example.jioleh.userprofile;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Report {

    private String reportingUser;
    private String reportedUser;
    private String descriptiveIssue;
    private boolean isOpen;

    @ServerTimestamp
    private Date timeOfReporting;

    public Report() {
    }

    public Report(String reportingUser, String reportedUser, String descriptiveIssue,boolean isOpen) {
        this.isOpen = isOpen;
        this.reportingUser = reportingUser;
        this.reportedUser = reportedUser;
        this.descriptiveIssue = descriptiveIssue;
    }

    public String getReportingUser() {
        return reportingUser;
    }

    public String getReportedUser() {
        return reportedUser;
    }

    public String getDescriptiveIssue() {
        return descriptiveIssue;
    }

    public Date getTimeOfReporting() {
        return timeOfReporting;
    }

    public boolean getIsOpen() {
        return isOpen;
    }
}
