package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.user.FragmentUserProfileDescription;
import com.example.onlinejobportal.models.UserProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.Holder> {

    private static final String TAG = AdapterAllUsers.class.getName();
    private Context context;
    private List<UserProfileModel> usersList;
    private FirebaseUser firebaseUser;

    public AdapterAllUsers(Context context, List<UserProfileModel> usersList) {
        this.context = context;
        this.usersList = usersList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public AdapterAllUsers.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_card_design, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllUsers.Holder holder, int position) {

        holder.verifiedIcon.setVisibility(View.INVISIBLE);
        Log.e(TAG, "onBindViewHolder: " + usersList.get(holder.getAdapterPosition()).getUserFirstName());

        final UserProfileModel userProfileModel = usersList.get(holder.getAdapterPosition());

        if (userProfileModel.getUserImage() != null && !userProfileModel.getUserImage().equals("null") && !userProfileModel.getUserImage().equals(""))
            Picasso.get()
                    .load(userProfileModel.getUserImage())
                    .error(R.drawable.useravatar)
                    .placeholder(R.drawable.useravatar)
                    .centerInside().fit()
                    .into(holder.userProfileImage);

        if (userProfileModel.getUserStatus() != null && userProfileModel.getUserStatus().equals(Constants.USER_TRUSTED))
            holder.verifiedIcon.setVisibility(View.VISIBLE);

        holder.userName.setText(userProfileModel.getUserFirstName());
        holder.userSkills.setText(userProfileModel.getUserSkills());
        holder.userCurrentJob.setText(userProfileModel.getUserCurrentJob());
        holder.userCity.setText(userProfileModel.getUserCity());

        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentUserProfileDescription fragmentUserProfileDescription = new FragmentUserProfileDescription();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_OBJECT, userProfileModel);
                fragmentUserProfileDescription.setArguments(bundle);
                if (firebaseUser == null)
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentUserProfileDescription, Constants.TITLE_USER_DESCRIPTION).addToBackStack(Constants.TITLE_USER_DESCRIPTION).commit();
                else
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, fragmentUserProfileDescription, Constants.TITLE_USER_DESCRIPTION).addToBackStack(Constants.TITLE_USER_DESCRIPTION).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView userCard;
        CircleImageView userProfileImage;
        ImageView verifiedIcon;
        TextView userName, userSkills, userCurrentJob, userCity;

        public Holder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            verifiedIcon = itemView.findViewById(R.id.verifiedIcon);
            userCard = itemView.findViewById(R.id.userCard);
            userName = itemView.findViewById(R.id.userName);
            userSkills = itemView.findViewById(R.id.userSkills);
            userCurrentJob = itemView.findViewById(R.id.userCurrentJob);
            userCity = itemView.findViewById(R.id.userCity);

        }
    }
}
