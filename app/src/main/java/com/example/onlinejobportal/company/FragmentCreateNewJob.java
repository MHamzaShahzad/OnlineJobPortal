package com.example.onlinejobportal.company;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateNewJob extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentCreateNewJob.class.getName();
    private Context context;
    private View view;

    private EditText jobTitle, jobLocation, jobIndustry, jobDepartment, jobEducation, jobCareer, jobDescription, requiredThings;
    private CheckBox typeFullTime, typePartTime, genderMale, genderFemale, genderOthers;

    public FragmentCreateNewJob() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_create_new_job, container, false);


            initLayoutWidgets();

        }
        return view;
    }

    private void initLayoutWidgets() {

        jobTitle = view.findViewById(R.id.jobTitle);
        jobLocation = view.findViewById(R.id.jobLocation);
        jobIndustry = view.findViewById(R.id.jobIndustry);
        jobDepartment = view.findViewById(R.id.jobDepartment);
        jobEducation = view.findViewById(R.id.jobEducation);
        jobCareer = view.findViewById(R.id.jobCareer);
        jobDescription = view.findViewById(R.id.jobDescription);
        requiredThings = view.findViewById(R.id.requiredThings);
        typeFullTime = view.findViewById(R.id.typeFullTime);
        typePartTime = view.findViewById(R.id.typePartTime);
        genderMale = view.findViewById(R.id.genderMale);
        genderFemale = view.findViewById(R.id.genderFemale);
        genderOthers = view.findViewById(R.id.genderOthers);

    }

    private String getGender() {

        String jobFor = "";
        if (genderMale.isChecked())
            jobFor = Constants.GENDER_MALE;
        else if (genderFemale.isChecked())
            jobFor = jobFor + "," + Constants.GENDER_FEMALE;
        else if (genderOthers.isChecked())
            jobFor = jobFor + "," + Constants.GENDER_OTHERS;

        return jobFor;
    }

    private String getJobType() {
        String jobType = "";
        if (typeFullTime.isChecked())
             jobType = Constants.JOB_TYPE_FULL_TIME;
        else if (typePartTime.isChecked())
            jobType = jobType + "," + Constants.JOB_TYPE_PART_TIME;

        return jobType;
    }

    private void pickLocation(){

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.jobLocation:

                break;
        }
    }
}
