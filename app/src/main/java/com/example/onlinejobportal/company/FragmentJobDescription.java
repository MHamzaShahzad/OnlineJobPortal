package com.example.onlinejobportal.company;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinejobportal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentJobDescription extends Fragment {

    private ImageView companyImage;
    private TextView jobTitle, companyName, jobLocation, jobExperience, jobDepartment, jobUploadedAt, jobDueDate, jobIndustry, jobType, jobRequiredEducation, jobCareer, jobRequiredGender, jobDescription, jobSpecification;


    public FragmentJobDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_description, container, false);
    }

}
