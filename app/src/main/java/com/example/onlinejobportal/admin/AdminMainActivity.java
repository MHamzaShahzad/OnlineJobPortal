package com.example.onlinejobportal.admin;

import android.os.Bundle;

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.onlinejobportal.R;

public class AdminMainActivity extends AppCompatActivity implements FragmentInteractionListenerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_admin, new FragmentMakeTrustedRequests(), Constants.TITLE_APPROVAL_REQUEST).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.admin_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /*int id = item.getItemId();
        switch (id){
            case R.id.menu_make_trusted_req:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_admin, new FragmentMakeTrustedRequests()).addToBackStack(null).commit();
                return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

    }
}
