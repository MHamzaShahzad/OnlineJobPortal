package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.Constants;
import com.example.onlinejobportal.R;
import com.example.onlinejobportal.UserProfileDescriptionActivity;
import com.example.onlinejobportal.models.UserProfileModel;

import java.util.List;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.Holder> {

    private static final String TAG = AdapterAllUsers.class.getName();
    private Context context;
    private List<UserProfileModel> usersList;

    public AdapterAllUsers(Context context, List<UserProfileModel> usersList) {
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

        final UserProfileModel userProfileModel = usersList.get(holder.getAdapterPosition());
        holder.userName.setText(userProfileModel.getUserFirstName());
        holder.userSkills.setText(userProfileModel.getUserSkills());
        holder.userCurrentJob.setText(userProfileModel.getUserCurrentJob());
        holder.userCity.setText(userProfileModel.getUserCity());

       holder.userCard.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context, UserProfileDescriptionActivity.class);
               intent.putExtra(Constants.USER_OBJECT, userProfileModel);
               context.startActivity(intent);
           }
       });

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView userCard;
        TextView userName, userSkills, userCurrentJob, userCity;

        public Holder(@NonNull View itemView) {
            super(itemView);

            userCard = itemView.findViewById(R.id.userCard);
            userName = itemView.findViewById(R.id.userName);
            userSkills = itemView.findViewById(R.id.userSkills);
            userCurrentJob = itemView.findViewById(R.id.userCurrentJob);
            userCity = itemView.findViewById(R.id.userCity);

        }
    }
}
