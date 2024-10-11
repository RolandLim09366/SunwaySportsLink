package com.example.sunwaysportslink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event implements Serializable {
    private String title;
    private String date;
    private String venue;
    private String startTime;
    private String endTime;
    private String participantLimit;
    private String details;
    private String createdBy;
    private String currentParticipants;
    private String eventKey;
    private List<String> joinedUsers; // List to store joined users' UIDs


    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    public Event() {
    }

    public Event(String title, String date, String venue, String startTime, String endTime, String participantLimit, String details, String createdBy) {
        this.title = title;
        this.date = date;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantLimit = participantLimit;
        this.details = details;
        this.createdBy = createdBy;
        this.currentParticipants = "1";
        this.joinedUsers = new ArrayList<>(); // Initialize the list
    }

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

    public String getEndTime() {
        return endTime;
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

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCurrentParticipants() {
        return currentParticipants;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public void setCurrentParticipants(String updatedParticipants) {
        this.currentParticipants = updatedParticipants;
    }

    // Method to add a user to the joinedUsers list
    public void addJoinedUser(String userId) {
        if (!joinedUsers.contains(userId)) { // Ensure no duplicates
            joinedUsers.add(userId);
        }

    }
    // Getter for the joinedUsers list
    public List<String> getJoinedUsers() {
        return joinedUsers;
    }
}