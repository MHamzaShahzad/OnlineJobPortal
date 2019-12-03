package com.example.onlinejobportal.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.activities.HomeDrawerActivityUser;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.MyFirebaseStorage;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSignUpActivity extends AppCompatActivity {

    private static final String TAG = UserSignUpActivity.class.getName();
    private Context context;

    CircleImageView profileImage;
    TextInputEditText firstName, inputEmail, inputPassword;
    Button btnSignUp;

    private FirebaseAuth mAuth;

    private static final int RESULT_LOAD_IMG = 1;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_user_sign_up);

        initLayoutWidgets();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid(view))
                    createNewUser(inputEmail.getText().toString(), inputPassword.getText().toString());
                else
                    Toast.makeText(context, "Form not valid!", Toast.LENGTH_LONG).show();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });


    }

    private boolean isFormValid(View view) {
        boolean result = true;

        if (inputEmail.length() == 0) {
            inputEmail.setError("Field is required!");
            result = false;
        }

        if (inputEmail.length() > 0 && !CommonFunctionsClass.isEmailValid(inputEmail.getText().toString().trim())) {
            inputEmail.setError("Email not valid!");
            result = false;
        }

        if (inputPassword.length() == 0) {
            inputPassword.setError("Field is required!");
            result = false;
        }

        if (firstName.length() == 0) {
            firstName.setError("Field is required!");
            result = false;
        }

        if (inputPassword.length() > 0 && inputPassword.length() < 6) {
            inputPassword.setError("Password should be at least six characters long!");
            result = false;
        }

        if (imageUri == null) {
            Snackbar.make(view, "Please select your profile picture.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            result = false;
        }

        return result;
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void initLayoutWidgets() {

        profileImage = findViewById(R.id.profileImage);
        firstName = findViewById(R.id.firstName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

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
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageOnStorage(final FirebaseUser user) {
        MyFirebaseStorage.PROFILE_PIC_STORAGE_REF.child(user.getUid() + ".jpg").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName.getText().toString())
                                        .setPhotoUri(uri)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    uploadUserBasicProfile(user);
                                                }
                                            }
                                        });


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

    private void createNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                uploadImageOnStorage(user);
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void uploadUserBasicProfile(FirebaseUser user){
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(user.getUid()).setValue(new UserProfileModel(
                user.getUid(),
                user.getPhotoUrl().toString(),
                user.getDisplayName(),
                user.getEmail(),
                Constants.USER_NOT_TRUSTED)
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(context, HomeDrawerActivityUser.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
