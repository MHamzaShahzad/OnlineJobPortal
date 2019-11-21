package com.example.onlinejobportal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.onlinejobportal.company.FragmentCreateEditCompanyProfile;
import com.example.onlinejobportal.company.FragmentCreateNewJob;
import com.example.onlinejobportal.company.FragmentMyPostedJobs;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.UserProfileModel;
import com.example.onlinejobportal.user.FragmentAllUsers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

public class HomeDrawerActivityCompany extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListenerInterface {

    private Context context;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private ValueEventListener companyProfileValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer_company);

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

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentAllUsers()).commit();
        loadCompanyProfileDataInNavigationHeader(navigationView.getHeaderView(0));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_drawer_activity_company, menu);
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

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentCreateEditCompanyProfile()).addToBackStack(null).commit();

        } else if (id == R.id.nav_posted_jobs) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentMyPostedJobs()).addToBackStack(null).commit();

        } else if (id == R.id.nav_upload_job) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, new FragmentCreateNewJob()).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {

            signOut();

        } else if (id == R.id.nav_share) {


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void signOut() {

        mAuth.signOut();
        startActivity(new Intent(context, StartMainActivity.class));
        finish();

    }

    @Override
    public void onFragmentInteraction(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    private void loadCompanyProfileDataInNavigationHeader(View headerView){
        final ImageView headerImageCompany = headerView.findViewById(R.id.companyProfileImageView);
        final TextView headerCompanyName = headerView.findViewById(R.id.companyNameText);
        final TextView headerCompanyEmail = headerView.findViewById(R.id.companyEmailText);
        companyProfileValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null){
                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null){
                            if (companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("null") && !companyProfileModel.getImage().equals(""))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(headerImageCompany);

                            headerCompanyName.setText(companyProfileModel.getCompanyName());
                            headerCompanyEmail.setText(companyProfileModel.getCompanyBusinessEmail());
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).addValueEventListener(companyProfileValueEventListener);
    }

    private void removeCompanyProfileEventListener(){
        if (companyProfileValueEventListener != null){
            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).removeEventListener(companyProfileValueEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        removeCompanyProfileEventListener();
        super.onDestroy();
    }
}
