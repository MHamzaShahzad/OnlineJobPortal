package com.example.onlinejobportal.controllers;

import com.example.onlinejobportal.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabase {

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference USER_PROFILE_REFERENCE = database.getReference(Constants.STRING_USER_PROFILE_REFERENCE);
    public static final DatabaseReference COMPANY_PROFILE_REFERENCE = database.getReference(Constants.STRING_COMPANY_PROFILE_REFERENCE);
    public static final DatabaseReference COMPANY_POSTED_JOBS_REFERENCE = database.getReference(Constants.STRING_COMPANY_POSTED_JOBS_REFERENCE);
    public static final DatabaseReference MAKE_TRUSTED_REFERENCE = database.getReference(Constants.STRING_MAKE_TRUSTED_REFERENCE);

}
