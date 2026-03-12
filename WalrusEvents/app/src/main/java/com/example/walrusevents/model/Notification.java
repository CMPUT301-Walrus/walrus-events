package com.example.walrusevents.model;

import java.time.LocalTime;

public class Notification {
    private String title;
    private String message;
    private String eventId;
    private String targetGroup; // "waiting_list", "selected", or "all"
    private long timestamp;

    public Notification(String title, String message, String eventId, String targetGroup) {
        this.title = title;
        this.message = message;
        this.eventId = eventId;
        this.targetGroup = targetGroup;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
