package com.example.walrusevents.model;

import java.time.LocalTime;

/**
 * Notification class is used for formatting emails/notifications to send to differnet users
 */
public class Notification {
    private String title;
    private String message;
    private String eventId;
    private String targetGroup; // "waiting_list", "selected", or "all"
    private long timestamp;

    /**
     * Constructor method
     * @param title header of notif
     * @param message body of notif
     * @param eventId event in discussion
     * @param targetGroup who to send it to (people in "waiting_list", "selected" or "all")
     */
    public Notification(String title, String message, String eventId, String targetGroup) {
        this.title = title;
        this.message = message;
        this.eventId = eventId;
        this.targetGroup = targetGroup;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Get title of notif
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Change/set title of notif
     * @param title header of notif
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get message for notif
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Change/set body of notif
     * @param message body of notif
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Get unique identifier for event
     * @return eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Set id of event
     * @param eventId unique identifier for event
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Get group we want to send notif to
     * @return targetGroup
     */
    public String getTargetGroup() { return targetGroup; }

    /**
     * Change/set target group
     * @param targetGroup recipient(s) notif
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    /**
     * Get time at which notif was sent
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set the time at which notif was sent/created
     * @param timestamp time object (long)
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
