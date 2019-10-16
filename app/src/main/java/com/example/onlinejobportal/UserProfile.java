package com.example.onlinejobportal;

public class UserProfile {

    String firstName, lastName, emailAddress, phoneNumber, cnicNumber, postalAddress,
    gender, imageUrl;

    public UserProfile() {
    }

    public UserProfile(String firstName, String lastName, String emailAddress, String phoneNumber, String cnicNumber, String postalAddress, String gender, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.cnicNumber = cnicNumber;
        this.postalAddress = postalAddress;
        this.gender = gender;
        this.imageUrl = imageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public String getGender() {
        return gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


