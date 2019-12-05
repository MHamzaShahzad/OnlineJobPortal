package com.example.onlinejobportal.company;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinejobportal.activities.LoginActivity;
import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.common.FragmentMapLocation;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.SendPushNotificationFirebase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.ApplyingRequest;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentJobDescription extends Fragment {

    private static final String TAG = FragmentJobDescription.class.getName();
    private Context context;
    private View view;


    private ImageView companyImage;
    private TextView jobTitle, companyName, jobLocation, jobExperience, jobDepartment, jobUploadedAt, jobDueDate, jobIndustry, jobType, jobRequiredEducation, jobCareer, jobRequiredGender, jobDescription, jobSpecification;
    private Button btnApplyForJob;

    private FirebaseUser firebaseUser;
    private FragmentInteractionListenerInterface mListener;


    public FragmentJobDescription() {
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
        btnApplyForJob = view.findViewById(R.id.btnApplyForJob);


    }

    private void setBtnApplyForJob(final JobModel jobModel) {
        btnApplyForJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser != null)
                    loadApplyingInstanceOnDatabase(jobModel);
                else {
                    Snackbar.make(view, "Sign In or create new account to continue!", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });
    }

    private void loadApplyingInstanceOnDatabase(final JobModel jobModel) {

        if (jobModel.getJobStatus().equals(Constants.JOB_ACTIVE)) {

            final String id = UUID.randomUUID().toString();
            MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(id).setValue(buildApplyingInstance(id, jobModel)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_APPLYING_REQ).push().setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "Applied Successful!", Snackbar.LENGTH_LONG).show();
                                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                                } else
                                    Snackbar.make(view, "Error in applying!", Snackbar.LENGTH_LONG).show();
                            }
                        });

                        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(jobModel.getUploadBy()).child(Constants.STRING_APPLYING_REQ).push().setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    SendPushNotificationFirebase.buildAndSendNotification(
                                            context,
                                            jobModel.getUploadBy(),
                                            "Job Request",
                                            "You have a new job request!"
                                    );
                            }
                        });

                    } else
                        Snackbar.make(view, "Error in applying!", Snackbar.LENGTH_LONG).show();
                }
            });

        } else
            Snackbar.make(view, "Sorry, this job is't active now!", Snackbar.LENGTH_LONG).show();
    }

    private ApplyingRequest buildApplyingInstance(String id, JobModel jobModel) {
        return new ApplyingRequest(
                id,
                firebaseUser.getUid(),
                jobModel.getUploadBy(),
                jobModel.getJobId(),
                CommonFunctionsClass.getCurrentDateTime(),
                "",
                "",
                "",
                "",
                Constants.REQUEST_STATUS_PENDING,
                ""
        );
    }

    private void loadData() {
        Bundle arguments = getArguments();
        if (arguments != null) {

            try {

                final JobModel jobModel = (JobModel) arguments.getSerializable(Constants.JOB_OBJECT);
                if (jobModel != null) {

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
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, mapLocationForTask, Constants.TITLE_JOB_LOCATION).addToBackStack(Constants.TITLE_JOB_LOCATION).commit();

                        }
                    });

                    setBtnApplyForJob(jobModel);

                }

            } catch (Exception e) {
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
