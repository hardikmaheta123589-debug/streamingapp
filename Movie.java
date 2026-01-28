package com.example.streamingapp;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String category;
    private int posterResourceId;
    private int videoResourceId;
    private String resourceName;

    public Movie(String title, String category, int posterResourceId, int videoResourceId, String resourceName) {
        this.title = title;
        this.category = category;
        this.posterResourceId = posterResourceId;
        this.videoResourceId = videoResourceId;
        this.resourceName = resourceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPosterResourceId() {
        return posterResourceId;
    }

    public void setPosterResourceId(int posterResourceId) {
        this.posterResourceId = posterResourceId;
    }

    public int getVideoResourceId() {
        return videoResourceId;
    }

    public void setVideoResourceId(int videoResourceId) {
        this.videoResourceId = videoResourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
