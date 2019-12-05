package com.example.onlinejobportal.company;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.controllers.MyFirebaseStorage;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class FragmentCreateEditCompanyProfile extends Fragment {

    private static final int ACTION_PICK_FROM_GALLERY = 109;
    ImageView companyImage;
    TextInputEditText companyName, companyEmailAddress, companyPhoneNumber, companyCity, companyCountry, companyAbout;
    RadioGroup companyType;
    RadioButton companyGovt, companyPvt;
    Button companyBtnSubmit;

    private FirebaseUser firebaseUser;

    private Context context;
    private View view;

    private Uri imageUri;
    private CompanyProfileModel profile;

    private FragmentInteractionListenerInterface mListener;


    public FragmentCreateEditCompanyProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_create_edit_company_profile, container, false);
            initLayoutWidgets();
        }
        return view;
    }

    private CompanyProfileModel getCompanyInstance(String imageUrl) {

        return new CompanyProfileModel(
                imageUrl,
                companyName.getText().toString(),
                companyEmailAddress.getText().toString(),
                companyPhoneNumber.getText().toString(),
                getCompanyType(),
                companyCity.getText().toString(),
                companyCountry.getText().toString(),
                companyAbout.getText().toString()
        );

    }

    private void uploadCompanyProfileOnDatabase(String imageUrl) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).setValue(getCompanyInstance(imageUrl)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CharSequence text = "Submitted";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();

                } else
                    task.getException().printStackTrace();
            }
        });
    }

    private void uploadImageOnStorage() {
        MyFirebaseStorage.COMPANY_PROFILE_STORAGE_REF.child(firebaseUser.getUid() + ".jpg").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadCompanyProfileOnDatabase(uri.toString());
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


    private void setSubmitListener() {
        companyBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid())
                    if (imageUri != null)
                        uploadImageOnStorage();
                    else
                        uploadCompanyProfileOnDatabase(profile.getImage());
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

                        profile = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (profile != null) {

                            if (profile.getImage() != null && !profile.getImage().equals("null") && !profile.getImage().equals(""))
                                Picasso.get()
                                        .load(profile.getImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(companyImage);

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

    private boolean isFormValid() {
        boolean result = true;
        if (companyName.length() == 0) {
            result = false;
            companyName.setError("Field is required!");
        }
        if (companyEmailAddress.length() == 0) {
            result = false;
            companyEmailAddress.setError("Field is required!");
        }

        if (companyEmailAddress.length() > 0 && !CommonFunctionsClass.isEmailValid(companyEmailAddress.getText().toString())) {
            result = false;
            companyEmailAddress.setError("Email not valid!");
        }

        if (companyPhoneNumber.length() == 0) {
            result = false;
            companyPhoneNumber.setError("Field is required!");
        }

        if (companyPhoneNumber.length() > 0 && companyPhoneNumber.length() != 11) {
            result = false;
            companyPhoneNumber.setError("Phone number not valid!");
        }

        if (companyCity.length() == 0) {
            result = false;
            companyCity.setError("Field is required!");
        }

        if (companyCountry.length() == 0) {
            result = false;
            companyCountry.setError("Field is required!");
        }

        if (companyAbout.length() == 0) {
            result = false;
            companyAbout.setError("Field is required!");
        }

        if (getCompanyType() == null) {
            result = false;
            Toast.makeText(context, "Select company type!", Toast.LENGTH_LONG).show();
        }

        if (imageUri == null && (profile != null && profile.getImage() == null)) {
            result = false;
            Toast.makeText(context, "Select company image!", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private void initLayoutWidgets() {

        companyImage = view.findViewById(R.id.companyImage);
        companyName = view.findViewById(R.id.companyName);
        companyEmailAddress = view.findViewById(R.id.companyEmailAddress);
        companyPhoneNumber = view.findViewById(R.id.companyPhoneNumber);
        companyCity = view.findViewById(R.id.companyCity);
        companyCountry = view.findViewById(R.id.companyCountry);
        companyAbout = view.findViewById(R.id.companyAbout);

        companyGovt = view.findViewById(R.id.companyGovt);
        companyPvt = view.findViewById(R.id.companyPvt);

        companyType = view.findViewById(R.id.companyType);

        companyBtnSubmit = view.findViewById(R.id.companyBtnSubmit);

        getOldData();
        setSubmitListener();
        loadImageFromGallery();
    }

    private void loadImageFromGallery() {
        companyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, ACTION_PICK_FROM_GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            companyImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onFragmentInteraction(this.getTag());
        }
        mListener = null;
    }

}
