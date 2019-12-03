package com.example.onlinejobportal.common;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterHiringRequests;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.HiringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHiringRequests extends Fragment {

    private static final String TAG = FragmentHiringRequests.class.getName();
    private Context context;
    private View view;

    private TabLayout tabsHiringReq;
    private AdapterHiringRequests adapterHiringRequests;
    private RecyclerView recyclerHiringRequests;
    private List<HiringRequest> hiringRequestListTemp, hiringRequestList;

    private FirebaseUser firebaseUser;

    private ValueEventListener hiringRequestsEventListener;

    private Bundle arguments;

    public static FragmentHiringRequests getInstance(Bundle arguments) {
        return new FragmentHiringRequests(arguments);
    }

    private FragmentHiringRequests(Bundle arguments) {
        // Required empty public constructor
        hiringRequestListTemp = new ArrayList<>();
        hiringRequestList = new ArrayList<>();
        this.arguments = arguments;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        adapterHiringRequests = new AdapterHiringRequests(context, hiringRequestListTemp, arguments.getBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY));
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hiring_requests, container, false);

            initLayoutWidgets();
            initHiringRequestsListener();
        }
        return view;
    }

    private void initLayoutWidgets() {
        tabsHiringReq = view.findViewById(R.id.tabHiringRequests);
        recyclerHiringRequests = view.findViewById(R.id.recyclerHiringRequests);

        setTabMyTasks();
        setRecyclerHiringReq();
    }

    private void setRecyclerHiringReq() {
        recyclerHiringRequests.setHasFixedSize(true);
        recyclerHiringRequests.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerHiringRequests.setAdapter(adapterHiringRequests);
    }

    private void setTabMyTasks() {
        tabsHiringReq.addTab(tabsHiringReq.newTab().setText("Pending"), true);
        tabsHiringReq.addTab(tabsHiringReq.newTab().setText("Accepted"));
        tabsHiringReq.addTab(tabsHiringReq.newTab().setText("Rejected"));
        tabsHiringReq.addTab(tabsHiringReq.newTab().setText("Hired"));
        tabsHiringReq.addTab(tabsHiringReq.newTab().setText("Not Hired"));

        setTabSelectedListener();
    }

    private void loadRequestsBasedOnTabSelected(int position) {
        switch (position) {
            case 0:
                getHiringRequests(Constants.REQUEST_STATUS_PENDING);
                break;
            case 1:
                getHiringRequests(Constants.REQUEST_STATUS_ACCEPTED);
                break;
            case 2:
                getHiringRequests(Constants.REQUEST_STATUS_REJECTED);
                break;
            case 3:
                getHiringRequests(Constants.REQUEST_STATUS_HIRED);
                break;
            case 4:
                getHiringRequests(Constants.REQUEST_STATUS_NOT_HIRED);
                break;
            default:
        }
    }

    private void getHiringRequests(String status) {
        Log.e(TAG, "getHiringRequests: " + status);
        hiringRequestListTemp.clear();
        for (HiringRequest requestModel : hiringRequestList)
            if (requestModel != null && requestModel.getHireStatus() != null && requestModel.getHireStatus().equals(status))
                hiringRequestListTemp.add(requestModel);
        adapterHiringRequests.notifyDataSetChanged();
    }


    private void setTabSelectedListener() {
        tabsHiringReq.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRequestsBasedOnTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadRequestsBasedOnTabSelected(tab.getPosition());
            }
        });
    }

    private void initHiringRequestsListener() {
        hiringRequestsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hiringRequestList.clear();
                Log.e(TAG, "initHiringRequestsListener-onDataChange: " + dataSnapshot );
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshots)
                        loadRequests((String) snapshot.getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void loadRequests(String reqId) {
        MyFirebaseDatabase.HIRING_REQUESTS_REFERENCE.child(reqId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "loadRequests-onDataChange: " + dataSnapshot );
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        HiringRequest hiringRequest = dataSnapshot.getValue(HiringRequest.class);
                        if (hiringRequest != null)
                            hiringRequestList.add(hiringRequest);

                        adapterHiringRequests.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    loadRequestsBasedOnTabSelected(tabsHiringReq.getSelectedTabPosition());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getHiringRequests() {
        if (arguments.getBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY))
            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_HIRING_REQ).addValueEventListener(hiringRequestsEventListener);
        else
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_HIRING_REQ).addValueEventListener(hiringRequestsEventListener);
    }

    private void removeHiringReqListener() {
        if (arguments.getBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY))
            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_HIRING_REQ).removeEventListener(hiringRequestsEventListener);
        else
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_HIRING_REQ).removeEventListener(hiringRequestsEventListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerHiringReq();
        setTabSelectedListener();
        getHiringRequests();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeHiringReqListener();
    }
}
