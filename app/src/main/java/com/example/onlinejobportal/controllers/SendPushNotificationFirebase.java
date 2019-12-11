package com.example.onlinejobportal.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.models.ApplyingRequest;
import com.example.onlinejobportal.models.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPushNotificationFirebase {

    private static final String TAG = SendPushNotificationFirebase.class.getName();

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" + "AAAAvO0-fEQ:APA91bGfaU9Afe2OdEiLSDGWpte6LKcJSbxy5EzucNxGEcnaKCwHPIXVH-FcOLMYYqD__OFi3gcOnlkVHx-5rX50tSfm07YU1Ru2lKRJI3w9V9qUhhnaBUOtkDPVyBYlmuM6VygRy6DA";
    private static final String contentType = "application/json";

    public static void buildAndSendNotification(Context context, String TOPIC_SEND_TO, String notificationTitle, String notificationMsg){
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", notificationTitle);
            notificationBody.put("message", notificationMsg);

            notification.put("to", "/topics/" + TOPIC_SEND_TO);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(context, notification);
    }

    public static void buildAndSendNotification(Context context, String TOPIC_SEND_TO, String notificationTitle, String notificationMsg, String chatId, String receiverId){
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {


            notificationBody.put("title", notificationTitle);
            notificationBody.put("message", notificationMsg);
            notificationBody.put(Constants.CHAT_ID_REF, chatId);
            notificationBody.put(Constants.MESSAGE_RECEIVER_ID, receiverId);

            notification.put("to", "/topics/" + TOPIC_SEND_TO);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(context, notification);
    }

    private static void sendNotification(final Context context, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
