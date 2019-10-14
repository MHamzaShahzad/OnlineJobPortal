package com.example.onlinejobportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    EditText firstName, lastName, emailAddress, phoneNumber, cnicNumber, postalAddress;
    RadioGroup groupGender;
    RadioButton genderMale, genderFemale, genderRatherNotSay;
    Button btnSubmit;

    FirebaseDatabase database;
    DatabaseReference profileRef;
    FirebaseUser firebaseUser;

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
                        "",
                        ""
                );

                profileRef.child(firebaseUser.getUid()).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Context context = getApplicationContext();
                            CharSequence text = "Submitted";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else
                            task.getException().printStackTrace();
                    }
                });

            }
        });

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
}
