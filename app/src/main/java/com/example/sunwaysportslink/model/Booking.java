package com.example.sunwaysportslink.model;

public class Booking {
    private String timeSlot;
    private String bookedBy;

    // Default constructor required for Firebase
    public Booking() {}

    public Booking(String timeSlot, String bookedBy) {
        this.timeSlot = timeSlot;
        this.bookedBy = bookedBy;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }
}

