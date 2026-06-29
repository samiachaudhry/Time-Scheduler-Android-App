package com.saqi.time_scheduler.Notifications;

public class NotificationSenderModel {
    public MessageNotificationData data;
    public String to;

    public NotificationSenderModel(MessageNotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationSenderModel() {
    }

    public MessageNotificationData getData() {
        return data;
    }

    public void setData(MessageNotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}