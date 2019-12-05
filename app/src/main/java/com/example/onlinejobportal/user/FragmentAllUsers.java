package com.example.onlinejobportal.user;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.adapters.AdapterAllUsers;
import com.example.onlinejobportal.common.Constants;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.example.onlinejobportal.models.UserProfileModel;
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
    private List<UserProfileModel> list, tempList;

    private SwitchCompat switchTrustedOrNot;
    private TextView textUserTypes;

    private ValueEventListener usersListValueEventListener;
    private FragmentInteractionListenerInterface mListener;


    public FragmentAllUsers() {
        // Required empty public constructor
        list = new ArrayList<>();
        tempList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        adapterAllUsers = new AdapterAllUsers(context, tempList);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_users, container, false);

            switchTrustedOrNot = view.findViewById(R.id.switchTrustedOrNot);
            textUserTypes = view.findViewById(R.id.textUserTypes);

            recycler_all_users = view.findViewById(R.id.recycler_all_users);
            recycler_all_users.setHasFixedSize(true);
            recycler_all_users.setLayoutManager(new LinearLayoutManager(context));
            recycler_all_users.setAdapter(adapterAllUsers);


            getUsersListFromFirebaseDatabase();
            setSwitchTrustedOrNot();
        }
        return view;
    }

    private void setSwitchTrustedOrNot(){
        switchTrustedOrNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getUsers(b);
                if (b)
                    textUserTypes.setText(Constants.STRING_USER_TRUSTED);
                else
                    textUserTypes.setText("All Users");
            }
        });
    }

    private void getUsersListFromFirebaseDatabase() {

        usersListValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        for (DataSnapshot snapshot : snapshots) {
                            UserProfileModel userProfileModel = snapshot.getValue(UserProfileModel.class);
                            list.add(userProfileModel);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getUsers(switchTrustedOrNot.isChecked());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabase.USER_PROFILE_REFERENCE.addValueEventListener(usersListValueEventListener);

    }

    private void getUsers(boolean showOnlyTrusted){

        if (showOnlyTrusted){
            if (list.size() > 0){
                tempList.clear();
                for (UserProfileModel userProfileModel : list){
                    if (userProfileModel.getUserStatus() != null && userProfileModel.getUserStatus().equals(Constants.USER_TRUSTED))
                        tempList.add(userProfileModel);
                }
                adapterAllUsers.notifyDataSetChanged();
            }
        }else {
            tempList.clear();
            tempList.addAll(list);
            adapterAllUsers.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersListValueEventListener != null)
            MyFirebaseDatabase.USER_PROFILE_REFERENCE.removeEventListener(usersListValueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onFragmentInteraction(this.getTag());
        }
        mListener = null;
    }


}
