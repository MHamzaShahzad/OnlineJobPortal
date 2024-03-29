package com.example.onlinejobportal.company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.activities.HomeDrawerActivityCompany;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CompanySignUpActivity extends AppCompatActivity {

    private static final String TAG = CompanySignUpActivity.class.getName();
    private FirebaseAuth mAuth;


    private TextInputEditText companyName, inputEmail, inputPassword;
    private Button btnSignUp;

    private Context context;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_company_sign_up);

        companyName = findViewById(R.id.companyName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid())
                    createNewUser(inputEmail.getText().toString(), inputPassword.getText().toString());
                else
                    Toast.makeText(CompanySignUpActivity.this, "Form not valid!", Toast.LENGTH_LONG).show();
            }
        });

        initProgressDialog();

    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private boolean isFormValid(){
        boolean result = true;

        if (inputEmail.length() == 0){
            inputEmail.setError("Field is required!");
            result = false;
        }

        if (inputEmail.length() > 0 && !CommonFunctionsClass.isEmailValid(inputEmail.getText().toString().trim())){
            inputEmail.setError("Email not valid!");
            result = false;
        }

        if (inputPassword.length() == 0){
            inputPassword.setError("Field is required!");
            result = false;
        }

        if (companyName.length() == 0){
            companyName.setError("Field is required!");
            result = false;
        }

        if (inputPassword.length() > 0 && inputPassword.length() < 6){
            inputPassword.setError("Password should be at least six characters long!");
            result = false;
        }

        return result;
    }

    private void createNewUser(String email, String password){
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(companyName.getText().toString())
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    uploadCompanyBasicProfile(user);
                                                }else
                                                    hideProgressDialog();
                                            }
                                        });


                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CompanySignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            hideProgressDialog();
                        }

                        // ...
                    }
                });
    }

    private void uploadCompanyBasicProfile(FirebaseUser company){
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(company.getUid()).setValue(new CompanyProfileModel(company.getDisplayName(), company.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompanySignUpActivity.this, HomeDrawerActivityCompany.class);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
    }

}
