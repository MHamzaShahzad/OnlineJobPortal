package com.example.onlinejobportal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.models.UserProfile;

import java.util.List;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.Holder> {

    private static final String TAG = AdapterAllUsers.class.getName();
    private Context context;
    private List<UserProfile> usersList;

    public AdapterAllUsers(Context context, List<UserProfile> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public AdapterAllUsers.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_card_design , null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllUsers.Holder holder, int position) {

        Log.e(TAG, "onBindViewHolder: " + usersList.get(holder.getAdapterPosition()).getUserFirstName() );

        UserProfile userProfile = usersList.get(holder.getAdapterPosition());
        holder.userName.setText(userProfile.getUserFirstName());
        holder.userSkills.setText(userProfile.getUserSkills());
        holder.userCurrentJob.setText(userProfile.getUserCurrentJob());
        holder.userCity.setText(userProfile.getUserCity());

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView userName, userSkills, userCurrentJob, userCity;

        public Holder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userSkills = itemView.findViewById(R.id.userSkills);
            userCurrentJob = itemView.findViewById(R.id.userCurrentJob);
            userCity = itemView.findViewById(R.id.userCity);

        }
    }
}
