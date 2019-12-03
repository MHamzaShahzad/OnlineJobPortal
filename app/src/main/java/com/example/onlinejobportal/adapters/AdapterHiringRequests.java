package com.example.onlinejobportal.adapters;

import android.content.Context;
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

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.common.FragmentHiringReqDescription;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.CompanyProfileModel;
import com.example.onlinejobportal.models.HiringRequest;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterHiringRequests extends RecyclerView.Adapter<AdapterHiringRequests.Holder> {

    private Context context;
    private List<HiringRequest> hiringRequestsList;
    private boolean isSeenByCompany;

    public AdapterHiringRequests(Context context, List<HiringRequest> hiringRequestsList, boolean isSeenByCompany) {
        this.context = context;
        this.hiringRequestsList = hiringRequestsList;
        this.isSeenByCompany = isSeenByCompany;
    }

    @NonNull
    @Override
    public AdapterHiringRequests.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hirinig_requests_card, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterHiringRequests.Holder holder, int position) {

        HiringRequest hiringRequest = hiringRequestsList.get(holder.getAdapterPosition());

        switch (hiringRequest.getHireStatus()) {
            case Constants.REQUEST_STATUS_PENDING:
                holder.requestedAt.setText(hiringRequest.getHireRequestedAt());
                break;
            case Constants.REQUEST_STATUS_ACCEPTED:
                holder.requestedAt.setText(hiringRequest.getAcceptedByUserAt());
                break;
            case Constants.REQUEST_STATUS_REJECTED:
                holder.requestedAt.setText(hiringRequest.getRejectedByUserAt());
                break;
            case Constants.REQUEST_STATUS_HIRED:
                holder.requestedAt.setText(hiringRequest.getHiringRejectedAt());
                break;
            case Constants.REQUEST_STATUS_NOT_HIRED:
                holder.requestedAt.setText(hiringRequest.getHiringAcceptedAt());
                break;
            default:
        }

        if (isSeenByCompany)
            loadUserDetails(holder, hiringRequest.getHiringUserId());
        else
            loadCompanyDetails(holder, hiringRequest.getHiredByCompanyId());

        holder.cardHiringRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_HIRING_SEEN_BY_COMPANY, isSeenByCompany);
                bundle.putSerializable(Constants.HIRING_REQ_OBJ, hiringRequestsList.get(holder.getAdapterPosition()));
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, FragmentHiringReqDescription.getInstance(bundle)).addToBackStack(null).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return hiringRequestsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView cardHiringRequest;
        private ImageView imageView;
        private TextView entityName, requestedAt, entityType, entityAddress;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardHiringRequest = itemView.findViewById(R.id.cardHiringRequest);
            imageView = itemView.findViewById(R.id.imageView);
            entityName = itemView.findViewById(R.id.entityName);
            requestedAt = itemView.findViewById(R.id.requestedAt);
            entityType = itemView.findViewById(R.id.entityType);
            entityAddress = itemView.findViewById(R.id.entityAddress);

        }
    }

    private void loadUserDetails(final Holder holder, final String userId) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {
                            holder.entityAddress.setText(userProfileModel.getUserCity() + ", " + userProfileModel.getUserCountry());
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
                            holder.entityAddress.setText(companyProfileModel.getCompanyCity() + ", " + companyProfileModel.getCompanyCountry());
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
