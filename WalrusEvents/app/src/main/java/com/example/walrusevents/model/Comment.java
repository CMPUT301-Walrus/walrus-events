package com.example.walrusevents.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Comment implements Serializable {
    private String entrantId;
    private String body;
    private int likes;
    private int dislikes;
    private ArrayList<Comment> replies;

    public Comment() {

    }
    public Comment(String entrantId, String body, int likes, int dislikes) {
        this.entrantId = entrantId;
        this.body = body;
        this.likes = likes;
        this.dislikes = dislikes;
    }
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

    public ArrayList<Comment> getReplies() {
        if (replies == null) {
            replies = new ArrayList<>();
        }
        return replies;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    public void addReply(Comment reply) {
        if (replies == null) {
            replies = new ArrayList<>();
        }
        replies.add(reply);
    }
}
