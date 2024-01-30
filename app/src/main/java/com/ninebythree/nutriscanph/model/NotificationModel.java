package com.ninebythree.nutriscanph.model;

public class NotificationModel {
    private String message;
    private String date;

    public NotificationModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
