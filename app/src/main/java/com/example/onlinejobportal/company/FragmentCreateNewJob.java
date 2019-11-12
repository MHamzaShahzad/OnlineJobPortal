package com.example.onlinejobportal.company;


import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.JobModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateNewJob extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentCreateNewJob.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    private Context context;
    private View view;

    private EditText jobDueDate, jobTitle, jobLocation, jobIndustry, jobDepartment, jobEducation, jobCareer, jobSalary, jobDescription, requiredThings;
    private CheckBox typeFullTime, typePartTime, genderMale, genderFemale, genderOthers;
    private Button submitJob;
    private LatLng jobLocationLatLng;

    private FirebaseUser firebaseUser;

    public FragmentCreateNewJob() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_create_new_job, container, false);


            initLayoutWidgets();

        }
        return view;
    }

    private void initLayoutWidgets() {

        jobDueDate = view.findViewById(R.id.jobDueDate);
        jobTitle = view.findViewById(R.id.jobTitle);
        jobLocation = view.findViewById(R.id.jobLocation);
        jobIndustry = view.findViewById(R.id.jobIndustry);
        jobDepartment = view.findViewById(R.id.jobDepartment);
        jobEducation = view.findViewById(R.id.jobEducation);
        jobSalary = view.findViewById(R.id.jobSalary);
        jobCareer = view.findViewById(R.id.jobCareer);
        jobDescription = view.findViewById(R.id.jobDescription);
        requiredThings = view.findViewById(R.id.requiredThings);
        typeFullTime = view.findViewById(R.id.typeFullTime);
        typePartTime = view.findViewById(R.id.typePartTime);
        genderMale = view.findViewById(R.id.genderMale);
        genderFemale = view.findViewById(R.id.genderFemale);
        genderOthers = view.findViewById(R.id.genderOthers);
        submitJob = view.findViewById(R.id.submitJob);


        setClickListeners();
    }

    private void setClickListeners() {
        jobLocation.setOnClickListener(this);
        submitJob.setOnClickListener(this);
    }

    private String getGender() {

        StringBuilder jobFor = new StringBuilder();
        if (genderMale.isChecked())
            jobFor.append(Constants.GENDER_MALE);
        else if (genderFemale.isChecked())
            if (genderMale.isChecked())
                jobFor.append(Constants.GENDER_FEMALE);
            else
                jobFor.append(",").append(Constants.GENDER_FEMALE);
        else if (genderOthers.isChecked())
            if (genderMale.isChecked())
                jobFor.append(Constants.GENDER_FEMALE);
            else
                jobFor.append(",").append(Constants.GENDER_FEMALE);

        return jobFor.toString();
    }

    private String getJobType() {
        String jobType = "";
        if (typeFullTime.isChecked())
            jobType = Constants.JOB_TYPE_FULL_TIME;
        else if (typePartTime.isChecked())
            jobType = jobType + "," + Constants.JOB_TYPE_PART_TIME;

        return jobType;
    }

    private JobModel buildJobModelInstance(String id) {
        return new JobModel(
                id,
                Constants.JOB_ACTIVE,
                firebaseUser.getUid(),
                getCurrentDate(),
                jobDueDate.getText().toString(),
                jobTitle.getText().toString().trim(),
                jobSalary.getText().toString(),
                jobLocation.getText().toString().trim(),
                jobLocationLatLng.latitude + "-" + jobLocationLatLng.longitude,
                jobDescription.getText().toString().trim(),
                jobIndustry.getText().toString().trim(),
                jobDepartment.getText().toString().trim(),
                jobEducation.getText().toString(),
                jobCareer.getText().toString().trim(),
                requiredThings.getText().toString().trim(),
                getGender(),
                getJobType()
        );
    }

    private String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }

    private void submitPost() {

        String id = UUID.randomUUID().toString();
        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.child(id).setValue(buildJobModelInstance(id)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();

                } else {
                    Toast.makeText(context, "Uploading Failed : " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void pickLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build((Activity) context), PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Place place = PlacePicker.getPlace(data, context);
            if (place != null) {

                if (place.getLatLng() != null) {

                    Log.e(TAG, "onActivityResult: " + place.getLatLng());

                    jobLocationLatLng = place.getLatLng();
                }
                jobLocation.setText(place.getName() + "-" + place.getAddress());

            }

        } else {
            Toast.makeText(context, "You haven't picked an address!", Toast.LENGTH_LONG).show();
        }


    } // onActivityResult Ended...

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jobLocation:
                pickLocation();
                break;
            case R.id.submitJob:
                submitPost();
                break;
        }
    }

}
