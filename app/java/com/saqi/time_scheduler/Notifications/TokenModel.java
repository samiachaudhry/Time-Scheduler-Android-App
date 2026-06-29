package com.saqi.time_scheduler.Notifications;

public class TokenModel {
    private String token;

    public TokenModel(String token) {
        this.token = token;
    }

    public TokenModel() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}