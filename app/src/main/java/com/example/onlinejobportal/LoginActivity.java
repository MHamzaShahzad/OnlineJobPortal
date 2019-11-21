package com.example.onlinejobportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onlinejobportal.company.CompanySignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private Context context;
    private FirebaseAuth mAuth;

    private TextInputEditText inputEmail, inputPassword;
    private Button btnSignIn;

    private String AUTHENTICATE_AS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        AUTHENTICATE_AS = getIntent().getStringExtra(Constants.AUTHENTICATE_AS);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputEmail.length() > 0 && inputPassword.length() > 0)
                    sigIn(inputEmail.getText().toString(), inputPassword.getText().toString());
                else
                    Toast.makeText(LoginActivity.this, "Form not valid!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sigIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                CommonFunctionsClass.moveToHome(context, user.getUid());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    public void moveToSignUp(View view) {
        CommonFunctionsClass.moveToCreate(context, AUTHENTICATE_AS);
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(context, ForgotPasswordActivity.class));
    }
}