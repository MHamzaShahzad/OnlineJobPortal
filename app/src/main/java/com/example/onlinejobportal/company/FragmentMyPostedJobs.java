package com.example.onlinejobportal.company;


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

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterAllJobs;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.JobModel;
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
public class FragmentMyPostedJobs extends Fragment {

    private static final String TAG = FragmentMyPostedJobs.class.getName();
    private Context context;
    private View view;

    private TabLayout jobsTabLayout;
    private FirebaseUser firebaseUser;
    private AdapterAllJobs adapterAllJobs;
    private RecyclerView recyclerPostedJobs;

    private ValueEventListener jobsValueEventListener;

    private List<JobModel> jobModelList, tempJobModelList;
    private FragmentInteractionListenerInterface mListener;


    public FragmentMyPostedJobs() {
        // Required empty public constructor
        jobModelList = new ArrayList<>();
        tempJobModelList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        adapterAllJobs = new AdapterAllJobs(context, jobModelList);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_posted_jobs, container, false);
            initLayoutWidgets();

            initJobsValueEventListener();
        }
        return view;
    }

    private void initLayoutWidgets() {
        jobsTabLayout = view.findViewById(R.id.jobsTabLayout);
        recyclerPostedJobs = view.findViewById(R.id.recyclerPostedJobs);

        setTabsListener();
        setRecyclerPostedJobs();
    }

    private void setRecyclerPostedJobs() {
        recyclerPostedJobs.setHasFixedSize(true);
        recyclerPostedJobs.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerPostedJobs.setAdapter(adapterAllJobs);
    }

    private void setTabsListener() {
        jobsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getJobs(Constants.JOB_ACTIVE);
                        break;
                    case 1:
                        getJobs(Constants.JOB_COMPLETED);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getJobs(String status) {
        jobModelList.clear();
        if (tempJobModelList.size() > 0) {
            for (JobModel model : tempJobModelList) {
                if (model != null && model.getJobStatus() != null && model.getJobStatus().equals(status))
                    jobModelList.add(model);
            }
            adapterAllJobs.notifyDataSetChanged();
        }
    }

    private void initJobsValueEventListener() {
        jobsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    Log.e(TAG, "onDataChange: " + dataSnapshot);

                    tempJobModelList.clear();

                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshots) {
                        try {

                            JobModel jobModel = snapshot.getValue(JobModel.class);
                            if (jobModel != null && jobModel.getUploadBy().equals(firebaseUser.getUid()))
                                tempJobModelList.add(jobModel);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    getJobs((jobsTabLayout.getSelectedTabPosition() == 0) ? Constants.JOB_ACTIVE : Constants.JOB_COMPLETED);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.addValueEventListener(jobsValueEventListener);
    }

    private void removeJobsValueEventListener() {
        if (jobsValueEventListener != null)
            MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.removeEventListener(jobsValueEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeJobsValueEventListener();
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
