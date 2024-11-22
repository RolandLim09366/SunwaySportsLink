package com.example.sunwaysportslink.model;

import android.os.Parcelable;

import java.io.Serializable;

public class SportsNews implements Serializable {
    private String id;  // Unique identifier for each news (Firebase key)
    private String title;
    private String description;
    private String imageUrl;

    public SportsNews() {
        // Default constructor required for Firebase
    }

    public SportsNews(String title, String description, String imageUrl ) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
