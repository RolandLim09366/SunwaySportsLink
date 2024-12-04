package com.example.sunwaysportslink.model;

public class ChatMessage {
    private String messageText;
    private String senderId;
    private String senderName; // Add this
    private String senderProfilePictureUrl; // Add this
    private long timestamp;

    // Default constructor for Firebase
    public ChatMessage() {
    }

    public ChatMessage(String messageText, String senderId, String senderName, String senderProfilePictureUrl, long timestamp) {
        this.messageText = messageText;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfilePictureUrl = senderProfilePictureUrl;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessageText() {
        return messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderProfilePictureUrl() {
        return senderProfilePictureUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }
}