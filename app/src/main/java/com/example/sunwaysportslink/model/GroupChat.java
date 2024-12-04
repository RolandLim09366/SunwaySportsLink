package com.example.sunwaysportslink.model;

public class GroupChat {
    private String groupId;
    private String groupName;
    private String lastMessage;
    private String timestamp;

    public GroupChat(String groupId, String groupName, String lastMessage, String timestamp) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}