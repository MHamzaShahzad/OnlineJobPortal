package com.example.onlinejobportal.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.activities.HomeDrawerActivityCompany;
import com.example.onlinejobportal.activities.HomeDrawerActivityUser;
import com.example.onlinejobportal.activities.LoginActivity;
import com.example.onlinejobportal.company.CompanySignUpActivity;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.user.UserSignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonFunctionsClass {

    private static final String TAG = CommonFunctionsClass.class.getName();

    public static void subscribeToTopic(final Context context, final String topic, final boolean isHidden) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (isHidden)
                        Log.d(TAG, "onComplete: subscription to " + topic + " successful!");
                    else
                        Toast.makeText(context, "Subscription to " + topic + " successful.", Toast.LENGTH_LONG).show();
                } else {
                    if (isHidden)
                        Log.d(TAG, "onComplete: can't subscribe successfully to " + topic);
                    else
                        Toast.makeText(context, "Subscription to " + topic + " failed.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public static void unSubscribeFromTopic(final Context context, final String topic, final boolean isHidden) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (isHidden)
                        Log.d(TAG, "onComplete: un-subscribed from " + topic + " successful!");
                    else
                        Toast.makeText(context, "Un-Subscribed from " + topic + " successful.", Toast.LENGTH_LONG).show();
                } else {
                    if (isHidden)
                        Log.d(TAG, "onComplete: can't un-subscribe successfully from " + topic);
                    else
                        Toast.makeText(context, "Un-Subscribed from " + topic + " failed.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public static boolean isEmailValid(CharSequence charSequence) {
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }

    public static void moveToHome(final Context context, String uId, final Bundle bundle) {


        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Intent intent = new Intent(context, HomeDrawerActivityCompany.class);
                    intent.putExtra(Constants.NOTIFICATION_CHAT_DATA_BUNDLE, bundle);
                    context.startActivity(intent);
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
                    Intent intent = new Intent(context, HomeDrawerActivityUser.class);
                    intent.putExtra(Constants.NOTIFICATION_CHAT_DATA_BUNDLE, bundle);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void openChatFromNotification(Intent intent, FragmentManager fragmentManager, boolean seenByCompanyOrNot) {
        if (intent != null) {
            Bundle notiChatBundle = intent.getBundleExtra(Constants.NOTIFICATION_CHAT_DATA_BUNDLE);
            if (notiChatBundle != null)
                try {
                    notiChatBundle.putBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY, seenByCompanyOrNot);
                    fragmentManager.beginTransaction().replace(R.id.fragment_home, FragmentChat.getInstance(notiChatBundle), Constants.TITLE_APPLICANT_REQUEST_DESCRIPTION).addToBackStack(Constants.TITLE_APPLICANT_REQUEST_DESCRIPTION).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
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

    public static String getGender(String gender) {
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
        return gender;
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

    public static void clearFragmentBackStack(FragmentManager fragmentManager) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
            fragmentManager.popBackStack();
    }

    public static String getUserStatusString(String status) {
        switch (status) {
            case Constants.USER_TRUSTED:
                return Constants.STRING_USER_TRUSTED;
            case Constants.USER_NOT_TRUSTED:
                return Constants.STRING_USER_NOT_TRUSTED;
            default:
                return null;
        }
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("dd MM yyyy hh:mm a").format(Calendar.getInstance().getTime());
    }

}
