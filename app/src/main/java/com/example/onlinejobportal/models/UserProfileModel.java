package com.example.onlinejobportal.models;

import java.io.Serializable;

public class UserProfileModel implements Serializable {

    String userImage, userFirstName, userLastName, userPhone, userEmail, userGender, userAge, userMarriageStatus, userCity,
            userIntro, userEduction, userCountry, userCurrentJob, userSkills;

    public UserProfileModel() {
    }

    public UserProfileModel(String userImage, String userFirstName, String userEmail) {
        this.userImage = userImage;
        this.userFirstName = userFirstName;
        this.userEmail = userEmail;
    }

    public UserProfileModel(String userImage, String userFirstName, String userLastName, String userPhone, String userEmail, String userGender, String userAge, String userMarriageStatus, String userCity, String userIntro, String userEduction, String userCountry, String userCurrentJob, String userSkills) {
        this.userImage = userImage;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userAge = userAge;
        this.userMarriageStatus = userMarriageStatus;
        this.userCity = userCity;
        this.userIntro = userIntro;
        this.userEduction = userEduction;
        this.userCountry = userCountry;
        this.userCurrentJob = userCurrentJob;
        this.userSkills = userSkills;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserMarriageStatus() {
        return userMarriageStatus;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getUserIntro() {
        return userIntro;
    }

    public String getUserEduction() {
        return userEduction;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public String getUserCurrentJob() {
        return userCurrentJob;
    }

    public String getUserSkills() {
        return userSkills;
    }
}


