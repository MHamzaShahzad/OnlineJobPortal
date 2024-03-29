package com.example.onlinejobportal.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinejobportal.activities.LoginActivity;
import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.SendPushNotificationFirebase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.HiringRequest;
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

import java.util.UUID;

public class FragmentUserProfileDescription extends Fragment {

    private static final String TAG = FragmentUserProfileDescription.class.getName();
    private Context context;
    private View view;

    private ImageView profileImage;
    private TextView userName, userAge, userSkill, userCurrentJob, userEmail, userPhoneNumber, userMaritalStatus, userAddress, userTrusted;
    private Button btnSendHiringRequest;

    private FirebaseUser firebaseUser;
    private FragmentInteractionListenerInterface mListener;


    public FragmentUserProfileDescription() {
        // Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_profile_description, container, false);

            initLayoutWidgets();
            // load user profile data
            loadData();

        }
        return view;
    }

    private void initLayoutWidgets() {

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

        btnSendHiringRequest = view.findViewById(R.id.btnSendHiringRequest);

    }

    private void loadData() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            try {

                UserProfileModel userProfileModel = (UserProfileModel) bundle.getSerializable(Constants.USER_OBJECT);
                if (userProfileModel != null) {

                    if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("") && !userProfileModel.getUserImage().equals("null"))
                        Picasso.get()
                                .load(userProfileModel.getUserImage())
                                .placeholder(R.drawable.useravatar)
                                .error(R.drawable.useravatar)
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

                    setBtnSendHiringRequest(userProfileModel);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setBtnSendHiringRequest(final UserProfileModel userProfileModel) {

        btnSendHiringRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (firebaseUser != null) {


                    MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {


                                boolean isRequested = false;

                                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                                for (DataSnapshot snapshot : snapshots) {
                                    try {

                                        HiringRequest hiringRequest = snapshot.getValue(HiringRequest.class);
                                        if (hiringRequest != null
                                                && hiringRequest.getHiringUserId().equals(userProfileModel.getUserId())
                                                && !hiringRequest.getHireStatus().equals(Constants.REQUEST_STATUS_HIRED)
                                                && !hiringRequest.getHireStatus().equals(Constants.REQUEST_STATUS_ACCEPTED)
                                        ) {
                                            isRequested = true;
                                            Snackbar.make(view, "Already requested", Snackbar.LENGTH_LONG).show();
                                            return;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!isRequested)
                                    showDialogForProposal(userProfileModel);

                            } else
                                showDialogForProposal(userProfileModel);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    Snackbar.make(view, "Sign In or create new account to continue!", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(context, LoginActivity.class));
                }

            }
        });
    }

    private void loadHireInstanceOnDatabase(final UserProfileModel userProfileModel, String proposal, final AlertDialog dialog) {
        final String id = UUID.randomUUID().toString();

        MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(id).setValue(buildHiringRequests(id, userProfileModel, proposal)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_HIRING_REQ).push().setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Snackbar.make(view, "Request successfully sent!", Snackbar.LENGTH_LONG).show();
                            else
                                Snackbar.make(view, "Error sending request!", Snackbar.LENGTH_LONG).show();
                        }
                    });

                    MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userProfileModel.getUserId()).child(Constants.STRING_HIRING_REQ).push().setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendPushNotificationFirebase.buildAndSendNotification(
                                        context,
                                        userProfileModel.getUserId(),
                                        "Job Offer",
                                        "Hey " + userProfileModel.getUserLastName() + " you have a new job offer."
                                );
                            }
                        }
                    });

                    dialog.dismiss();

                } else
                    Snackbar.make(view, "Error sending request!", Snackbar.LENGTH_LONG).show();


            }
        });
    }

    private HiringRequest buildHiringRequests(String id, UserProfileModel userProfileModel, String proposal) {
        return new HiringRequest(
                id,
                firebaseUser.getUid(),
                userProfileModel.getUserId(),
                Constants.REQUEST_STATUS_PENDING,
                CommonFunctionsClass.getCurrentDateTime(),
                "",
                "",
                "",
                "",
                "",
                proposal
        );
    }

    private void showDialogForProposal(final UserProfileModel userProfileModel) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_write_proposal, null);

        final EditText inputProposal = view.findViewById(R.id.inputProposal);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view).create();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputProposal.getText())) {
                    inputProposal.setError("Field is required!");
                } else if (inputProposal.length() < 50) {
                    inputProposal.setError("At least 50 characters required!");
                } else
                    loadHireInstanceOnDatabase(userProfileModel, inputProposal.getText().toString().trim(), dialog);
            }
        });

        dialog.show();
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
