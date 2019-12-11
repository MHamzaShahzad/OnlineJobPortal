package com.example.onlinejobportal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.admin.AdminMainActivity;
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
    private String pType;
    private ProgressDialog progressDialog;


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
    private void sigIn(String email, String password) {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                CommonFunctionsClass.moveToHome(context, user.getUid(), null);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        hideProgressDialog();

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


    public void loginAdmin(View view) {
        getAdminLoginDialog();
    }

    public void getAdminLoginDialog() {

        final AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View view = ((FragmentActivity) context).getLayoutInflater().inflate(R.layout.authenticate_admin_dialog, null);
        final EditText editText_enter_username = (EditText) view.findViewById(R.id.editText_enter_username);
        final EditText editText_enter_password = (EditText) view.findViewById(R.id.editText_enter_password);
        final Button btn_submit_pin = (Button) view.findViewById(R.id.btn_submit);

        final TextView showPassword = (TextView) view.findViewById(R.id.show_password_eye_text);
        pType = "editText_enter_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType.equals("editText_enter_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)")) {
                    pType = "editText_enter_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)";
                    editText_enter_password.setTransformationMethod(null);

                    if (editText_enter_password.getText().length() > 0)
                        editText_enter_password.setSelection(editText_enter_password.getText().length());
                    showPassword.setBackgroundResource(R.drawable.eye);
                } else {
                    pType = "editText_enter_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
                    editText_enter_password.setTransformationMethod(new PasswordTransformationMethod());
                    if (editText_enter_password.getText().length() > 0)
                        editText_enter_password.setSelection(editText_enter_password.getText().length());
                    showPassword.setBackgroundResource(R.drawable.eye1);
                }
            }
        });


        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btn_submit_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = "Admin";
                String password = "admin123!@#";
                if (editText_enter_username.length() == 0) {
                    editText_enter_username.setError("Field is required!");
                    editText_enter_username.setFocusable(true);
                } else if (editText_enter_password.length() == 0) {
                    editText_enter_password.setError("Field is required!");
                    editText_enter_password.setFocusable(true);
                } else {

                    if (editText_enter_username.getText().toString().trim().equals(username) && editText_enter_password.getText().toString().trim().equals(password)) {
                        dialog.dismiss();
                        startActivity(new Intent(context, AdminMainActivity.class));
                        finish();
                    }
                    if (!editText_enter_username.getText().toString().trim().equals(username)) {
                        editText_enter_username.setError("Invalid username!");
                        editText_enter_username.setFocusable(true);
                    }
                    if (!editText_enter_password.getText().toString().trim().equals(password)) {
                        editText_enter_password.setError("Invalid password!");
                        editText_enter_password.setFocusable(true);
                    }
                }
            }
        });
    }

}