package com.example.walrusevents;

public class Applicant {
    // This is just filling in for now
    private int id;
    private int status;

    public Applicant(int id) {
        this.id = id;
        this.status = 0;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
