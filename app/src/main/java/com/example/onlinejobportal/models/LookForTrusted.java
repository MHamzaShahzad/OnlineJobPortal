package com.example.onlinejobportal.models;

import java.io.Serializable;

public class LookForTrusted implements Serializable {

    String userId, userMessage, requestedAt, interviewLocation, interViewLatLng, interviewHeldOnDate, interviewHeldOnTime;
    boolean isNotified;

    public LookForTrusted() {
    }

    public LookForTrusted(String userId, String userMessage, String requestedAt, boolean isNotified) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.requestedAt = requestedAt;
        this.isNotified = isNotified;
    }

    public LookForTrusted(String userId, String userMessage, String requestedAt, String interviewLocation, String interViewLatLng, String interviewHeldOnDate, String interviewHeldOnTime, boolean isNotified) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.requestedAt = requestedAt;
        this.interviewLocation = interviewLocation;
        this.interViewLatLng = interViewLatLng;
        this.interviewHeldOnDate = interviewHeldOnDate;
        this.interviewHeldOnTime = interviewHeldOnTime;
        this.isNotified = isNotified;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public String getInterViewLatLng() {
        return interViewLatLng;
    }

    public String getInterviewHeldOnDate() {
        return interviewHeldOnDate;
    }

    public String getInterviewHeldOnTime() {
        return interviewHeldOnTime;
    }

    public boolean isNotified() {
        return isNotified;
    }



    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public void setInterViewLatLng(String interViewLatLng) {
        this.interViewLatLng = interViewLatLng;
    }

    public void setInterviewHeldOnDate(String interviewHeldOnDate) {
        this.interviewHeldOnDate = interviewHeldOnDate;
    }

    public void setInterviewHeldOnTime(String interviewHeldOnTime) {
        this.interviewHeldOnTime = interviewHeldOnTime;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }
}
