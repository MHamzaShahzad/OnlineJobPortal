package com.example.onlinejobportal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.example.onlinejobportal.company.CompanySignUpActivity;
import com.example.onlinejobportal.user.UserSignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Context context;
    private SearchView searchView;
    private RecyclerView recyclerUsersProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_start);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentHolderUserProfiles, new FragmentAllUsers()).commit();

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(StartActivity.this, HomeDrawerActivity.class));
            finish();
        }
    }

    public void signIn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Login As User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToLogin(Constants.LOGIN_AS_USER);
            }
        });
        builder.setNeutralButton("Login As Company", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToLogin(Constants.LOGIN_AS_COMPANY);
            }
        });
        builder.create().show();
    }

    public void signUp(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Sign Up As User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToCreate(Constants.SIGN_UP_USER);
            }
        });
        builder.setNeutralButton("Sign Up As Company", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToCreate(Constants.SIGN_UP_COMPANY);
            }
        });
        builder.create().show();

    }

    private void moveToLogin(String loginAs) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.LOGIN_AS, loginAs);
        startActivity(intent);
        finish();
    }

    private void moveToCreate(String signUpAs) {
        Intent intent = null;

        if (signUpAs.equals(Constants.SIGN_UP_USER)) {
            intent = new Intent(context, UserSignUpActivity.class);

        }
        if (signUpAs.equals(Constants.SIGN_UP_COMPANY)) {
            intent = new Intent(context, CompanySignUpActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }


}
