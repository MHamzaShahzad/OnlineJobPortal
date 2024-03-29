package com.example.onlinejobportal.models;

import java.io.Serializable;

public class JobModel implements Serializable {

    public static final String STRING_JOB_STATUS = "jobStatus";

    private String jobId, jobStatus, uploadBy, uploadedAt, jobDueDate, jobTitle, jobSalary, jobLocation, jobLocationLatLng, jobDescription, jobIndustry,
            jobDepartment, jobEducation, jobCareer, requiredThings, jobForGender, jobType, jobExperience;

    public JobModel() {
    }

    public JobModel(String jobId, String jobStatus, String uploadBy, String uploadedAt, String jobDueDate, String jobTitle, String jobSalary, String jobLocation, String jobLocationLatLng, String jobDescription, String jobIndustry, String jobDepartment, String jobEducation, String jobCareer, String requiredThings, String jobForGender, String jobType, String jobExperience) {
        this.jobId = jobId;
        this.jobStatus = jobStatus;
        this.uploadBy = uploadBy;
        this.uploadedAt = uploadedAt;
        this.jobDueDate = jobDueDate;
        this.jobTitle = jobTitle;
        this.jobSalary = jobSalary;
        this.jobLocation = jobLocation;
        this.jobLocationLatLng = jobLocationLatLng;
        this.jobDescription = jobDescription;
        this.jobIndustry = jobIndustry;
        this.jobDepartment = jobDepartment;
        this.jobEducation = jobEducation;
        this.jobCareer = jobCareer;
        this.requiredThings = requiredThings;
        this.jobForGender = jobForGender;
        this.jobType = jobType;
        this.jobExperience = jobExperience;
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

    public String getJobDueDate() {
        return jobDueDate;
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

    public String getJobLocationLatLng() {
        return jobLocationLatLng;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getJobIndustry() {
        return jobIndustry;
    }

    public String getJobDepartment() {
        return jobDepartment;
    }

    public String getJobEducation() {
        return jobEducation;
    }

    public String getJobCareer() {
        return jobCareer;
    }

    public String getRequiredThings() {
        return requiredThings;
    }

    public String getJobForGender() {
        return jobForGender;
    }

    public String getJobType() {
        return jobType;
    }

    public String getJobExperience() {
        return jobExperience;
    }
}
