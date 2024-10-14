package com.example.sunwaysportslink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
}
