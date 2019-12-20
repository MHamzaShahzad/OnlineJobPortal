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

import com.example.onlinejobportal.common.CommonFunctionsClass;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.admin.FragmentTrustRequestDescription;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.LookForTrusted;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTrustedUsers extends RecyclerView.Adapter<AdapterTrustedUsers.Holder> {

    private Context context;
    private List<LookForTrusted> list;

    public AdapterTrustedUsers(Context context, List<LookForTrusted> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trusted_users_card_design, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final LookForTrusted lookForTrusted = list.get(holder.getAdapterPosition());

        holder.requestedAt.setText(lookForTrusted.getRequestedAt());
        holder.cardLookForTrusted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTrustRequestDescription description = new FragmentTrustRequestDescription();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.LOOK_FOR_TRUSTED_OBJECT, lookForTrusted);
                description.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_admin, description, Constants.TITLE_APPROVAL_REQUEST_DESCRIPTION).addToBackStack(Constants.TITLE_APPROVAL_REQUEST_DESCRIPTION).commit();
            }
        });

        getUserData(lookForTrusted.getUserId(), holder);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView cardLookForTrusted;
        private CircleImageView userProfileImage;
        private TextView userName, userEducation, userGender, userLocation, requestedAt;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardLookForTrusted = itemView.findViewById(R.id.cardLookForTrusted);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userEducation = itemView.findViewById(R.id.userEducation);
            userGender = itemView.findViewById(R.id.userGender);
            userLocation = itemView.findViewById(R.id.userLocation);
            requestedAt = itemView.findViewById(R.id.requestedAt);

        }
    }

    private void getUserData(final String userId, final Holder holder) {
        MyFirebaseDatabase.USER_PROFILE_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                        if (userProfileModel != null) {

                            if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("null") && !userProfileModel.getUserImage().equals(""))
                                Picasso.get()
                                        .load(userProfileModel.getUserImage())
                                        .error(R.drawable.useravatar)
                                        .placeholder(R.drawable.useravatar)
                                        .centerInside().fit()
                                        .into(holder.userProfileImage);

                            holder.userName.setText(userProfileModel.getUserFirstName() + " " + userProfileModel.getUserLastName());
                            holder.userEducation.setText(userProfileModel.getUserEduction());
                            holder.userLocation.setText(userProfileModel.getUserCity() + ", " + userProfileModel.getUserCountry());
                            holder.userGender.setText(CommonFunctionsClass.getGender(userProfileModel.getUserGender()));
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
