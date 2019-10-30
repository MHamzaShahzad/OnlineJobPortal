package com.example.onlinejobportal.models;


public class CompanyProfile {

    String companyName, companyBusinessEmail, companyPhone, companyType, companyCity, companyCountry;

    public CompanyProfile() {

    }

    public CompanyProfile(String companyName, String companyBusinessEmail, String companyPhone, String companyType, String companyCity, String companyCountry) {
        this.companyName = companyName;
        this.companyBusinessEmail = companyBusinessEmail;
        this.companyPhone = companyPhone;
        this.companyType = companyType;
        this.companyCity = companyCity;
        this.companyCountry = companyCountry;
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

}

