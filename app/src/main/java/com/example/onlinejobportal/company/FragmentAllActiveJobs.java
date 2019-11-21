package com.example.onlinejobportal.company;


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

import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterAllJobs;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentAllActiveJobs extends Fragment {

    private static final String TAG = FragmentAllActiveJobs.class.getName();
    private Context context;
    private View view;

    private AdapterAllJobs adapterAllJobs;
    private RecyclerView recyclerAllActiveJobs;
    private ValueEventListener jobsValueEventListener;
    private List<JobModel> jobModelList;

    public FragmentAllActiveJobs() {
        // Required empty public constructor
        jobModelList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        adapterAllJobs = new AdapterAllJobs(context, jobModelList);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_jobs, container, false);

            initLayoutWidgets();
            setRecyclerAllActiveJobs();
            initJobsValueEventListener();
        }
        return view;
    }

    private void initLayoutWidgets() {
        recyclerAllActiveJobs = view.findViewById(R.id.recyclerAllActiveJobs);
    }

    private void setRecyclerAllActiveJobs() {
        recyclerAllActiveJobs.setHasFixedSize(true);
        recyclerAllActiveJobs.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerAllActiveJobs.setAdapter(adapterAllJobs);
    }

    private void initJobsValueEventListener() {
        jobsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    jobModelList.clear();
                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshots) {
                        try {

                            JobModel jobModel = snapshot.getValue(JobModel.class);
                            if (jobModel != null && jobModel.getJobStatus().equals(Constants.JOB_ACTIVE))
                                jobModelList.add(jobModel);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapterAllJobs.notifyDataSetChanged();
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
}
