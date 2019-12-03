package com.example.onlinejobportal.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.company.FragmentAllActiveJobs;
import com.example.onlinejobportal.user.FragmentAllUsers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;

public class StartMainActivity extends AppCompatActivity {

    private Context context;
    private FragmentAllActiveJobs fragmentAllActiveJobs;
    private FragmentAllUsers fragmentAllUsers;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_jobs:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentAllActiveJobs).commit();
                    return true;
                case R.id.navigation_users:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentAllUsers).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        fragmentAllActiveJobs = new FragmentAllActiveJobs();
        fragmentAllUsers = new FragmentAllUsers();
        setContentView(R.layout.activity_start_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelected(true);
        navView.setSelectedItemId(R.id.navigation_jobs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            CommonFunctionsClass.moveToHome(context, currentUser.getUid());
        }
    }

    public void signIn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Login As User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CommonFunctionsClass.moveToLogin(context, Constants.AUTHENTICATE_AS_USER);
            }
        });
        builder.setNeutralButton("Login As Company", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CommonFunctionsClass.moveToLogin(context, Constants.AUTHENTICATE_AS_COMPANY);
            }
        });
        builder.create().show();
    }

    public void signUp(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Sign Up As User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CommonFunctionsClass.moveToCreate(context, Constants.AUTHENTICATE_AS_USER);
            }
        });
        builder.setNeutralButton("Sign Up As Company", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CommonFunctionsClass.moveToCreate(context, Constants.AUTHENTICATE_AS_COMPANY);
            }
        });
        builder.create().show();

    }

}
