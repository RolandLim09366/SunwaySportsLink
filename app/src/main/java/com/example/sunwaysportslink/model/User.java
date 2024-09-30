package com.example.sunwaysportslink.model;

import java.util.List;

public class User {
    private String email;
    private String username;
    private String phoneNumber;
    private String gender;
    private List<String> favoriteSports;

    // Constructor with only required fields
    public User(String email) {
        this.email = email;
        this.username = extractUsernameFromEmail(email); // Automatically generate username from email
        this.phoneNumber = null;  // Default to null, can be set later
        this.gender = null;  // Default to null, can be set later
        this.favoriteSports = null;  // Default to null, can be set later
    }

    // Constructor with all fields (when known)
    public User(String email, String username, String phoneNumber, String gender, List<String> favoriteSports) {
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.favoriteSports = favoriteSports;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public List<String> getFavoriteSports() {
        return favoriteSports;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFavoriteSports(List<String> favoriteSports) {
        this.favoriteSports = favoriteSports;
    }

    // Method to extract username from email (before the @ symbol)
    private String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        } else {
            return email; // In case of malformed email, return the full email as fallback
        }
    }
}
