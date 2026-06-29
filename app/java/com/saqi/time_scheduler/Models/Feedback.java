package com.saqi.time_scheduler.Models;

public class Feedback {
    private String UID,feedback,timeStamp;

    public Feedback() {
    }

    public Feedback(String UID, String feedback, String timeStamp) {
        this.UID = UID;
        this.feedback = feedback;
        this.timeStamp = timeStamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
