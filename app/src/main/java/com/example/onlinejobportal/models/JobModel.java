package com.example.onlinejobportal.models;

import java.io.Serializable;

public class JobModel implements Serializable {

    private String jobId, jobStatus, uploadBy, uploadedAt, jobTitle, jobSalary, jobLocation, jobDescription;

    public JobModel() {
    }

    public JobModel(String jobId, String jobStatus, String uploadBy, String uploadedAt, String jobTitle, String jobSalary, String jobLocation, String jobDescription) {
        this.jobId = jobId;
        this.jobStatus = jobStatus;
        this.uploadBy = uploadBy;
        this.uploadedAt = uploadedAt;
        this.jobTitle = jobTitle;
        this.jobSalary = jobSalary;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobSalary() {
        return jobSalary;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getJobDescription() {
        return jobDescription;
    }
}
