package com.example.onlinejobportal.common;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.SendPushNotificationFirebase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.ApplyingRequest;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAppliedAtJobDescription extends Fragment {

    private static final String TAG = FragmentAppliedAtJobDescription.class.getName();
    private Context context;
    private View view;

    private Bundle arguments;
    private FirebaseUser firebaseUser;

    private ValueEventListener jobRequestEventListener;
    private ApplyingRequest applyingRequest;

    private boolean isAppliedAtJobSeenByCompany;
    private ImageView imageView;
    private TextView jobTitle, companyName, jobLocation, jobExperience, jobDepartment, jobUploadedAt, jobDueDate, jobIndustry, jobType, jobRequiredEducation, jobCareer, jobRequiredGender, jobDescription, jobSpecification;
    private LinearLayout layout_company_controls, layout_accept_reject_request, layout_hire_not_hire_request;
    private RelativeLayout layout_applicant_details;
    private Button btnAcceptRequest, btnRejectRequest, btnHire, btnNotHire;

    private TextView
            btnChat, applicantName, applicantTrustedOrNot, applicantAge, applicantEmail,
            applicantMaritalStatus, applicantSkills, applicantEducation,
            applicantCurrentJob, applicantCurrentAddress, jobProposal;

    private FragmentInteractionListenerInterface mListener;


    public static FragmentAppliedAtJobDescription getInstance(Bundle arguments) {
        return new FragmentAppliedAtJobDescription(arguments);
    }

    private FragmentAppliedAtJobDescription(Bundle arguments) {
        // Required empty public constructor
        this.arguments = arguments;
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
            view = inflater.inflate(R.layout.fragment_applied_at_job_description, container, false);

            initLayoutWidgets();
        }
        return view;
    }

    private void initLayoutWidgets() {

        layout_applicant_details = view.findViewById(R.id.layout_applicant_details);
        layout_company_controls = view.findViewById(R.id.layout_company_controls);
        layout_accept_reject_request = view.findViewById(R.id.layout_accept_reject_request);
        layout_hire_not_hire_request = view.findViewById(R.id.layout_hire_not_hire_request);

        btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest);
        btnRejectRequest = view.findViewById(R.id.btnRejectRequest);
        btnHire = view.findViewById(R.id.btnHire);
        btnNotHire = view.findViewById(R.id.btnNotHire);
        btnChat = view.findViewById(R.id.btnChat);

        imageView = view.findViewById(R.id.imageView);
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
        jobProposal = view.findViewById(R.id.jobProposal);

        applicantName = view.findViewById(R.id.applicantName);
        applicantTrustedOrNot = view.findViewById(R.id.applicantTrustedOrNot);
        applicantAge = view.findViewById(R.id.applicantAge);
        applicantEmail = view.findViewById(R.id.applicantEmail);
        applicantSkills = view.findViewById(R.id.applicantSkills);
        applicantMaritalStatus = view.findViewById(R.id.applicantMaritalStatus);
        applicantEducation = view.findViewById(R.id.applicantEducation);
        applicantCurrentJob = view.findViewById(R.id.applicantCurrentJob);
        applicantCurrentAddress = view.findViewById(R.id.applicantCurrentAddress);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeJobEventListener();
    }

    private void getArgumentsData() {

        if (arguments != null) {

            try {

                isAppliedAtJobSeenByCompany = arguments.getBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY);
                applyingRequest = (ApplyingRequest) arguments.getSerializable(Constants.APPLYING_REQ_OBJ);

                if (applyingRequest != null) {
                    initJobReqListener(applyingRequest.getRequestId());
                    if (isAppliedAtJobSeenByCompany) {
                        loadUserDetails(applyingRequest.getApplierId());
                        setStartChatWithUser(applyingRequest.getChatId(), applyingRequest.getApplierId());
                    }else {
                        setChatWithCompany(applyingRequest.getChatId(), applyingRequest.getApplyingAtCompanyId());
                    }
                    loadCompanyDetails(applyingRequest.getApplyingAtCompanyId());
                    loadJobDetails(applyingRequest.getApplyingAtJobId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void setChatWithCompany(final String chatId, final String receiverId) {
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatId != null && !chatId.equals("") && !chatId.equals("null")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MESSAGE_RECEIVER_ID, receiverId);
                    bundle.putString(Constants.CHAT_ID_REF, chatId);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentChat.getInstance(bundle)).addToBackStack(null).commit();
                } else
                    Snackbar.make(view, "Company did't create chat with you!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setStartChatWithUser(final String chatId, final String receiverId) {
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chatId != null && !chatId.equals("") && !chatId.equals("null")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MESSAGE_RECEIVER_ID, receiverId);
                    bundle.putString(Constants.CHAT_ID_REF, chatId);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentChat.getInstance(bundle)).addToBackStack(null).commit();
                } else {

                    final String id = UUID.randomUUID().toString();
                    MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(applyingRequest.getRequestId()).child(ApplyingRequest.STRING_CHAT_REF).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                MyFirebaseDatabase.CHATS_REFERENCE.child(id).push().setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString(Constants.MESSAGE_RECEIVER_ID, receiverId);
                                            bundle.putString(Constants.CHAT_ID_REF, id);
                                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentChat.getInstance(bundle)).addToBackStack(null).commit();

                                        }
                                    }
                                });
                        }
                    });
                }

            }
        });
    }

    // request listener

    private void initJobReqListener(String requestId) {

        jobRequestEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        applyingRequest = dataSnapshot.getValue(ApplyingRequest.class);
                        if (applyingRequest != null) {

                            Log.e(TAG, "onDataChange: " + applyingRequest.getRequestProposal() );
                            jobProposal.setText(applyingRequest.getRequestProposal());

                            switch (applyingRequest.getApplyingStatus()) {
                                case Constants.REQUEST_STATUS_PENDING:
                                    if (isAppliedAtJobSeenByCompany && applyingRequest.getApplyingAtCompanyId().equals(firebaseUser.getUid())) {
                                        layout_company_controls.setVisibility(View.VISIBLE);
                                        layout_accept_reject_request.setVisibility(View.VISIBLE);
                                        setBtnAcceptRequest();
                                        setBtnRejectRequest();
                                    }

                                    break;
                                case Constants.REQUEST_STATUS_ACCEPTED:
                                    if (isAppliedAtJobSeenByCompany && applyingRequest.getApplyingAtCompanyId().equals(firebaseUser.getUid())) {
                                        layout_company_controls.setVisibility(View.VISIBLE);
                                        layout_accept_reject_request.setVisibility(View.GONE);
                                        layout_hire_not_hire_request.setVisibility(View.VISIBLE);
                                        setBtnHire();
                                        setBtnNotHire();
                                    }
                                    break;
                                case Constants.REQUEST_STATUS_REJECTED:
                                    layout_company_controls.setVisibility(View.GONE);
                                    layout_accept_reject_request.setVisibility(View.GONE);
                                    layout_hire_not_hire_request.setVisibility(View.GONE);
                                    break;
                                case Constants.REQUEST_STATUS_HIRED:
                                    layout_company_controls.setVisibility(View.GONE);
                                    layout_accept_reject_request.setVisibility(View.GONE);
                                    layout_hire_not_hire_request.setVisibility(View.GONE);
                                    break;
                                case Constants.REQUEST_STATUS_NOT_HIRED:
                                    layout_company_controls.setVisibility(View.GONE);
                                    layout_accept_reject_request.setVisibility(View.GONE);
                                    layout_hire_not_hire_request.setVisibility(View.GONE);
                                    break;
                                default:
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(requestId).addValueEventListener(jobRequestEventListener);
    }

    private void removeJobEventListener() {
        if (jobRequestEventListener != null && applyingRequest != null)
            MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(applyingRequest.getRequestId()).removeEventListener(jobRequestEventListener);
    }

    // load data

    private void loadJobDetails(final String jobId) {
        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        final JobModel jobModel = dataSnapshot.getValue(JobModel.class);
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

    private void loadUserDetails(final String userId) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {

                            layout_applicant_details.setVisibility(View.VISIBLE);

                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("") && !userProfileModel.getUserImage().equals("null"))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .placeholder(R.drawable.useravatar)
                                        .error(R.drawable.useravatar)
                                        .centerInside()
                                        .fit()
                                        .into(imageView);

                            applicantName.setText(userProfileModel.getUserFirstName());
                            applicantAge.setText(userProfileModel.getUserAge());
                            applicantSkills.setText(userProfileModel.getUserSkills());
                            applicantCurrentJob.setText(userProfileModel.getUserCurrentJob());
                            applicantEmail.setText(userProfileModel.getUserEmail());
                            applicantMaritalStatus.setText(userProfileModel.getUserMarriageStatus());
                            applicantCurrentAddress.setText(userProfileModel.getUserCity() + ", " + userProfileModel.getUserCountry());
                            applicantTrustedOrNot.setText(CommonFunctionsClass.getUserStatusString(userProfileModel.getUserStatus()));
                            applicantEducation.setText(userProfileModel.getUserEduction());
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

    private void loadCompanyDetails(final String companyId) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {
                            if (!isAppliedAtJobSeenByCompany && companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("null") && !companyProfileModel.getImage().equals(""))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .error(R.drawable.image_placeholder)
                                        .placeholder(R.drawable.image_placeholder)
                                        .centerInside().fit()
                                        .into(imageView);

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


    // Click Listeners

    private void setBtnAcceptRequest() {
        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(ApplyingRequest.STRING_ACCEPTED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(ApplyingRequest.STRING_STATUS, Constants.REQUEST_STATUS_ACCEPTED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_ACCEPTED);
            }
        });
    }

    private void setBtnRejectRequest() {
        btnRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(ApplyingRequest.STRING_REJECTED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(ApplyingRequest.STRING_STATUS, Constants.REQUEST_STATUS_REJECTED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_REJECTED);
            }
        });
    }

    private void setBtnHire() {
        btnHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(ApplyingRequest.STRING_NOT_HIRED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(ApplyingRequest.STRING_STATUS, Constants.REQUEST_STATUS_HIRED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_HIRED);
            }
        });
    }

    private void setBtnNotHire() {
        btnNotHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(ApplyingRequest.STRING_NOT_HIRED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(ApplyingRequest.STRING_STATUS, Constants.REQUEST_STATUS_NOT_HIRED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_NOT_HIRED);
            }
        });
    }

    private void updateApplyingAtJobReq(HashMap<String, Object> map, final String requestStatus) {
        MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(applyingRequest.getRequestId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(view, "Updated Successfully!", Snackbar.LENGTH_LONG).show();
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            applyingRequest.getApplierId(),
                            "Job Alert",
                            "Your job request has been " + requestStatus
                    );
                } else
                    Snackbar.make(view, "Can't Update, Something went wrong, please try again!", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getArgumentsData();
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
