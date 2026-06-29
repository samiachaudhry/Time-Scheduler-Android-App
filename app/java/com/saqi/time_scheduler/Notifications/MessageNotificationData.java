package com.saqi.time_scheduler.Notifications;

public class MessageNotificationData {
    private String receiverID, receiverName, receiverImage, message, notificationType, extras,day;

    public MessageNotificationData() {
    }

    public MessageNotificationData(String receiverID, String receiverName, String receiverImage) {
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
    }

    public MessageNotificationData(String receiverID, String receiverName, String receiverImage, String message) {
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.message = message;
    }

    public MessageNotificationData(String receiverID, String receiverName, String receiverImage,
                                   String message, String notificationType, String extras,String day) {
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.message = message;
        this.notificationType = notificationType;
        this.extras = extras;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}