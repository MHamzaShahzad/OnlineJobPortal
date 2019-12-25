package com.example.onlinejobportal.models;


public class CompanyProfileModel {

    String image, companyName, companyBusinessEmail, companyPhone, companyType, companyCity, companyCountry, companyAbout;

    public CompanyProfileModel() {

    }

    public CompanyProfileModel(String image, String companyName, String companyBusinessEmail) {
        this.image = image;
        this.companyName = companyName;
        this.companyBusinessEmail = companyBusinessEmail;
    }

    public CompanyProfileModel(String companyName, String companyBusinessEmail) {
        this.companyName = companyName;
        this.companyBusinessEmail = companyBusinessEmail;
    }

    public CompanyProfileModel(String image, String companyName, String companyBusinessEmail, String companyPhone, String companyType, String companyCity, String companyCountry, String companyAbout) {
        this.image = image;
        this.companyName = companyName;
        this.companyBusinessEmail = companyBusinessEmail;
        this.companyPhone = companyPhone;
        this.companyType = companyType;
        this.companyCity = companyCity;
        this.companyCountry = companyCountry;
        this.companyAbout = companyAbout;
    }

    public String getImage() {
        return image;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyBusinessEmail() {
        return companyBusinessEmail;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public String getCompanyType() {
        return companyType;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public String getCompanyAbout() {
        return companyAbout;
    }
}

