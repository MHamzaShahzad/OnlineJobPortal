package com.example.onlinejobportal.company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class CompanyProfileActivity extends AppCompatActivity {

    TextInputEditText companyName, companyEmailAddress, companyPhoneNumber, companyCity, companyCountry, companyAbout;
    RadioGroup companyType;
    RadioButton companyGovt, companyPvt;
    Button companyBtnSubmit;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        initLayoutWidgets();
    }

    private CompanyProfileModel getCompanyInstance() {

        return new CompanyProfileModel(
                companyName.getText().toString(),
                companyEmailAddress.getText().toString(),
                companyPhoneNumber.getText().toString(),
                getCompanyType(),
                companyCity.getText().toString(),
                companyCountry.getText().toString(),
                companyAbout.getText().toString()
        );

    }

    private void uploadCompanyProfileOnDatabase() {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).setValue(getCompanyInstance()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Submitted";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    finish();

                } else
                    task.getException().printStackTrace();
            }
        });
    }

    private void setSubmitListener() {
        companyBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadCompanyProfileOnDatabase();

            }
        });
    }

    private String getCompanyType() {
        int id = companyType.getCheckedRadioButtonId();

        if (id == R.id.companyGovt)
            return companyGovt.getText().toString();

        if (id == R.id.companyPvt)
            return companyPvt.getText().toString();

        return null;

    }

    private void setCompanyType(String companyType) {
        if (companyType != null) {
            if (companyType.equals(companyGovt.getText().toString())) {
                companyGovt.setChecked(true);
                return;
            }
            if (companyType.equals(companyPvt.getText().toString())) {
                companyPvt.setChecked(true);
                return;
            }

        }
    }

    private void getOldData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        CompanyProfileModel profile = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (profile != null) {

                            companyName.setText(profile.getCompanyName());
                            companyEmailAddress.setText(profile.getCompanyBusinessEmail());
                            companyPhoneNumber.setText(profile.getCompanyPhone());
                            companyCity.setText(profile.getCompanyCity());
                            companyCountry.setText(profile.getCompanyCountry());
                            companyAbout.setText(profile.getCompanyAbout());

                            setCompanyType(profile.getCompanyType());

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
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).addListenerForSingleValueEvent(valueEventListener);
    }

    private void initLayoutWidgets() {

        companyName = findViewById(R.id.companyName);
        companyEmailAddress = findViewById(R.id.companyEmailAddress);
        companyPhoneNumber = findViewById(R.id.companyPhoneNumber);
        companyCity = findViewById(R.id.companyCity);
        companyCountry = findViewById(R.id.companyCountry);
        companyAbout = findViewById(R.id.companyAbout);

        companyGovt = findViewById(R.id.companyGovt);
        companyPvt = findViewById(R.id.companyPvt);

        companyType = findViewById(R.id.companyType);

        companyBtnSubmit = findViewById(R.id.companyBtnSubmit);

        getOldData();
        setSubmitListener();
    }

}
