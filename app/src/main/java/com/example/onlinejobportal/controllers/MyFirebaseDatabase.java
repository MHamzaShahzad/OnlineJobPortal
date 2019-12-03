package com.example.onlinejobportal.controllers;

import com.example.onlinejobportal.common.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabase {

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference USER_PROFILE_REFERENCE = database.getReference(Constants.STRING_USER_PROFILE_REFERENCE);
    public static final DatabaseReference COMPANY_PROFILE_REFERENCE = database.getReference(Constants.STRING_COMPANY_PROFILE_REFERENCE);
    public static final DatabaseReference COMPANY_POSTED_JOBS_REFERENCE = database.getReference(Constants.STRING_COMPANY_POSTED_JOBS_REFERENCE);
    public static final DatabaseReference MAKE_TRUSTED_REFERENCE = database.getReference(Constants.STRING_MAKE_TRUSTED_REFERENCE);
    public static final DatabaseReference HIRING_REQUESTS_REFERENCE = database.getReference(Constants.STRING_HIRING_REQUEST_REFERENCE);
    public static final DatabaseReference APPLYING_REQUESTS_REFERENCE = database.getReference(Constants.STRING_APPLYING_REQUEST_REFERENCE);
    public static final DatabaseReference CHATS_REFERENCE = database.getReference(Constants.STRING_CHATS_REFERENCE);

}
