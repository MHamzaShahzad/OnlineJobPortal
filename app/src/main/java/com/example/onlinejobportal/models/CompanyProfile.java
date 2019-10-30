package com.example.onlinejobportal.models;


public class CompanyProfile {
    
    String companyName, companyBusinessemail,companyPhone, companyType, companyCity, companyCountry, 
    selectOrganizationtype;
    
    public CompanyProfile(){
        
    }

    public CompanyProfile(String companyName, String companyBusinessemail, String companyPhone, String companyType, String companyCity, String companyCountry, String selectOrganizationtype) {
        this.companyName = companyName;
        this.companyBusinessemail = companyBusinessemail;
        this.companyPhone = companyPhone;
        this.companyType = companyType;
        this.companyCity = companyCity;
        this.companyCountry = companyCountry;
        this.selectOrganizationtype = selectOrganizationtype;
    }

    public String getcompanyName() {
        return companyName;
    }

    public String getcompanyBusinessemail() {
        return companyBusinessemail;
    }

    public String getcompanyPhone() {
        return companyPhone;
    }

    public String getcompanyType() {
        return companyType;
    }

    public String getcompanyCity() {
        return companyCity;
    }

    public String getcompanyCountry() {
        return companyCountry;
    }

    public String getselectOrganizationtype() {
        return selectOrganizationtype;
    }
}
