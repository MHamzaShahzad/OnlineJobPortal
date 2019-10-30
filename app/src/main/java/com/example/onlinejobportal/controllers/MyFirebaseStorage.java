package com.example.onlinejobportal.controllers;

import com.example.onlinejobportal.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirebaseStorage {

    public static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final StorageReference PROFILE_PIC_STORAGE_REF = storage.getReference(Constants.STRING_PROFILE_PIC_STORAGE_REF) ;

}
