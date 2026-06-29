package com.saqi.time_scheduler.Notifications;

public class MessageResponseModel {
    public int success;

    public MessageResponseModel(int success) {
        this.success = success;
    }

    public MessageResponseModel() {
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
