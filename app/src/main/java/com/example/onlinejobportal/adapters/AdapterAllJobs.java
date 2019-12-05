package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.company.FragmentEditJob;
import com.example.onlinejobportal.company.FragmentJobDescription;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllJobs extends RecyclerView.Adapter<AdapterAllJobs.Holder> {

    private Context context;
    private List<JobModel> jobModelList;
    private FirebaseUser firebaseUser;

    public AdapterAllJobs(Context context, List<JobModel> jobModelList) {
        this.context = context;
        this.jobModelList = jobModelList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_job_card_design, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final JobModel jobModel = jobModelList.get(holder.getAdapterPosition());

        holder.jobTitle.setText(jobModel.getJobTitle());
        holder.jobLocation.setText(jobModel.getJobLocation());
        holder.jobDueDate.setText(jobModel.getJobDueDate());

        getCompanyDetails(holder, jobModel.getUploadBy());

        holder.jobCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.JOB_OBJECT, jobModel);
                if (firebaseUser != null && firebaseUser.getUid().equals(jobModel.getUploadBy())) {
                    FragmentEditJob fragmentEditJob = new FragmentEditJob();
                    fragmentEditJob.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentEditJob, Constants.TITLE_EDIT_YOUR_JOB).addToBackStack(Constants.TITLE_EDIT_YOUR_JOB).commit();

                } else {
                    FragmentJobDescription fragmentJobDescription = new FragmentJobDescription();
                    fragmentJobDescription.setArguments(bundle);
                    if (firebaseUser == null)
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentJobDescription, Constants.TITLE_JOB_DESCRIPTION).addToBackStack(Constants.TITLE_JOB_DESCRIPTION).commit();
                    else
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentJobDescription).addToBackStack(null).commit();
                }
            }
        });

    }

    private void getCompanyDetails(final Holder holder, String companyId) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {
                            if (companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("null") && !companyProfileModel.getImage().equals(""))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(holder.companyImage);

                            holder.companyName.setText(companyProfileModel.getCompanyName());

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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

        private CardView jobCard;
        private CircleImageView companyImage;
        private TextView jobTitle, companyName, jobLocation, jobDueDate;

        public Holder(@NonNull View itemView) {
            super(itemView);

            jobCard = itemView.findViewById(R.id.jobCard);
            companyImage = itemView.findViewById(R.id.companyImage);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            companyName = itemView.findViewById(R.id.companyName);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobDueDate = itemView.findViewById(R.id.jobDueDate);

        }
    }
}
