package com.example.onlinejobportal.company;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinejobportal.CommonFunctionsClass;
import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.FragmentMapLocation;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterAllJobs;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentJobDescription extends Fragment {

    private static final String TAG = FragmentJobDescription.class.getName();
    private Context context;
    private View view;


    private ImageView companyImage;
    private TextView jobTitle, companyName, jobLocation, jobExperience, jobDepartment, jobUploadedAt, jobDueDate, jobIndustry, jobType, jobRequiredEducation, jobCareer, jobRequiredGender, jobDescription, jobSpecification;


    public FragmentJobDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_job_description, container, false);


            initLayoutWidgets();
            loadData();
        }
        return view;
    }

    private void initLayoutWidgets() {

        companyImage = view.findViewById(R.id.companyImage);
        jobTitle = view.findViewById(R.id.jobTitle);
        companyName = view.findViewById(R.id.companyName);
        jobLocation = view.findViewById(R.id.jobLocation);
        jobExperience = view.findViewById(R.id.jobExperience);
        jobDepartment = view.findViewById(R.id.jobDepartment);
        jobUploadedAt = view.findViewById(R.id.jobUploadedAt);
        jobDueDate = view.findViewById(R.id.jobDueDate);
        jobIndustry = view.findViewById(R.id.jobIndustry);
        jobType = view.findViewById(R.id.jobType);
        jobRequiredEducation = view.findViewById(R.id.jobRequiredEducation);
        jobCareer = view.findViewById(R.id.jobCareer);
        jobRequiredGender = view.findViewById(R.id.jobRequiredGender);
        jobDescription = view.findViewById(R.id.jobDescription);
        jobSpecification = view.findViewById(R.id.jobSpecification);

    }

    private void loadData() {
        Bundle arguments = getArguments();
        if (arguments != null) {

            try {

                final JobModel jobModel = (JobModel) arguments.getSerializable(Constants.JOB_OBJECT);
                if (jobModel != null){

                    jobTitle.setText(jobModel.getJobTitle());
                    jobLocation.setText(jobModel.getJobLocation());
                    jobExperience.setText(jobModel.getJobExperience());
                    jobDepartment.setText(jobModel.getJobDepartment());
                    jobUploadedAt.setText(jobModel.getUploadedAt());
                    jobDueDate.setText(jobModel.getJobDueDate());
                    jobIndustry.setText(jobModel.getJobIndustry());
                    jobType.setText(CommonFunctionsClass.getJobType(jobModel.getJobType()));
                    jobRequiredEducation.setText(jobModel.getJobEducation());
                    jobCareer.setText(jobModel.getJobCareer());
                    jobRequiredGender.setText(CommonFunctionsClass.getGender(jobModel.getJobForGender()));
                    jobDescription.setText(jobModel.getJobDescription());
                    jobSpecification.setText(jobModel.getRequiredThings());

                    getCompanyDetails(jobModel.getUploadBy());

                    jobLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentMapLocation mapLocationForTask = new FragmentMapLocation();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.STRING_LOCATION_ADDRESS, jobModel.getJobLocation());
                            bundle.putDouble(Constants.STRING_LOCATION_LATITUDE, CommonFunctionsClass.getLocLatitude(jobModel.getJobLocationLatLng()));
                            bundle.putDouble(Constants.STRING_LOCATION_LONGITUDE, CommonFunctionsClass.getLocLongitude(jobModel.getJobLocationLatLng()));
                            mapLocationForTask.setArguments(bundle);
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, mapLocationForTask).addToBackStack(null).commit();

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void getCompanyDetails(String companyId) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {
                            if (companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("null") && !companyProfileModel.getImage().equals(""))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(companyImage);

                            companyName.setText(companyProfileModel.getCompanyName());

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

}