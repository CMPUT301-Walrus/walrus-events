package com.example.walrusevents.model;

import android.content.Context;

import com.example.walrusevents.util.DeviceIdManager;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Comment implements Serializable {
    private String commentId;
    private String parentId;
    private String entrantId;
    private String body;
    private ArrayList<String> likedEntrantIds;
    @Exclude
    private boolean liked;
    private int totalLikes;

    public Comment() {

    }
    public Comment(String parentId, String entrantId, String body, ArrayList<String> likedEntrantIds) {
        this.parentId = parentId;
        this.entrantId = entrantId;
        this.body = body;
        this.likedEntrantIds = likedEntrantIds;
        totalLikes = likedEntrantIds.size();
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

    public ArrayList<String> getLikedEntrantIds() {
        return likedEntrantIds;
    }

    public void setLikedEntrantIds(ArrayList<String> likedEntrantIds) {
        if (likedEntrantIds == null) {
            return;
        }
        this.likedEntrantIds = likedEntrantIds;
        totalLikes = likedEntrantIds.size();
    }

    @Exclude
    public int getTotalLikes() {
        return totalLikes;
    }

    public void initializeLiked(Context context) {
        if (likedEntrantIds == null) {
            this.likedEntrantIds = new ArrayList<>();
        }

        if (likedEntrantIds.contains(DeviceIdManager.getOrCreate(context))) {
            liked = true;
        }
    }

    public boolean toggleLike(String entrantId) {
        liked = !liked;

        if (likedEntrantIds == null) {
            this.likedEntrantIds = new ArrayList<>();
        }

        if (liked) {
            likedEntrantIds.add(entrantId);
            totalLikes++;
        }
        else {
            likedEntrantIds.removeLast();
            totalLikes--;
        }
        return liked;
    }
}
