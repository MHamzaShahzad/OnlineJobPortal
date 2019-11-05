package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.models.JobModel;

import java.util.List;

public class AdapterAllJobs extends RecyclerView.Adapter<AdapterAllJobs.Holder> {

    private Context context;
    private List<JobModel> jobModelList;

    public AdapterAllJobs(Context context, List<JobModel> jobModelList) {
        this.context = context;
        this.jobModelList = jobModelList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_job_card_design, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return jobModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
