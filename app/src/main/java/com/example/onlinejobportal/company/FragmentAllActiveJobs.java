package com.example.onlinejobportal.company;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterAllJobs;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class FragmentAllActiveJobs extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FragmentAllActiveJobs.class.getName();
    private Context context;
    private View view;

    private AdapterAllJobs adapterAllJobs;
    private RecyclerView recyclerAllActiveJobs;
    private ValueEventListener jobsValueEventListener;
    private List<JobModel> jobModelList;

    private EditText inputJobTitle, inputJobCity;
    private Button btnSearchJobs;

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    private void initSwipeRefreshLayout() {
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                startRefreshing();
                // Fetching data from server
                initJobsValueEventListener();
            }
        });
    }

    private void startRefreshing() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);
    }

    private void stopRefreshing() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initLayoutWidgets() {
        recyclerAllActiveJobs = view.findViewById(R.id.recyclerAllActiveJobs);
        inputJobTitle = view.findViewById(R.id.inputJobTitle);
        inputJobCity = view.findViewById(R.id.inputJobCity);
        btnSearchJobs = view.findViewById(R.id.btnSearchJobs);

        setBtnSearchJobs();
    }

    private void setBtnSearchJobs() {
        btnSearchJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputJobTitle.getText())) {
                    inputJobTitle.setError("Field is required!");
                } else if (TextUtils.isEmpty(inputJobCity.getText())) {
                    inputJobCity.setError("Field is required!");
                } else {
                    if (jobModelList != null) {
                        List<JobModel> jobModelListTemp = new ArrayList<>();
                        for (JobModel model : jobModelList)
                            if (Pattern.compile(Pattern.quote(inputJobTitle.getText().toString().trim()), Pattern.CASE_INSENSITIVE).matcher(model.getJobTitle()).find()
                                    && Pattern.compile(Pattern.quote(inputJobCity.getText().toString().trim()), Pattern.CASE_INSENSITIVE).matcher(model.getJobLocation()).find()
                            )
                                jobModelListTemp.add(model);

                        jobModelList.clear();
                        jobModelList.addAll(jobModelListTemp);
                        adapterAllJobs.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    private void setRecyclerAllActiveJobs() {
        recyclerAllActiveJobs.setHasFixedSize(true);
        recyclerAllActiveJobs.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerAllActiveJobs.setAdapter(adapterAllJobs);
    }

    private void initJobsValueEventListener() {
        removeJobsValueEventListener();
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
                    stopRefreshing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                stopRefreshing();
            }
        };
        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.addValueEventListener(jobsValueEventListener);
    }

    private void removeJobsValueEventListener() {
        if (jobsValueEventListener != null)
            MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.removeEventListener(jobsValueEventListener);
    }

    @Override
    public void onResume() {
        initSwipeRefreshLayout();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeJobsValueEventListener();
    }

    @Override
    public void onRefresh() {
        initJobsValueEventListener();
    }
}
