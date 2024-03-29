package com.example.onlinejobportal.company;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

// This is a company profile description fragment

public class FragmentCompanyProfileDescription extends Fragment {

    private static final String TAG = FragmentCompanyProfileDescription.class.getName();
    private Context context;
    private View view;
    private CircleImageView companyProfileImage;
    private TextView companyName, companyType, companyBusiness, companyPhone;
    private FirebaseUser firebaseUser;

    private FragmentInteractionListenerInterface mListener;


    public FragmentCompanyProfileDescription() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(this.getTag());
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_company_profile_description, container, false);
            initLayoutWidgets();
        }
        return view;
    }

    private void initLayoutWidgets(){

        companyProfileImage = view.findViewById(R.id.companyProfileImage);
        companyName = view.findViewById(R.id.companyName);
        companyType = view.findViewById(R.id.companyType);
        companyBusiness = view.findViewById(R.id.companyBusiness);
        companyPhone = view.findViewById(R.id.companyPhone);

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
