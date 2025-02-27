package com.example.sunwaysportslink.model;

public class User {
    private String email;
    private String username;
    private String phone; // Ensure this field is present and correctly named
    private String gender;
    private String userId;
    private String favourite_sports;
    private String lastOnlineTime; // New field for last online time
    private String fcmToken;
    private String profileImageUrl;

    // No-argument constructor (required for Firebase)
    public User() {
    }

    // Constructor with only required fields
    public User(String email) {
        this.email = email;
        this.username = extractUsernameFromEmail(email); // Automatically generate username from email
        this.phone = null;  // Default to null, can be set later
        this.gender = null;  // Default to null, can be set later
        this.favourite_sports = null;  // Default to null, can be set later
    }

    // Constructor with all fields (when known)
    public User(String email, String username, String phone, String gender, String favourite_sports) {
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.gender = gender;
        this.favourite_sports = favourite_sports;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getUserId() {
        return userId;
    }


    public String getFavourite_Sports() {
        return favourite_sports;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFavoriteSports(String favourite_sports) {
        this.favourite_sports = favourite_sports;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(String lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    // Method to extract username from email (before the @ symbol)
    private String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        } else {
            return email; // In case of malformed email, return the full email as fallback
        }
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // Getter for profileImageUrl
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Setter for profileImageUrl
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
