package com.example.onlinejobportal.company;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.R;

/**
 * A simple {@link Fragment} subclass.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditJob extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentEditJob.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    private Context context;
    private View view;

    private EditText jobDueDate, jobTitle, jobLocation, jobIndustry, jobDepartment, jobEducation, jobCareer, jobSalary, jobDescription, requiredThings, jobExperience;
    private RadioButton typeFullTime, typePartTime, typeBoth, genderMale, genderFemale, genderOthers, genderAll;
    private Button submitJob, btnCompleteJob;
    private LatLng jobLocationLatLng;

    private DatePickerDialog dialog;
    private DateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
    private String uploadDate;


    private FirebaseUser firebaseUser;
    private JobModel jobModel;

    private FragmentInteractionListenerInterface mListener;


    public FragmentEditJob() {
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
            view = inflater.inflate(R.layout.fragment_create_new_job, container, false);


            initLayoutWidgets();
            oldData();
            initDatePickerDialog();

        }
        return view;
    }

    private void oldData() {
        Bundle arguments = getArguments();
        if (arguments != null) {

            try {

                jobModel = (JobModel) arguments.getSerializable(Constants.JOB_OBJECT);
                if (jobModel != null) {

                    uploadDate = jobModel.getUploadedAt();
                    jobLocationLatLng = new LatLng(CommonFunctionsClass.getLocLatitude(jobModel.getJobLocationLatLng()), CommonFunctionsClass.getLocLongitude(jobModel.getJobLocationLatLng()));
                    jobDueDate.setText(jobModel.getJobDueDate());
                    jobTitle.setText(jobModel.getJobTitle());
                    jobLocation.setText(jobModel.getJobLocation());
                    jobIndustry.setText(jobModel.getJobIndustry());
                    jobDepartment.setText(jobModel.getJobDepartment());
                    jobEducation.setText(jobModel.getJobEducation());
                    jobSalary.setText(jobModel.getJobSalary());
                    jobDescription.setText(jobModel.getJobDescription());
                    requiredThings.setText(jobModel.getRequiredThings());
                    jobCareer.setText(jobModel.getJobCareer());
                    jobExperience.setText(jobModel.getJobExperience());

                    setJobType(jobModel.getJobType());
                    setGenderFor(jobModel.getJobForGender());

                    if (jobModel.getJobStatus().equals(Constants.JOB_ACTIVE))
                        setBtnCompleteJob(jobModel);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        typeBoth = view.findViewById(R.id.typeBoth);
        genderMale = view.findViewById(R.id.genderMale);
        genderFemale = view.findViewById(R.id.genderFemale);
        genderOthers = view.findViewById(R.id.genderOthers);
        genderAll = view.findViewById(R.id.genderAll);
        jobExperience = view.findViewById(R.id.jobExperience);
        submitJob = view.findViewById(R.id.submitJob);
        btnCompleteJob = view.findViewById(R.id.btnCompleteJob);


        setClickListeners();
    }

    private void setClickListeners() {
        jobLocation.setOnClickListener(this);
        submitJob.setOnClickListener(this);
        jobDueDate.setOnClickListener(this);
    }


    private void setJobType(String type) {
        switch (type) {
            case Constants.JOB_TYPE_FULL_TIME:
                typeFullTime.setChecked(true);
                break;
            case Constants.JOB_TYPE_PART_TIME:
                typePartTime.setChecked(true);
                break;
            case Constants.JOB_TYPE_BOTH_TIME:
                typeBoth.setChecked(true);
                break;
        }
    }

    private void setGenderFor(String genderFor) {
        switch (genderFor) {
            case Constants.GENDER_MALE:
                genderMale.setChecked(true);
                break;
            case Constants.GENDER_FEMALE:
                genderFemale.setChecked(true);
                break;
            case Constants.GENDER_OTHERS:
                genderOthers.setChecked(true);
                break;
            case Constants.GENDER_ALL:
                genderAll.setChecked(true);
                break;
        }
    }

    private String getGender() {

        if (genderMale.isChecked())
            return Constants.GENDER_MALE;
        if (genderFemale.isChecked())
            return Constants.GENDER_FEMALE;
        if (genderOthers.isChecked())
            return Constants.GENDER_OTHERS;
        if (genderAll.isChecked())
            return Constants.GENDER_ALL;

        return null;
    }

    private String getJobType() {
        if (typeFullTime.isChecked())
            return Constants.JOB_TYPE_FULL_TIME;
        if (typePartTime.isChecked())
            return Constants.JOB_TYPE_PART_TIME;
        if (typeBoth.isChecked())
            return Constants.JOB_TYPE_BOTH_TIME;

        return null;
    }

    private JobModel buildJobModelInstance(String id) {
        return new JobModel(
                id,
                Constants.JOB_ACTIVE,
                firebaseUser.getUid(),
                uploadDate,
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
                getJobType(),
                jobExperience.getText().toString().trim()
        );
    }

    private void setBtnCompleteJob(final JobModel jobModel) {
        btnCompleteJob.setVisibility(View.VISIBLE);
        btnCompleteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.child(jobModel.getJobId()).child(JobModel.STRING_JOB_STATUS).setValue(Constants.JOB_COMPLETED).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            datePicker.animate();
            Log.e(TAG, "onDateSet: " + getStringDay(day) + " " + getMonthString(month) + " " + year);
            jobDueDate.setText(getStringDay(day) + " " + getMonthString(month) + " " + year);
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

    private void initDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        dialog = new DatePickerDialog(
                context,
                mDateSetListener,
                year, month, day
        );
    }

    private void submitPost() {

        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.child(jobModel.getJobId()).setValue(buildJobModelInstance(jobModel.getJobId())).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void showDatePickerDialog() {
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jobLocation:
                pickLocation();
                break;
            case R.id.jobDueDate:
                showDatePickerDialog();
                break;
            case R.id.submitJob:
                submitPost();
                break;
        }
    }

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
