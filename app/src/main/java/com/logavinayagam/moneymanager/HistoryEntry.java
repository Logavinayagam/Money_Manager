package com.logavinayagam.moneymanager;


public class HistoryEntry {
    private String reason;
    private double amount;
    private String date;
    private String time;

    // Constructors, getters, and setters

    public HistoryEntry() {
        // Default constructor required for Firebase
    }

    public HistoryEntry(String reason, double amount, String date, String time) {
        this.reason = reason;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

