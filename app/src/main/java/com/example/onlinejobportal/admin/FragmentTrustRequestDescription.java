package com.example.onlinejobportal.admin;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.common.FragmentMapLocation;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.LookForTrusted;
import com.example.onlinejobportal.models.UserProfileModel;
import com.example.onlinejobportal.user.FragmentUserProfileDescription;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class FragmentTrustRequestDescription extends Fragment {

    private static final String TAG = FragmentTrustRequestDescription.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 67;
    private Context context;
    private View view;

    private LookForTrusted lookForTrusted;

    private CircleImageView userProfileImage;
    private ImageView interviewHeldOnDatePicker, interviewHeldOnTimePicker;
    private TextView textUserName, placeRequestedAt, placeUserMessage;
    private EditText interviewHeldOnDate, interviewHeldOnTime, interviewLocation;
    private Button btnSend, btnVerifyUser;

    private LatLng interViewLocLatLng;

    private TimePickerDialog mTimePicker;
    private DatePickerDialog datePickerDialog;
    private DateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    private FirebaseUser firebaseUser;
    private FragmentInteractionListenerInterface mListener;


    public FragmentTrustRequestDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_trust_request_description, container, false);
            initLayoutWidgets();
            initDatePickerDialog();
            initTimePickerDialog();
            getArgumentsData();
        }
        return view;
    }

    private void initLayoutWidgets() {
        userProfileImage = view.findViewById(R.id.userProfileImage);
        interviewHeldOnDatePicker = view.findViewById(R.id.interviewHeldOnDatePicker);
        interviewHeldOnTimePicker = view.findViewById(R.id.interviewHeldOnTimePicker);
        textUserName = view.findViewById(R.id.textUserName);
        placeRequestedAt = view.findViewById(R.id.placeRequestedAt);
        placeUserMessage = view.findViewById(R.id.placeUserMessage);
        interviewHeldOnDate = view.findViewById(R.id.interviewHeldOnDate);
        interviewHeldOnTime = view.findViewById(R.id.interviewHeldOnTime);
        interviewLocation = view.findViewById(R.id.interviewLocation);
        btnSend = view.findViewById(R.id.btnSend);
        btnVerifyUser = view.findViewById(R.id.btnVerifyUser);

        interviewHeldOnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        interviewHeldOnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        interviewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser == null) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build((Activity) context), PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (firebaseUser.getUid().equals(lookForTrusted.getUserId())){
                        FragmentMapLocation mapLocation = new FragmentMapLocation();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.STRING_LOCATION_ADDRESS, lookForTrusted.getInterviewLocation());
                        bundle.putDouble(Constants.STRING_LOCATION_LATITUDE, CommonFunctionsClass.getLocLatitude(lookForTrusted.getInterViewLatLng()));
                        bundle.putDouble(Constants.STRING_LOCATION_LONGITUDE, CommonFunctionsClass.getLocLongitude(lookForTrusted.getInterViewLatLng()));
                        mapLocation.setArguments(bundle);
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, mapLocation, Constants.TITLE_INTERVIEW_LOCATION).addToBackStack(Constants.TITLE_INTERVIEW_LOCATION).commit();

                    }
                }
            }
        });
    }

    private void getArgumentsData() {

        Bundle bundleArguments = getArguments();
        if (bundleArguments != null) {

            try {

                lookForTrusted = (LookForTrusted) bundleArguments.getSerializable(Constants.LOOK_FOR_TRUSTED_OBJECT);

                if (lookForTrusted != null) {

                    getUserData(lookForTrusted.getUserId());
                    placeRequestedAt.setText(lookForTrusted.getRequestedAt());
                    placeUserMessage.setText(lookForTrusted.getUserMessage());

                    if (lookForTrusted.getInterviewHeldOnDate() != null)
                        interviewHeldOnDate.setText(lookForTrusted.getInterviewHeldOnDate());
                    if (lookForTrusted.getInterviewHeldOnTime() != null)
                        interviewHeldOnTime.setText(lookForTrusted.getInterviewHeldOnTime());
                    if (lookForTrusted.getInterviewLocation() != null)
                        interviewLocation.setText(lookForTrusted.getInterviewLocation());

                    placeRequestedAt.setText(lookForTrusted.getRequestedAt());

                    if (firebaseUser == null) {
                        if (lookForTrusted.isNotified()) {
                            btnSend.setEnabled(false);
                            setBtnVerifyUser();
                        } else
                            setBtnSend();
                    } else
                        btnSend.setVisibility(View.INVISIBLE);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setBtnVerifyUser() {
        btnVerifyUser.setVisibility(View.VISIBLE);
        btnVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(lookForTrusted.getUserId()).child(UserProfileModel.STRING_USER_STATUS_REF).setValue(Constants.USER_TRUSTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Verified Successfully!", Toast.LENGTH_LONG).show();
                            ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(context, "Can't Verify, please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void getUserData(final String userId) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        final UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {

                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("null") && !userProfileModel.getUserImage().equals(""))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .error(R.drawable.useravatar)
                                        .placeholder(R.drawable.useravatar)
                                        .centerInside().fit()
                                        .into(userProfileImage);

                            textUserName.setText(userProfileModel.getUserFirstName() + " " + userProfileModel.getUserLastName());
                            textUserName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FragmentUserProfileDescription fragmentUserProfileDescription = new FragmentUserProfileDescription();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constants.USER_OBJECT, userProfileModel);
                                    fragmentUserProfileDescription.setArguments(bundle);
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentUserProfileDescription).addToBackStack(null).commit();

                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBtnSend() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid())
                    MyFirebaseDatabase.MAKE_TRUSTED_REFERENCE.child(lookForTrusted.getUserId()).setValue(buildLookForTrusted()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Send Successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Can't Send!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
        });
    }

    private LookForTrusted buildLookForTrusted() {
        return new LookForTrusted(
                lookForTrusted.getUserId(),
                lookForTrusted.getUserMessage(),
                lookForTrusted.getRequestedAt(),
                interviewLocation.getText().toString().trim(),
                interViewLocLatLng.latitude + "-" + interViewLocLatLng.longitude,
                interviewHeldOnDate.getText().toString().trim(),
                interviewHeldOnTime.getText().toString().trim(),
                true
        );
    }

    private boolean isFormValid() {
        boolean result = true;

        if (interviewHeldOnDate.length() == 0) {
            result = false;
            interviewHeldOnDate.setError("Please! select interview date.");
        }

        if (interviewHeldOnTime.length() == 0) {
            result = false;
            interviewHeldOnTime.setError("Please! select interview time.");
        }

        if (interviewLocation.length() == 0 || interViewLocLatLng == null) {
            result = false;
            interviewLocation.setError("Please! select interview location.");
        }

        return result;
    }

    private void initDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                context,
                mDateSetListener,
                year, month, day
        );
        interviewHeldOnDate.setText(formatter.format(cal.getTime()));
    }

    private void initTimePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(
                context,
                onTimeSetListener,
                hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        interviewHeldOnTime.setText(hour + ":" + minute);
    }

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            interviewHeldOnTime.setText(selectedHour + ":" + selectedMinute);
        }
    };

    private void showTimePickerDialog() {
        mTimePicker.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            datePicker.animate();
            Log.e(TAG, "onDateSet: " + getStringDay(day) + " " + getMonthString(month) + " " + year);
            interviewHeldOnDate.setText(getStringDay(day) + " " + getMonthString(month) + " " + year);
        }
    };

    private String getStringDay(int day) {
        if (day < 9)
            return "0" + day;
        else
            return String.valueOf(day);
    }

    private String getMonthString(int month) {
        String monthName = null;
        switch (month) {
            case 0:
                monthName = "Jan";
                break;
            case 1:
                monthName = "Feb";
                break;
            case 2:
                monthName = "Mar";
                break;
            case 3:
                monthName = "Apr";
                break;
            case 4:
                monthName = "May";
                break;
            case 5:
                monthName = "Jun";
                break;
            case 6:
                monthName = "Jul";
                break;
            case 7:
                monthName = "Aug";
                break;
            case 8:
                monthName = "Sep";
                break;
            case 9:
                monthName = "Oct";
                break;
            case 10:
                monthName = "Nov";
                break;
            case 11:
                monthName = "Dec";
                break;
        }
        return monthName;
    }

    private void showDatePickerDialog() {
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, context);
                if (place != null) {

                    if (place.getLatLng() != null) {

                        Log.e(TAG, "onActivityResult: " + place.getLatLng());

                        interViewLocLatLng = place.getLatLng();
                    }
                    interviewLocation.setText(place.getName() + "-" + place.getAddress());
                }

            }
        }
    } // onActivityResult Ended...
    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onFragmentInteraction(this.getTag());
        }
        mListener = null;
    }
}
