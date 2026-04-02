package com.example.walrusevents.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Comment implements Serializable {
    private String commentId;
    private String parentId;
    private String entrantId;
    private String body;
    private int likes;
    private int dislikes;

    public Comment() {

    }
    public Comment(String parentId, String entrantId, String body, int likes, int dislikes) {
        this.parentId = parentId;
        this.entrantId = entrantId;
        this.body = body;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
}
