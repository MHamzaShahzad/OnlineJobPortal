package com.example.onlinejobportal.models;

import java.io.Serializable;

public class ApplyingRequest implements Serializable {

    public static final String STRING_ACCEPTED_AT_DATE_TIME = "acceptedAtDateTime";
    public static final String STRING_REJECTED_AT_DATE_TIME = "rejectedAtDateTime";
    public static final String STRING_HIRED_AT_DATE_TIME = "hiredAtDateTime";
    public static final String STRING_NOT_HIRED_AT_DATE_TIME = "notHiredAtDateTime";
    public static final String STRING_STATUS = "applyingStatus";
    public static final String STRING_CHAT_REF = "chatId";

    private String requestId, applierId, applyingAtCompanyId, applyingAtJobId, applyingAtDateTime, acceptedAtDateTime,
            rejectedAtDateTime, hiredAtDateTime, notHiredAtDateTime, applyingStatus, chatId, requestProposal;

    public ApplyingRequest() {
    }

    public ApplyingRequest(String requestId, String applierId, String applyingAtCompanyId, String applyingAtJobId, String applyingAtDateTime, String acceptedAtDateTime, String rejectedAtDateTime, String hiredAtDateTime, String notHiredAtDateTime, String applyingStatus, String chatId, String requestProposal) {
        this.requestId = requestId;
        this.applierId = applierId;
        this.applyingAtCompanyId = applyingAtCompanyId;
        this.applyingAtJobId = applyingAtJobId;
        this.applyingAtDateTime = applyingAtDateTime;
        this.acceptedAtDateTime = acceptedAtDateTime;
        this.rejectedAtDateTime = rejectedAtDateTime;
        this.hiredAtDateTime = hiredAtDateTime;
        this.notHiredAtDateTime = notHiredAtDateTime;
        this.applyingStatus = applyingStatus;
        this.chatId = chatId;
        this.requestProposal = requestProposal;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getApplierId() {
        return applierId;
    }

    public String getApplyingAtCompanyId() {
        return applyingAtCompanyId;
    }

    public String getApplyingAtJobId() {
        return applyingAtJobId;
    }

    public String getApplyingAtDateTime() {
        return applyingAtDateTime;
    }

    public String getAcceptedAtDateTime() {
        return acceptedAtDateTime;
    }

    public String getRejectedAtDateTime() {
        return rejectedAtDateTime;
    }

    public String getHiredAtDateTime() {
        return hiredAtDateTime;
    }

    public String getNotHiredAtDateTime() {
        return notHiredAtDateTime;
    }

    public String getApplyingStatus() {
        return applyingStatus;
    }

    public String getChatId() {
        return chatId;
    }

    public String getRequestProposal() {
        return requestProposal;
    }

}
