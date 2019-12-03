package com.example.onlinejobportal.common;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterJobsAppliedAt;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.SendPushNotificationFirebase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.HiringRequest;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHiringReqDescription extends Fragment {

    private static final String TAG = FragmentHiringReqDescription.class.getName();
    private Context context;
    private View view;

    private Bundle arguments;
    private FirebaseUser firebaseUser;

    private ValueEventListener hiringReqEventListener;
    private HiringRequest hiringRequest;

    private boolean isHiringSeenByCompany;

    private CircleImageView profileImage, companyProfileImage;
    private TextView userName, userAge, userSkill, userCurrentJob, userEmail, userPhoneNumber, userMaritalStatus, userAddress, userTrusted, startChatWithUser;
    private TextView companyName, companyType, companyBusinessEmail, companyPhone, companyAbout, companyLocation, chatWithCompany;

    private LinearLayout layout_controls, layout_accept_reject_request, layout_hire_not_hire_request;
    private Button btnAcceptRequest, btnRejectRequest, btnHire, btnNotHire;
    private RelativeLayout layout_user_description, layout_company_description;

    public static FragmentHiringReqDescription getInstance(Bundle arguments) {
        return new FragmentHiringReqDescription(arguments);
    }

    private FragmentHiringReqDescription(Bundle arguments) {
        // Required empty public constructor
        this.arguments = arguments;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hiring_req_description, container, false);

            initLayoutWidgets();
        }
        return view;
    }

    private void initLayoutWidgets() {


        layout_company_description = view.findViewById(R.id.layout_company_description);
        layout_user_description = view.findViewById(R.id.layout_user_description);


        companyProfileImage = view.findViewById(R.id.companyProfileImage);
        companyName = view.findViewById(R.id.companyName);
        companyType = view.findViewById(R.id.companyType);
        companyBusinessEmail = view.findViewById(R.id.companyBusinessEmail);
        companyPhone = view.findViewById(R.id.companyPhone);
        companyAbout = view.findViewById(R.id.companyAbout);
        companyLocation = view.findViewById(R.id.companyLocation);
        chatWithCompany = view.findViewById(R.id.chatWithCompany);


        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        userAge = view.findViewById(R.id.userAge);
        userSkill = view.findViewById(R.id.userSkill);
        userCurrentJob = view.findViewById(R.id.userCurrentJob);
        userEmail = view.findViewById(R.id.userEmail);
        userPhoneNumber = view.findViewById(R.id.userPhoneNumber);
        userMaritalStatus = view.findViewById(R.id.userMaritalStatus);
        userAddress = view.findViewById(R.id.userAddress);
        userTrusted = view.findViewById(R.id.userTrusted);
        startChatWithUser = view.findViewById(R.id.startChatWithUser);

        layout_controls = view.findViewById(R.id.layout_controls);
        layout_accept_reject_request = view.findViewById(R.id.layout_accept_reject_request);
        layout_hire_not_hire_request = view.findViewById(R.id.layout_hire_not_hire_request);

        btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest);
        btnRejectRequest = view.findViewById(R.id.btnRejectRequest);
        btnHire = view.findViewById(R.id.btnHire);
        btnNotHire = view.findViewById(R.id.btnNotHire);

    }


    @Override
    public void onResume() {
        super.onResume();
        getArgumentsData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeHiringReqEventListener();
    }

    private void getArgumentsData() {
        if (arguments != null) {
            try {

                isHiringSeenByCompany = arguments.getBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY);
                hiringRequest = (HiringRequest) arguments.getSerializable(Constants.HIRING_REQ_OBJ);
                if (hiringRequest != null) {

                    initHiringReqEventListener(hiringRequest.getHiringId());
                    if (isHiringSeenByCompany) {
                        loadUserDetails(hiringRequest.getHiringUserId());
                        setStartChatWithUser(hiringRequest.getChatId(), hiringRequest.getHiringUserId());
                    } else {
                        loadCompanyDetails(hiringRequest.getHiredByCompanyId());
                        setChatWithCompany(hiringRequest.getChatId(), hiringRequest.getHiredByCompanyId());
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setChatWithCompany(final String chatId, final String receiverId) {
        chatWithCompany.setOnClickListener(new View.OnClickListener() {
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
        startChatWithUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chatId != null && !chatId.equals("") && !chatId.equals("null")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MESSAGE_RECEIVER_ID, receiverId);
                    bundle.putString(Constants.CHAT_ID_REF, chatId);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentChat.getInstance(bundle)).addToBackStack(null).commit();
                } else {

                    final String id = UUID.randomUUID().toString();
                    MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(hiringRequest.getHiringId()).child(HiringRequest.STRING_CHAT_REF).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void initHiringReqEventListener(String requestId) {

        hiringReqEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        hiringRequest = dataSnapshot.getValue(HiringRequest.class);

                        if (hiringRequest != null) {

                            switch (hiringRequest.getHireStatus()) {
                                case Constants.REQUEST_STATUS_PENDING:
                                    if (!isHiringSeenByCompany && hiringRequest.getHiringUserId().equals(firebaseUser.getUid())) {

                                        layout_controls.setVisibility(View.VISIBLE);
                                        layout_accept_reject_request.setVisibility(View.VISIBLE);
                                        setBtnAcceptRequest();
                                        setBtnRejectRequest();

                                    }
                                    break;
                                case Constants.REQUEST_STATUS_ACCEPTED:
                                    if (isHiringSeenByCompany && hiringRequest.getHiredByCompanyId().equals(firebaseUser.getUid())) {
                                        layout_controls.setVisibility(View.VISIBLE);
                                        layout_accept_reject_request.setVisibility(View.GONE);
                                        layout_hire_not_hire_request.setVisibility(View.VISIBLE);
                                        setBtnHire();
                                        setBtnNotHire();
                                    }
                                    break;
                                case Constants.REQUEST_STATUS_REJECTED:
                                    invisibleAllControllingLayouts();
                                    break;
                                case Constants.REQUEST_STATUS_HIRED:
                                    invisibleAllControllingLayouts();
                                    break;
                                case Constants.REQUEST_STATUS_NOT_HIRED:
                                    invisibleAllControllingLayouts();
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

        MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(requestId).addValueEventListener(hiringReqEventListener);
    }

    private void invisibleAllControllingLayouts() {
        layout_controls.setVisibility(View.GONE);
        layout_accept_reject_request.setVisibility(View.GONE);
        layout_hire_not_hire_request.setVisibility(View.GONE);
    }

    private void removeHiringReqEventListener() {
        if (hiringReqEventListener != null && hiringRequest != null)
            MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(hiringRequest.getHiringId()).removeEventListener(hiringReqEventListener);
    }

    private void loadUserDetails(final String userId) {
        layout_user_description.setVisibility(View.VISIBLE);
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {

                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("") && !userProfileModel.getUserImage().equals("null"))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .centerInside()
                                        .fit()
                                        .into(profileImage);

                            userName.setText(userProfileModel.getUserFirstName());
                            userAge.setText(userProfileModel.getUserAge());
                            userSkill.setText(userProfileModel.getUserSkills());
                            userCurrentJob.setText(userProfileModel.getUserCurrentJob());
                            userEmail.setText(userProfileModel.getUserEmail());
                            userPhoneNumber.setText(userProfileModel.getUserPhone());
                            userMaritalStatus.setText(userProfileModel.getUserMarriageStatus());
                            userAddress.setText(userProfileModel.getUserCity() + ", " + userProfileModel.getUserCountry());
                            userTrusted.setText(CommonFunctionsClass.getUserStatusString(userProfileModel.getUserStatus()));

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
        layout_company_description.setVisibility(View.VISIBLE);
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {

                            if (companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("") && !companyProfileModel.getImage().equalsIgnoreCase("null"))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(companyProfileImage);

                            companyName.setText(companyProfileModel.getCompanyName());
                            companyAbout.setText(companyProfileModel.getCompanyAbout());
                            companyPhone.setText(companyProfileModel.getCompanyPhone());
                            companyBusinessEmail.setText(companyProfileModel.getCompanyBusinessEmail());
                            companyLocation.setText(companyProfileModel.getCompanyCity() + ", " + companyProfileModel.getCompanyCountry());
                            companyType.setText(companyProfileModel.getCompanyType());

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
                map.put(HiringRequest.STRING_ACCEPTED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(HiringRequest.STRING_STATUS, Constants.REQUEST_STATUS_ACCEPTED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_ACCEPTED);
            }
        });
    }

    private void setBtnRejectRequest() {
        btnRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(HiringRequest.STRING_REJECTED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(HiringRequest.STRING_STATUS, Constants.REQUEST_STATUS_REJECTED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_REJECTED);
            }
        });
    }

    private void setBtnHire() {
        btnHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(HiringRequest.STRING_HIRED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(HiringRequest.STRING_STATUS, Constants.REQUEST_STATUS_HIRED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_HIRED);
            }
        });
    }

    private void setBtnNotHire() {
        btnNotHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(HiringRequest.STRING_NOT_HIRED_AT_DATE_TIME, CommonFunctionsClass.getCurrentDateTime());
                map.put(HiringRequest.STRING_STATUS, Constants.REQUEST_STATUS_NOT_HIRED);
                updateApplyingAtJobReq(map, Constants.STRING_REQUEST_STATUS_NOT_HIRED);
            }
        });
    }

    private void updateApplyingAtJobReq(HashMap<String, Object> map, final String requestStatus) {
        MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(hiringRequest.getHiringId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(view, "Updated Successfully!", Snackbar.LENGTH_LONG).show();
                    if (requestStatus.equalsIgnoreCase(Constants.STRING_REQUEST_STATUS_ACCEPTED) || requestStatus.equalsIgnoreCase(Constants.STRING_REQUEST_STATUS_REJECTED))
                        SendPushNotificationFirebase.buildAndSendNotification(
                                context,
                                hiringRequest.getHiredByCompanyId(),
                                "Job Offer Alert",
                                "Your job offer request has been " + requestStatus
                        );
                    if (requestStatus.equalsIgnoreCase(Constants.STRING_REQUEST_STATUS_HIRED) || requestStatus.equalsIgnoreCase(Constants.STRING_REQUEST_STATUS_NOT_HIRED))
                        SendPushNotificationFirebase.buildAndSendNotification(
                                context,
                                hiringRequest.getHiringUserId(),
                                "Job Alert",
                                "Your are " + requestStatus
                        );
                } else
                    Snackbar.make(view, "Can't Update, Something went wrong, please try again!", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
