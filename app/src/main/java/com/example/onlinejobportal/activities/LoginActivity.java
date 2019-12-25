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
import com.example.onlinejobportal.company.CompanySignUpActivity;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.UserProfileModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getName();
    private Context context;
    private FirebaseAuth mAuth;

    private TextInputEditText inputEmail, inputPassword;
    private Button btnSignIn, login_button_fb, login_button_google;

    private String AUTHENTICATE_AS;
    private String pType;
    private ProgressDialog progressDialog;

    private String mVerificationId;
    private CallbackManager mCallbackManager;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);

        login_button_fb = findViewById(R.id.login_button_fb);
        login_button_google = findViewById(R.id.login_button_google);
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

        login_button_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithFacebook();
            }
        });

        login_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        initProgressDialog();
        initFacebookLogin();
        initGoogleSignIn();
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

    // facebook authentication
    private void initFacebookLogin() {
        // Initialize Facebook Login button

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                signInWithCredentials(credential);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos", "email", "public_profile")
        );
    }

    // google login
    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void signInWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {

        showProgressDialog();

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                signInWithCredentials(credential);
            }
        } else {
            hideProgressDialog();
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. " + result);
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithCredentials(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                                continueWithFacebookOrGoogle(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        hideProgressDialog();

                    }
                });
    }

    private void continueWithFacebookOrGoogle(final FirebaseUser user) {
        switch (AUTHENTICATE_AS) {
            case Constants.AUTHENTICATE_AS_USER:
                MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            Toast.makeText(context, "Account already exist for Company.", Toast.LENGTH_LONG).show();
                            CommonFunctionsClass.moveToHome(context, user.getUid(), null);
                        }else {
                            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null)
                                        CommonFunctionsClass.moveToHome(context, user.getUid(), null);
                                    else
                                        uploadUserBasicProfile(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
            case Constants.AUTHENTICATE_AS_COMPANY:
                MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            Toast.makeText(context, "Account already exist for User.", Toast.LENGTH_LONG).show();
                            CommonFunctionsClass.moveToHome(context, user.getUid(), null);
                        }else {
                            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null)
                                        CommonFunctionsClass.moveToHome(context, user.getUid(), null);
                                    else
                                        uploadCompanyBasicProfile(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            default:
                Log.i(TAG, "continueWithFacebookOrGoogle: OPERATION_NOT_DEFINED");
        }
    }

    private void uploadCompanyBasicProfile(FirebaseUser company) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(company.getUid()).setValue(new CompanyProfileModel(String.valueOf(company.getPhotoUrl()), company.getDisplayName(), company.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, HomeDrawerActivityCompany.class);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
    }

    private void uploadUserBasicProfile(FirebaseUser user) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(user.getUid()).setValue(new UserProfileModel(
                user.getUid(),
                String.valueOf(user.getPhotoUrl()),
                user.getDisplayName(),
                user.getEmail(),
                Constants.USER_NOT_TRUSTED)
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(context, HomeDrawerActivityUser.class);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
            googleApiClient.disconnect();
        } else // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}