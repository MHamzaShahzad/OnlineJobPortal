package com.example.onlinejobportal.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.MyFirebaseStorage;
import com.example.onlinejobportal.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    TextInputEditText firstName, lastName, emailAddress, phoneNumber, userIntro, userSkills, userCurrentJob,
            userEduction, userAge, userCity, userCountry;
    RadioGroup groupGender, userMarriageStatus;
    RadioButton genderMale, genderFemale, genderRatherNotSay, usermarried, unmarried;
    Button btnSubmit;


    FirebaseUser firebaseUser;
    private static final int RESULT_LOAD_IMG = 1;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        initLayoutWidgets();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImageOnStorage();

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

    }

    private void uploadImageOnStorage(){
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

    private void uploadUserOnDatabase(String imageUrl){
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).setValue(getUserInstance(imageUrl)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Submitted";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else
                    task.getException().printStackTrace();
            }
        });
    }

    private UserProfile getUserInstance(String imageUrl){
        return new UserProfile(
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
                userSkills.getText().toString()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        getOldData();
    }

    private String getSelectedGender() {
        int id = groupGender.getCheckedRadioButtonId();

        if (id == R.id.genderMale)
            return genderMale.getText().toString();
        if (id == R.id.genderFemale)
            return genderFemale.getText().toString();
        if (id == R.id.genderRatherNotSay)
            return genderRatherNotSay.getText().toString();

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
            if (gender.equals(genderMale.getText().toString())) {
                genderMale.setChecked(true);
                return;
            }
            if (gender.equals(genderFemale.getText().toString())) {
                genderFemale.setChecked(true);
                return;
            }
            if (gender.equals(genderRatherNotSay.getText().toString())) {
                genderRatherNotSay.setChecked(true);
            }
        }

    }

    private void initLayoutWidgets() {

        profileImage = findViewById(R.id.profileImage);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        phoneNumber = findViewById(R.id.phoneNumber);
        userIntro = findViewById(R.id.userIntro);
        userSkills = findViewById(R.id.userSkills);
        userCurrentJob = findViewById(R.id.userCurrentJob);
        userEduction = findViewById(R.id.userEduction);
        userAge = findViewById(R.id.userAge);
        userCity = findViewById(R.id.userCity);
        userCountry = findViewById(R.id.userCountry);


        groupGender = findViewById(R.id.groupGender);
        userMarriageStatus = findViewById(R.id.userMarriageStatus);

        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        genderRatherNotSay = findViewById(R.id.genderRatherNotSay);
        usermarried = findViewById(R.id.usermarried);
        unmarried = findViewById(R.id.unmarried);


        btnSubmit = findViewById(R.id.btnSubmit);


    }

    private void getOldData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                        if (profile != null) {

                            try {
                                if (profile.getUserImage() != null && !profile.getUserImage().equals("null") && !profile.getUserImage().equals(""))
                                    Picasso.get().load(profile.getUserImage()).error(R.drawable.ic_launcher_background).placeholder(R.drawable.ic_launcher_background).centerInside().fit().into(profileImage);
                            }catch (Exception e){
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(UserProfileActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
