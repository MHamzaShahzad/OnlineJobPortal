package com.example.onlinejobportal.models;

import java.io.Serializable;

public class HiringRequest implements Serializable {

    public static final String STRING_ACCEPTED_AT_DATE_TIME = "acceptedByUserAt";
    public static final String STRING_REJECTED_AT_DATE_TIME = "rejectedByUserAt";
    public static final String STRING_HIRED_AT_DATE_TIME = "hiringAcceptedAt";
    public static final String STRING_NOT_HIRED_AT_DATE_TIME = "hiringRejectedAt";
    public static final String STRING_CHAT_REF = "chatId";
    public static final String STRING_STATUS = "hireStatus";

    String hiringId, hiredByCompanyId, hiringUserId, hireStatus, hireRequestedAt, acceptedByUserAt,
            rejectedByUserAt, hiringRejectedAt, hiringAcceptedAt, chatId;

    public HiringRequest() {
    }

    public HiringRequest(String hiringId, String hiredByCompanyId, String hiringUserId, String hireStatus, String hireRequestedAt, String acceptedByUserAt, String rejectedByUserAt, String hiringRejectedAt, String hiringAcceptedAt, String chatId) {
        this.hiringId = hiringId;
        this.hiredByCompanyId = hiredByCompanyId;
        this.hiringUserId = hiringUserId;
        this.hireStatus = hireStatus;
        this.hireRequestedAt = hireRequestedAt;
        this.acceptedByUserAt = acceptedByUserAt;
        this.rejectedByUserAt = rejectedByUserAt;
        this.hiringRejectedAt = hiringRejectedAt;
        this.hiringAcceptedAt = hiringAcceptedAt;
        this.chatId = chatId;
    }

    public String getHiringId() {
        return hiringId;
    }

    public String getHiredByCompanyId() {
        return hiredByCompanyId;
    }

    public String getHiringUserId() {
        return hiringUserId;
    }

    public String getHireStatus() {
        return hireStatus;
    }

    public String getHireRequestedAt() {
        return hireRequestedAt;
    }

    public String getAcceptedByUserAt() {
        return acceptedByUserAt;
    }

    public String getRejectedByUserAt() {
        return rejectedByUserAt;
    }

    public String getHiringRejectedAt() {
        return hiringRejectedAt;
    }

    public String getHiringAcceptedAt() {
        return hiringAcceptedAt;
    }

    public String getChatId() {
        return chatId;
    }
}
