package com.example.onlinejobportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinejobportal.models.UserProfile;
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

                UserProfile userProfile = (UserProfile) intent.getSerializableExtra(Constants.USER_OBJECT);
                if (userProfile != null){

                    if (userProfile.getUserImage() != null && !userProfile.getUserImage().equals("") && !userProfile.getUserImage().equals("null") )
                        Picasso.get()
                                .load(userProfile.getUserImage())
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .centerInside()
                                .fit()
                                .into(profileImage);

                    userName.setText(userProfile.getUserFirstName() + " " + userProfile.getUserLastName());
                    userAge.setText(userProfile.getUserAge());
                    userSkill.setText(userProfile.getUserSkills());
                    userCurrentJob.setText(userProfile.getUserCurrentJob());
                    userEmail.setText(userProfile.getUserEmail());
                    userPhoneNumber.setText(userProfile.getUserPhone());
                    userMaritalStatus.setText(userProfile.getUserMarriageStatus());
                    userAddress.setText(userProfile.getUserCity() + "," + userProfile.getUserCountry());

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
