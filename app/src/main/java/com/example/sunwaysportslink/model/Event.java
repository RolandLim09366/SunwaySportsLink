package com.example.sunwaysportslink.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event implements Serializable {
    private String title;
    private String date;
    private String venue;
    private String startTime;
    private String participantLimit;
    private String details;
    private String createdBy;
    private String currentParticipants;
    private String eventKey;
    private List<String> joinedUsers; // List to store joined users' UIDs

    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    public Event() {
    }

    public Event(String title, String date, String venue, String startTime, String participantLimit, String details, String createdBy) {
        this.title = title;
        this.date = date;
        this.venue = venue;
        this.startTime = startTime;
        this.participantLimit = participantLimit;
        this.details = details;
        this.createdBy = createdBy;
        this.currentParticipants = "1";
        this.joinedUsers = new ArrayList<>(); // Initialize the list
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getVenue() {
        return venue;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getParticipantLimit() {
        return participantLimit;
    }

    public String getDetails() {
        return details;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCurrentParticipants() {
        return currentParticipants;
    }

    public String getEventKey() {
        return eventKey;
    }

    public List<String> getJoinedUsers() {
        return joinedUsers;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setParticipantLimit(String participantLimit) {
        this.participantLimit = participantLimit;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setCurrentParticipants(String currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public void setJoinedUsers(List<String> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public void addJoinedUser(String userId) {
        if (!joinedUsers.contains(userId)) { // Ensure no duplicates
            joinedUsers.add(userId);
        }
    }

    public boolean isExpired() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Adjust the format to match your date input
        try {
            Date eventDate = sdf.parse(this.date); // Parse the event date
            Date currentDate = sdf.parse(sdf.format(new Date())); // Parse today's date (date only)

            if (eventDate != null && currentDate != null) {
                return eventDate.before(currentDate); // Return true if eventDate is before currentDate
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}