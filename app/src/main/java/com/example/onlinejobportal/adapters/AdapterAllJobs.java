package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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

        JobModel jobModel = jobModelList.get(holder.getAdapterPosition());

        holder.companyName.setText(jobModel.getUploadBy());
        holder.jobLocation.setText(jobModel.getJobLocation());
        holder.jobStatus.setText(jobModel.getJobStatus());
        holder.jobDueDate.setText(jobModel.getJobDueDate());

        getCompanyDetails(holder, jobModel.getUploadBy());

    }

    private void getCompanyDetails(final Holder holder, String companyId){
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null){

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {
                            holder.jobTitle.setText(companyProfileModel.getCompanyName());

                        }

                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return jobModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CircleImageView companyImage;
        private TextView jobTitle, companyName, jobLocation, jobStatus, jobDueDate;

        public Holder(@NonNull View itemView) {
            super(itemView);

            companyImage = itemView.findViewById(R.id.companyImage);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            companyName = itemView.findViewById(R.id.companyName);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobStatus = itemView.findViewById(R.id.jobStatus);
            jobDueDate = itemView.findViewById(R.id.jobDueDate);

        }
    }
}
