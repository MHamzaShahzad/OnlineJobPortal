package com.example.onlinejobportal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    EditText firstName, lastName, emailAddress, phoneNumber, cnicNumber, postalAddress;
    RadioGroup groupGender;
    RadioButton genderMale, genderFemale, genderRatherNotSay;
    Button btnSubmit;

    FirebaseDatabase database;
    DatabaseReference profileRef;
    FirebaseUser firebaseUser;
    private static final int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initLayoutWidgets();

        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileRef = database.getReference("Profile");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserProfile profile = new UserProfile(

                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        emailAddress.getText().toString(),
                        phoneNumber.getText().toString(),
                        cnicNumber.getText().toString(),
                        postalAddress.getText().toString(),
                        getSelectedGender(),

                );

                profileRef.child(firebaseUser.getUid()).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

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
        cnicNumber = findViewById(R.id.cnicNumber);
        postalAddress = findViewById(R.id.postalAddress);

        groupGender = findViewById(R.id.groupGender);

        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        genderRatherNotSay = findViewById(R.id.genderRatherNotSay);

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

                            firstName.setText(profile.getFirstName());
                            lastName.setText(profile.getLastName());
                            cnicNumber.setText(profile.getCnicNumber());
                            phoneNumber.setText(profile.getPhoneNumber());
                            emailAddress.setText(profile.getEmailAddress());
                            postalAddress.setText(profile.getPostalAddress());


                            setGender(profile.getGender());

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
        profileRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(valueEventListener);
    }


    private void getImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
