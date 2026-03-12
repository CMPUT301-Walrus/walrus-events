package com.example.walrusevents;

/**
 * This is a class that associates the id of a user profile with their application status for an event
 */
public class Entry {
    // This is just filling in for now
    private String id;
    private Status status;

    public Entry(String id) {
        this.id = id;
        this.status = Status.PENDING;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

enum Status {
    PENDING,
    INVITED,
    ACCEPTED,
    DECLINED,
    CANCELLED
}
