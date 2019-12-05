package com.example.onlinejobportal.admin;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterTrustedUsers;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.LookForTrusted;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentMakeTrustedRequests extends Fragment {

    private static final String TAG = FragmentMakeTrustedRequests.class.getName();
    private Context context;
    private View view;

    private TabLayout makeTrustedRequestsTabs;
    private RecyclerView recyclerMakeTrusted;
    private List<LookForTrusted> lookForTrustedList, lookForTrustedListTemp;

    private ValueEventListener requestsValueEventListener;
    private FragmentInteractionListenerInterface mListener;


    public FragmentMakeTrustedRequests() {
        // Required empty public constructor
        lookForTrustedList = new ArrayList<>();
        lookForTrustedListTemp = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_make_trusted_requests, container, false);

            makeTrustedRequestsTabs = view.findViewById(R.id.makeTrustedRequestsTabs);
            makeTrustedRequestsTabs.addTab(makeTrustedRequestsTabs.newTab().setText("Un-Notified"), true);
            makeTrustedRequestsTabs.addTab(makeTrustedRequestsTabs.newTab().setText("Notified"));
            setTabSelectedListener();

            recyclerMakeTrusted = view.findViewById(R.id.recyclerMakeTrusted);
            recyclerMakeTrusted.setHasFixedSize(true);
            recyclerMakeTrusted.setLayoutManager(new LinearLayoutManager(context));

        }

        return view;

    }

    private void setTabSelectedListener() {

        makeTrustedRequestsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadTasksBasedOnTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadTasksBasedOnTabSelected(tab.getPosition());
            }
        });
    }

    private void loadTasksBasedOnTabSelected(int position) {
        switch (position) {
            case 0:
                getRequests(false);
                break;
            case 1:
                getRequests(true);
                break;
            default:
        }
    }

    private void getRequests(boolean notified) {

        lookForTrustedListTemp.clear();
        Log.e(TAG, "getRequests:-0 " + lookForTrustedListTemp.size() );

        for (final LookForTrusted lookForTrusted : lookForTrustedList) {
            if (lookForTrusted != null && lookForTrusted.isNotified() == notified) {
                Log.e(TAG, "getRequests: " + notified + " = " + lookForTrusted.isNotified());
                lookForTrustedListTemp.add(lookForTrusted);
            }
        }

        Log.e(TAG, "getRequests:-1 " + lookForTrustedListTemp.size() );
        recyclerMakeTrusted.setAdapter(new AdapterTrustedUsers(context, lookForTrustedListTemp));

    }

    private void loadRequests() {
        removeRequestsValueEventListener();
        requestsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lookForTrustedList.clear();

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                    for (DataSnapshot snapshot : snapshots) {
                        try {
                            LookForTrusted lookForTrusted = snapshot.getValue(LookForTrusted.class);
                            if (lookForTrusted != null) {
                                lookForTrustedList.add(lookForTrusted);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                loadTasksBasedOnTabSelected(makeTrustedRequestsTabs.getSelectedTabPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.MAKE_TRUSTED_REFERENCE.addValueEventListener(requestsValueEventListener);
    }

    private void removeRequestsValueEventListener() {
        if (requestsValueEventListener != null)
            MyFirebaseDatabase.MAKE_TRUSTED_REFERENCE.removeEventListener(requestsValueEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeRequestsValueEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequests();
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
