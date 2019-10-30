package com.example.onlinejobportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.models.UserProfile;

import java.util.List;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.Holder> {

    List<UserProfile> usersList;

    @NonNull
    @Override
    public AdapterAllUsers.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_card_design , null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllUsers.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
