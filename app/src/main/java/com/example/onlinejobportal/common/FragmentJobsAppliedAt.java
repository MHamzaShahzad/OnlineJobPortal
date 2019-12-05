package com.example.onlinejobportal.common;


import android.app.Activity;
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
import com.example.onlinejobportal.adapters.AdapterJobsAppliedAt;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.ApplyingRequest;
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
public class FragmentJobsAppliedAt extends Fragment {

    private static final String TAG = FragmentJobsAppliedAt.class.getName();
    private Context context;
    private View view;

    private FragmentInteractionListenerInterface mListener;

    public FragmentJobsAppliedAt() {
        // Required empty public constructor
    }


    private TabLayout tabApplyingRequests;
    private AdapterJobsAppliedAt adapterJobsAppliedAt;
    private RecyclerView recyclerApplyingRequests;
    private List<ApplyingRequest> applyingRequestListTemp, applyingRequestList;

    private FirebaseUser firebaseUser;

    private ValueEventListener applyingRequestsEventListener;

    private Bundle arguments;

    public static FragmentJobsAppliedAt getInstance(Bundle arguments) {
        return new FragmentJobsAppliedAt(arguments);
    }

    private FragmentJobsAppliedAt(Bundle arguments) {
        // Required empty public constructor
        applyingRequestListTemp = new ArrayList<>();
        applyingRequestList = new ArrayList<>();
        this.arguments = arguments;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        adapterJobsAppliedAt = new AdapterJobsAppliedAt(context, applyingRequestListTemp, arguments.getBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY));
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_jobs_applied_at, container, false);

            initLayoutWidgets();
            initAppliedAtRequestsListener();
        }
        return view;
    }

    private void initLayoutWidgets() {
        tabApplyingRequests = view.findViewById(R.id.tabApplyingRequests);
        recyclerApplyingRequests = view.findViewById(R.id.recyclerApplyingRequests);

        setTabMyTasks();
        setRecyclerApplyingReq();
    }

    private void setRecyclerApplyingReq() {
        recyclerApplyingRequests.setHasFixedSize(true);
        recyclerApplyingRequests.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerApplyingRequests.setAdapter(adapterJobsAppliedAt);
    }

    private void setTabMyTasks() {
        tabApplyingRequests.addTab(tabApplyingRequests.newTab().setText("Pending"), true);
        tabApplyingRequests.addTab(tabApplyingRequests.newTab().setText("Accepted"));
        tabApplyingRequests.addTab(tabApplyingRequests.newTab().setText("Rejected"));
        tabApplyingRequests.addTab(tabApplyingRequests.newTab().setText("Hired"));
        tabApplyingRequests.addTab(tabApplyingRequests.newTab().setText("Not Hired"));

        setTabSelectedListener();
    }

    private void loadRequestsBasedOnTabSelected(int position) {
        switch (position) {
            case 0:
                getApplyingRequests(Constants.REQUEST_STATUS_PENDING);
                break;
            case 1:
                getApplyingRequests(Constants.REQUEST_STATUS_ACCEPTED);
                break;
            case 2:
                getApplyingRequests(Constants.REQUEST_STATUS_REJECTED);
                break;
            case 3:
                getApplyingRequests(Constants.REQUEST_STATUS_HIRED);
                break;
            case 4:
                getApplyingRequests(Constants.REQUEST_STATUS_NOT_HIRED);
                break;
            default:
        }
    }

    private void getApplyingRequests(String status) {
        Log.e(TAG, "getApplyingRequests: " + status);
        applyingRequestListTemp.clear();
        for (ApplyingRequest requestModel : applyingRequestList)
            if (requestModel != null && requestModel.getApplyingStatus() != null && requestModel.getApplyingStatus().equals(status))
                applyingRequestListTemp.add(requestModel);
        adapterJobsAppliedAt.notifyDataSetChanged();
    }

    private void setTabSelectedListener() {
        tabApplyingRequests.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void initAppliedAtRequestsListener() {
        applyingRequestsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applyingRequestList.clear();
                Log.e(TAG, "initAppliedAtRequestsListener-onDataChange: " + dataSnapshot);
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
        MyFirebaseDatabase.APPLYING_REQUESTS_REFERENCE.child(reqId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "loadRequests-onDataChange: " + dataSnapshot);
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        ApplyingRequest applyingRequest = dataSnapshot.getValue(ApplyingRequest.class);
                        if (applyingRequest != null)
                            applyingRequestList.add(applyingRequest);

                        adapterJobsAppliedAt.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadRequestsBasedOnTabSelected(tabApplyingRequests.getSelectedTabPosition());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getApplyingRequests() {
        if (arguments.getBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY)) {
            if (mListener != null)
                mListener.onFragmentInteraction(Constants.TITLE_APPLICANT_REQUEST);
            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_APPLYING_REQ).addValueEventListener(applyingRequestsEventListener);
        } else {
            if (mListener != null)
                mListener.onFragmentInteraction(Constants.TITLE_YOUR_REQUEST);
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_APPLYING_REQ).addValueEventListener(applyingRequestsEventListener);
        }
    }

    private void removeApplyingReqListener() {
        if (arguments.getBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY))
            MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_APPLYING_REQ).removeEventListener(applyingRequestsEventListener);
        else
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_APPLYING_REQ).removeEventListener(applyingRequestsEventListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerApplyingReq();
        setTabSelectedListener();
        getApplyingRequests();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeApplyingReqListener();
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
