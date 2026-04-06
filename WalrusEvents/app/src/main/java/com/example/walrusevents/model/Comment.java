package com.example.walrusevents.model;

import android.content.Context;

import com.example.walrusevents.util.DeviceIdManager;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Model class that holds information about a comment
 * Information includes but is not limited to who commented, the comment itself, and the number of likes, etc.
 * This class is responsible for providing an interface for the rest of the app to interact with comments
 */
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

    /**
     * Constructor for a comment
     * @param parentId id of the parent comment
     * @param entrantId id of the entrant who made the comment
     * @param body body of the comment
     * @param likedEntrantIds list of entrants who liked the comment
     */
    public Comment(String parentId, String entrantId, String body, ArrayList<String> likedEntrantIds) {
        this.parentId = parentId;
        this.entrantId = entrantId;
        this.body = body;
        this.likedEntrantIds = likedEntrantIds;
        totalLikes = likedEntrantIds.size();
    }

    /**
     * Gets the id of the comment
     * @return
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * Sets the id of the comment
     * @param commentId
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     * Gets the id of the parent comment
     * @return
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the id of the parent comment
     * @param parentId id of the parent comment
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the id of the entrant who made the comment
     * @return
     */
    public String getEntrantId() {
        return entrantId;
    }

    /**
     * Sets the id of the entrant who made the comment
     * @param entrantId id of the entrant who made the comment
     */
    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    /**
     * Gets the body of the comment
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the comment
     * @param body new body of the comment
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the list of entrants who liked the comment
     * @return
     */
    public ArrayList<String> getLikedEntrantIds() {
        return likedEntrantIds;
    }

    /**
     * Sets the list of entrants who liked the comment
     * @param likedEntrantIds new list of entrants who liked the comment
     */
    public void setLikedEntrantIds(ArrayList<String> likedEntrantIds) {
        if (likedEntrantIds == null) {
            return;
        }
        this.likedEntrantIds = likedEntrantIds;
        totalLikes = likedEntrantIds.size();
    }

    /**
     * Get all likes for particular comment
     * @return
     */
    @Exclude
    public int getTotalLikes() {
        return totalLikes;
    }

    /**
     * Initialize all likes for particular comment
     * @param totalLikes number of likes
     */
    @Exclude
    public void initializeLiked(Context context) {
        if (likedEntrantIds == null) {
            this.likedEntrantIds = new ArrayList<>();
        }

        if (likedEntrantIds.contains(DeviceIdManager.getOrCreate(context))) {
            liked = true;
        }
    }

    /**
     * Toggle like for particular comment
     * On for like, off for unlike
     * @param entrantId id of entrant who liked the comment
     * @return
     */
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
