package com.example.onlinejobportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;


public class CompanySignup extends AppCompatActivity {

    TextInputEditText companyName, companyEmailAddress, companyPhoneNumber, companyCity, companyCountry;
    RadioGroup cmpnyType;
    RadioButton cmpnygovt, cmpnyprvt;
    Button cmpnybtnSubmit;

    private FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference cmpnyprofileref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_signup);


        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        cmpnyprofileref = database.getReference("Company Profile");


        initLayoutWidgets();
    }

    private CompanyProfile getCompanyInstance() {

        return new CompanyProfile(
                companyName.getText().toString(),
                getCompanyType(),
                companyEmailAddress.getText().toString(),
                companyPhoneNumber.getText().toString(),
                getCompanyType(),
                companyCity.getText().toString(),
                companyCountry.getText().toString()
        );

    }

    private void uploadCompanyProfileOnDatabase(){
        cmpnyprofileref.child(firebaseUser.getUid()).setValue(getCompanyInstance()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void setSubmitListener(){
        cmpnybtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadCompanyProfileOnDatabase();

            }
        });
    }

    private String getCompanyType() {
        int id = cmpnyType.getCheckedRadioButtonId();

        if (id == R.id.cmpnygovt)
            return cmpnygovt.getText().toString();

        if (id == R.id.cmpnyprvt)
            return cmpnyprvt.getText().toString();

        return null;

    }

    private void setCompanyType(String cmpnyType) {
        if (cmpnyType != null) {
            if (cmpnyType.equals(cmpnygovt.getText().toString())) {
                cmpnygovt.setChecked(true);
                return;
            }
            if (cmpnyType.equals(cmpnyprvt.getText().toString())) {
                cmpnyprvt.setChecked(true);
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

                        CompanyProfile profile = dataSnapshot.getValue(CompanyProfile.class);
                        if (profile != null) {



                            companyName.setText(profile.getcompanyName());
                            companyEmailAddress.setText(profile.getcompanyBusinessemail());
                            companyPhoneNumber.setText(profile.getcompanyPhone());
                            companyCity.setText(profile.getcompanyCity());
                            companyCountry.setText(profile.getcompanyCountry());

                            setCompanyType(profile.getselectOrganizationtype());

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
        cmpnyprofileref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(valueEventListener);
    }

    private void initLayoutWidgets() {

        companyName = findViewById(R.id.companyName);
        companyEmailAddress = findViewById(R.id.companyEmailAddress);
        companyPhoneNumber = findViewById(R.id.companyPhoneNumber);
        companyCity = findViewById(R.id.companyCity);
        companyCountry = findViewById(R.id.companyCountry);

        cmpnygovt = findViewById(R.id.cmpnygovt);
        cmpnyprvt = findViewById(R.id.cmpnyprvt);

        cmpnybtnSubmit = findViewById(R.id.cmpnybtnSubmit);

        getOldData();
        setSubmitListener();
    }

}
