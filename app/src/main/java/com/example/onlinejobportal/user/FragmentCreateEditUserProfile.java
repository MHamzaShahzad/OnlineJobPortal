package com.example.onlinejobportal.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.admin.FragmentTrustRequestDescription;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.MyFirebaseStorage;
import com.example.onlinejobportal.models.LookForTrusted;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentCreateEditUserProfile extends Fragment {

    private Context context;
    private View view;

    private ImageView verifiedIcon, notVerifiedIcon;
    private CircleImageView profileImage;
    private TextInputEditText firstName, lastName, emailAddress, phoneNumber, userIntro, userSkills, userCurrentJob,
            userEduction, userAge, userCity, userCountry;
    private RadioGroup groupGender, userMarriageStatus;
    private RadioButton genderMale, genderFemale, genderRatherNotSay, usermarried, unmarried;
    private Button btnSubmit;

    private FirebaseUser firebaseUser;

    private static final int RESULT_LOAD_IMG = 1;
    private Uri imageUri;

    private UserProfileModel profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_create_edit_user_profile, container, false);
            initLayoutWidgets();


            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isFormValid())
                        if (imageUri != null)
                            uploadImageOnStorage();
                        else
                            uploadUserOnDatabase(profile.getUserImage());

                }
            });

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getImageFromGallery();
                }
            });

            getOldData();
        }
        return view;
    }

    private void uploadImageOnStorage() {
        MyFirebaseStorage.PROFILE_PIC_STORAGE_REF.child(firebaseUser.getUid() + ".jpg").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadUserOnDatabase(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void uploadUserOnDatabase(String imageUrl) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).setValue(getUserInstance(imageUrl)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CharSequence text = "Submitted";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else
                    task.getException().printStackTrace();
            }
        });
    }

    private UserProfileModel getUserInstance(String imageUrl) {
        return new UserProfileModel(
                firebaseUser.getUid(),
                imageUrl,
                firstName.getText().toString(),
                lastName.getText().toString(),
                phoneNumber.getText().toString(),
                emailAddress.getText().toString(),
                getSelectedGender(),
                userAge.getText().toString(),
                getMarriageStatus(),
                userCity.getText().toString(),
                userIntro.getText().toString(),
                userEduction.getText().toString(),
                userCountry.getText().toString(),
                userCurrentJob.getText().toString(),
                userSkills.getText().toString(),
                profile.getUserStatus()
        );
    }

    private String getSelectedGender() {
        int id = groupGender.getCheckedRadioButtonId();

        if (id == R.id.genderMale)
            return Constants.GENDER_MALE;
        if (id == R.id.genderFemale)
            return Constants.GENDER_FEMALE;
        if (id == R.id.genderRatherNotSay)
            return Constants.GENDER_OTHERS;

        return null;

    }

    private String getMarriageStatus() {
        int id = userMarriageStatus.getCheckedRadioButtonId();

        if (id == R.id.usermarried)
            return usermarried.getText().toString();

        if (id == R.id.unmarried)
            return unmarried.getText().toString();

        return null;

    }

    private void setGender(String gender) {
        if (gender != null) {
            if (gender.equals(Constants.GENDER_MALE)) {
                genderMale.setChecked(true);
                return;
            }
            if (gender.equals(Constants.GENDER_FEMALE)) {
                genderFemale.setChecked(true);
                return;
            }
            if (gender.equals(Constants.GENDER_OTHERS)) {
                genderRatherNotSay.setChecked(true);
            }
        }

    }

    private boolean isFormValid() {
        boolean result = true;
        if (firstName.length() == 0) {
            result = false;
            firstName.setError("Field is required!");
        }
        if (lastName.length() == 0) {
            result = false;
            lastName.setError("Field is required!");
        }

        if (emailAddress.length() > 0 && !CommonFunctionsClass.isEmailValid(emailAddress.getText().toString())) {
            result = false;
            emailAddress.setError("Email not valid!");
        }

        if (phoneNumber.length() == 0) {
            result = false;
            phoneNumber.setError("Field is required!");
        }

        if (phoneNumber.length() > 0 && phoneNumber.length() != 11) {
            result = false;
            phoneNumber.setError("Phone number not valid!");
        }

        if (userCity.length() == 0) {
            result = false;
            userCity.setError("Field is required!");
        }

        if (userCountry.length() == 0) {
            result = false;
            userCountry.setError("Field is required!");
        }

        if (getMarriageStatus() == null) {
            result = false;
            Toast.makeText(context, "Select Marital status!", Toast.LENGTH_LONG).show();
        }

        if (getSelectedGender() == null) {
            result = false;
            Toast.makeText(context, "Select your gender!", Toast.LENGTH_LONG).show();
        }

        if (imageUri == null && (profile != null && profile.getUserImage() == null)) {
            result = false;
            Toast.makeText(context, "Select profile image!", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private void initLayoutWidgets() {

        verifiedIcon = view.findViewById(R.id.verifiedIcon);
        notVerifiedIcon = view.findViewById(R.id.notVerifiedIcon);
        profileImage = view.findViewById(R.id.profileImage);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        emailAddress = view.findViewById(R.id.emailAddress);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        userIntro = view.findViewById(R.id.userIntro);
        userSkills = view.findViewById(R.id.userSkills);
        userCurrentJob = view.findViewById(R.id.userCurrentJob);
        userEduction = view.findViewById(R.id.userEduction);
        userAge = view.findViewById(R.id.userAge);
        userCity = view.findViewById(R.id.userCity);
        userCountry = view.findViewById(R.id.userCountry);

        groupGender = view.findViewById(R.id.groupGender);
        userMarriageStatus = view.findViewById(R.id.userMarriageStatus);

        genderMale = view.findViewById(R.id.genderMale);
        genderFemale = view.findViewById(R.id.genderFemale);
        genderRatherNotSay = view.findViewById(R.id.genderRatherNotSay);
        usermarried = view.findViewById(R.id.usermarried);
        unmarried = view.findViewById(R.id.unmarried);

        btnSubmit = view.findViewById(R.id.btnSubmit);

    }

    private void getOldData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        profile = dataSnapshot.getValue(UserProfileModel.class);
                        if (profile != null) {

                            try {
                                if (profile.getUserImage() != null && !profile.getUserImage().equals("null") && !profile.getUserImage().equals(""))
                                    Picasso.get()
                                            .load(profile.getUserImage())
                                            .error(R.drawable.ic_launcher_background)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .centerInside().fit()
                                            .into(profileImage);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            firstName.setText(profile.getUserFirstName());
                            lastName.setText(profile.getUserLastName());
                            phoneNumber.setText(profile.getUserPhone());
                            emailAddress.setText(profile.getUserEmail());
                            userAge.setText(profile.getUserAge());
                            userIntro.setText(profile.getUserIntro());
                            userSkills.setText(profile.getUserSkills());
                            userCurrentJob.setText(profile.getUserCurrentJob());
                            userCountry.setText(profile.getUserCountry());
                            userCity.setText(profile.getUserCity());
                            userEduction.setText(profile.getUserEduction());

                            setGender(profile.getUserGender());
                            setMarriedStatus(profile.getUserMarriageStatus());

                            if (profile.getUserStatus().equals(Constants.USER_TRUSTED))
                                verifiedIcon.setVisibility(View.VISIBLE);
                            else {
                                notVerifiedIcon.setVisibility(View.VISIBLE);
                                MyFirebaseDatabase.MAKE_TRUSTED_REFERENCE.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                                            try {
                                                final LookForTrusted lookForTrusted = dataSnapshot.getValue(LookForTrusted.class);
                                                if (lookForTrusted != null) {
                                                    notVerifiedIcon.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            FragmentTrustRequestDescription description = new FragmentTrustRequestDescription();
                                                            Bundle bundle = new Bundle();
                                                            bundle.putSerializable(Constants.LOOK_FOR_TRUSTED_OBJECT, lookForTrusted);
                                                            description.setArguments(bundle);
                                                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, description).addToBackStack(null).commit();

                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else
                                            notVerifiedIcon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    FragmentApplyForVerify.newInstance().show(((FragmentActivity) context).getSupportFragmentManager(), "Apply For Verification");
                                                }
                                            });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).addListenerForSingleValueEvent(valueEventListener);
    }

    private void setMarriedStatus(String userMarriageStatus) {
        if (userMarriageStatus != null) {
            if (userMarriageStatus.equals(usermarried.getText().toString())) {
                usermarried.setChecked(true);
                return;
            }
            if (userMarriageStatus.equals(unmarried.getText().toString())) {
                unmarried.setChecked(true);
            }
        }
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        } else {
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

}
