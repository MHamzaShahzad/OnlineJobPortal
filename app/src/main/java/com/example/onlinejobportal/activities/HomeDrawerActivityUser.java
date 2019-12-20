package com.example.onlinejobportal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.common.FragmentJobsAppliedAt;
import com.example.onlinejobportal.company.FragmentAllActiveJobs;
import com.example.onlinejobportal.common.FragmentHiringRequests;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.UserProfileModel;
import com.example.onlinejobportal.user.FragmentCreateEditUserProfile;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeDrawerActivityUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListenerInterface {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private ValueEventListener userProfileValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_home_user);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null)
            signOut();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentAllActiveJobs(), Constants.TITLE_JOBS).commit();
        loadUserProfileDataInNavigationHeader(navigationView.getHeaderView(0));

        CommonFunctionsClass.openChatFromNotification(getIntent(), getSupportFragmentManager(), false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonFunctionsClass.subscribeToTopic(context, firebaseUser.getUid(), true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(Constants.TITLE_JOBS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        CommonFunctionsClass.clearFragmentBackStack(getSupportFragmentManager());

        if (id == R.id.nav_home) {
            // Handle the camera action

        } else if (id == R.id.nav_profile) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentCreateEditUserProfile(), Constants.TITLE_YOUR_PROFILE).addToBackStack(Constants.TITLE_YOUR_PROFILE).commit();

        } else if (id == R.id.nav_hiring_req) {

            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY, false);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, FragmentHiringRequests.getInstance(bundle)).addToBackStack(null).commit();

        } else if (id == R.id.nav_applying_req) {

            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY, false);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, FragmentJobsAppliedAt.getInstance(bundle)).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {

            signOut();

        } else if (id == R.id.nav_contact_us) {


        } else if (id == R.id.nav_about_us) {


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUserProfileDataInNavigationHeader(View headerView) {
        final ImageView headerImageUser = headerView.findViewById(R.id.userProfileImageView);
        final TextView headerUserName = headerView.findViewById(R.id.userNameText);
        final TextView headerUserEmail = headerView.findViewById(R.id.userEmailText);
        userProfileValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {
                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("null") && !userProfileModel.getUserImage().equals(""))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .placeholder(R.drawable.useravatar)
                                        .error(R.drawable.useravatar)
                                        .centerInside().fit()
                                        .into(headerImageUser);

                            headerUserName.setText(userProfileModel.getUserFirstName());
                            headerUserEmail.setText(userProfileModel.getUserEmail());
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
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).addValueEventListener(userProfileValueEventListener);
    }

    private void removeUserProfileEventListener() {
        if (userProfileValueEventListener != null) {
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).removeEventListener(userProfileValueEventListener);
        }
    }

    private void signOut() {

        mAuth.signOut();
        if (firebaseUser != null)
            CommonFunctionsClass.unSubscribeFromTopic(context, firebaseUser.getUid(), true);
        startActivity(new Intent(context, StartMainActivity.class));
        finish();

    }

    @Override
    protected void onDestroy() {
        removeUserProfileEventListener();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }
}
