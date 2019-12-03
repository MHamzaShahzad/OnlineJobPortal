package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.common.FragmentAppliedAtJobDescription;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.ApplyingRequest;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.JobModel;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterJobsAppliedAt extends RecyclerView.Adapter<AdapterJobsAppliedAt.Holder> {

    private Context context;
    private List<ApplyingRequest> applyingRequestList;
    private boolean isSeenByCompany;

    public AdapterJobsAppliedAt(Context context, List<ApplyingRequest> applyingRequestList, boolean isSeenByCompany) {
        this.context = context;
        this.applyingRequestList = applyingRequestList;
        this.isSeenByCompany = isSeenByCompany;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_applying_at_job_card, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final ApplyingRequest applyingRequest = applyingRequestList.get(holder.getAdapterPosition());

        switch (applyingRequest.getApplyingStatus()) {
            case Constants.REQUEST_STATUS_PENDING:
                holder.requestedAt.setText(applyingRequest.getApplyingAtDateTime());
                break;
            case Constants.REQUEST_STATUS_ACCEPTED:
                holder.requestedAt.setText(applyingRequest.getAcceptedAtDateTime());
                break;
            case Constants.REQUEST_STATUS_REJECTED:
                holder.requestedAt.setText(applyingRequest.getRejectedAtDateTime());
                break;
            case Constants.REQUEST_STATUS_HIRED:
                holder.requestedAt.setText(applyingRequest.getHiredAtDateTime());
                break;
            case Constants.REQUEST_STATUS_NOT_HIRED:
                holder.requestedAt.setText(applyingRequest.getNotHiredAtDateTime());
                break;
            default:
        }

        loadJobDetails(holder, applyingRequest.getApplyingAtJobId());

        if (isSeenByCompany)
            loadUserDetails(holder, applyingRequest.getApplierId());
        else
            loadCompanyDetails(holder, applyingRequest.getApplyingAtCompanyId());

        holder.cardAppliedAtJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_APPLYING_SEEN_BY_COMPANY, isSeenByCompany);
                bundle.putSerializable(Constants.APPLYING_REQ_OBJ, applyingRequestList.get(holder.getAdapterPosition()));
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentAppliedAtJobDescription.getInstance(bundle)).addToBackStack(null).commit();
            }
        });


    }


    @Override
    public int getItemCount() {
        return applyingRequestList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView cardAppliedAtJob;
        private ImageView imageView;
        private TextView jobTitle, entityName, requestedAt, entityType;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardAppliedAtJob = itemView.findViewById(R.id.cardAppliedAtJob);
            imageView = itemView.findViewById(R.id.imageView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            entityName = itemView.findViewById(R.id.entityName);
            requestedAt = itemView.findViewById(R.id.requestedAt);
            entityType = itemView.findViewById(R.id.entityType);
        }
    }

    private void loadJobDetails(final Holder holder, final String jobId) {
        MyFirebaseDatabase.COMPANY_POSTED_JOBS_REFERENCE.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        JobModel jobModel = dataSnapshot.getValue(JobModel.class);
                        if (jobModel != null) {
                            holder.jobTitle.setText(jobModel.getJobTitle());
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

    private void loadUserDetails(final Holder holder, final String userId) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {
                            holder.entityType.setText(CommonFunctionsClass.getGender(userProfileModel.getUserGender()));
                            holder.entityName.setText(userProfileModel.getUserFirstName());
                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("null") && !userProfileModel.getUserImage().equals(""))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(holder.imageView);
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

    private void loadCompanyDetails(final Holder holder, final String companyId) {
        MyFirebaseDatabase.COMPANY_PROFILE_REFERENCE.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CompanyProfileModel companyProfileModel = dataSnapshot.getValue(CompanyProfileModel.class);
                        if (companyProfileModel != null) {
                            holder.entityType.setText(companyProfileModel.getCompanyType());
                            holder.entityName.setText(companyProfileModel.getCompanyName());
                            if (companyProfileModel.getImage() != null && !companyProfileModel.getImage().equals("null") && !companyProfileModel.getImage().equals(""))
                                Picasso.get()
                                        .load(companyProfileModel.getImage())
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .centerInside().fit()
                                        .into(holder.imageView);
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

}
