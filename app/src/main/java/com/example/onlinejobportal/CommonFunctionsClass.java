package com.example.onlinejobportal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.onlinejobportal.company.CompanySignUpActivity;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.user.UserSignUpActivity;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CommonFunctionsClass {


    public static boolean isEmailValid(CharSequence charSequence) {
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }

    public static void moveToHome(final Context context, String uId) {

        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    context.startActivity(new Intent(context, HomeDrawerActivityCompany.class));
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    context.startActivity(new Intent(context, HomeDrawerActivityUser.class));
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void moveToCreate(Context context, String signUpAs) {
        Intent intent = null;

        if (signUpAs.equals(Constants.AUTHENTICATE_AS_USER)) {
            intent = new Intent(context, UserSignUpActivity.class);

        }
        if (signUpAs.equals(Constants.AUTHENTICATE_AS_COMPANY)) {
            intent = new Intent(context, CompanySignUpActivity.class);
        }

        if (intent != null) {
            context.startActivity(intent);
        }
    }

    public static void moveToLogin(Context context, String loginAs) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.AUTHENTICATE_AS, loginAs);
        context.startActivity(intent);
    }

    public static String getJobType(String type) {
        switch (type) {
            case Constants.JOB_TYPE_FULL_TIME:
                return Constants.STRING_JOB_TYPE_FULL_TIME;
            case Constants.JOB_TYPE_PART_TIME:
                return Constants.STRING_JOB_TYPE_PART_TIME;
            case Constants.JOB_TYPE_BOTH_TIME:
                return Constants.STRING_JOB_TYPE_BOTH_TIME;
        }
        return "";
    }

    public static String getGender(String gender){
        switch (gender) {
            case Constants.GENDER_MALE:
                return Constants.STRING_GENDER_MALE;
            case Constants.GENDER_FEMALE:
                return Constants.STRING_GENDER_FEMALE;
            case Constants.GENDER_OTHERS:
                return Constants.STRING_GENDER_OTHERS;
            case Constants.GENDER_ALL:
                return Constants.STRING_GENDER_ALL;
        }
        return "";
    }

    public static double getLocLatitude(String latLng) {
        if (latLng != null) {
            if (latLng.contains("-")) {
                return Double.parseDouble(latLng.split("-")[0]);
            }
        }
        return 0.0;
    }

    public static double getLocLongitude(String latLng) {
        if (latLng != null) {
            if (latLng.contains("-")) {
                return Double.parseDouble(latLng.split("-")[1]);
            }
        }
        return 0.0;
    }

    public static void clearFragmentBackStack(FragmentManager fragmentManager){
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
            fragmentManager.popBackStack();
    }

}
