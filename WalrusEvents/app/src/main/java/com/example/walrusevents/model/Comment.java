package com.example.walrusevents.model;

public class Comment {
    private String entrantId;
    private String body;
    private int likes;
    private int dislikes;

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void addLike() {
        likes += 1;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void addDislike() {
        dislikes += 1;
    }
}
