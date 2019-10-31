package com.example.onlinejobportal;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllUsers extends Fragment {


    private static final String TAG = FragmentAllUsers.class.getName();
    private Context context;
    private View view;

    private RecyclerView recycler_all_users;
    private AdapterAllUsers adapterAllUsers;
    private List<UserProfile> list;

    private ValueEventListener usersListValueEventListener;

    public FragmentAllUsers() {
        // Required empty public constructor
        list = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        adapterAllUsers = new AdapterAllUsers(context, list);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_users, container, false);

            recycler_all_users = view.findViewById(R.id.recycler_all_users);
            recycler_all_users.setHasFixedSize(true);
            recycler_all_users.setLayoutManager(new LinearLayoutManager(context));
            recycler_all_users.setAdapter(adapterAllUsers);


            getUsersListFromFirebaseDatabase();
        }
        return view;
    }


    private void getUsersListFromFirebaseDatabase() {

        usersListValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        for (DataSnapshot snapshot : snapshots){
                            UserProfile userProfile = snapshot.getValue(UserProfile.class);
                            list.add(userProfile);
                        }
                        adapterAllUsers.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabase.USER_PROFILE_REFERENCE.addValueEventListener(usersListValueEventListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersListValueEventListener != null)
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.removeEventListener(usersListValueEventListener);
    }
}
