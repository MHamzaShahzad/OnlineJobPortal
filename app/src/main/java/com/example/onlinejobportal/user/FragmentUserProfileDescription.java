package com.example.onlinejobportal.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinejobportal.CommonFunctionsClass;
import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.models.UserProfileModel;
import com.squareup.picasso.Picasso;

public class FragmentUserProfileDescription extends Fragment {

    private Context context;
    private View view;

    private ImageView profileImage;
    private TextView userName, userAge, userSkill, userCurrentJob, userEmail, userPhoneNumber, userMaritalStatus, userAddress, userTrusted;

    public FragmentUserProfileDescription() {
        // Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

}
