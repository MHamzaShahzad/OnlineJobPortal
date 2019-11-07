package com.example.onlinejobportal.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.models.UserProfileModel;
import com.squareup.picasso.Picasso;

public class UserProfileDescriptionActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userAge, userSkill, userCurrentJob, userEmail, userPhoneNumber, userMaritalStatus, userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_description);

        initLayoutWidgets();

        // load user profile data
        loadData();
    }

    private void initLayoutWidgets(){
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userAge = findViewById(R.id.userAge);
        userSkill = findViewById(R.id.userSkill);
        userCurrentJob = findViewById(R.id.userCurrentJob);
        userEmail = findViewById(R.id.userEmail);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        userMaritalStatus = findViewById(R.id.userMaritalStatus);
        userAddress = findViewById(R.id.userAddress);
    }

    private void loadData(){
        Intent intent = getIntent();
        if (intent != null) {
            try {

                UserProfileModel userProfileModel = (UserProfileModel) intent.getSerializableExtra(Constants.USER_OBJECT);
                if (userProfileModel != null){

                    if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("") && !userProfileModel.getUserImage().equals("null") )
                        Picasso.get()
                                .load(userProfileModel.getUserImage())
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .centerInside()
                                .fit()
                                .into(profileImage);

                    userName.setText(userProfileModel.getUserFirstName() + " " + userProfileModel.getUserLastName());
                    userAge.setText(userProfileModel.getUserAge());
                    userSkill.setText(userProfileModel.getUserSkills());
                    userCurrentJob.setText(userProfileModel.getUserCurrentJob());
                    userEmail.setText(userProfileModel.getUserEmail());
                    userPhoneNumber.setText(userProfileModel.getUserPhone());
                    userMaritalStatus.setText(userProfileModel.getUserMarriageStatus());
                    userAddress.setText(userProfileModel.getUserCity() + "," + userProfileModel.getUserCountry());

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
