package com.example.onlinejobportal.company;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinejobportal.R;

/**
 * A simple {@link Fragment} subclass.
 */

// This is a company profile description fragment

public class FragmentCompanyProfileDescription extends Fragment {

    private static final String TAG = FragmentCompanyProfileDescription.class.getName();
    private Context context;
    private View view;

    public FragmentCompanyProfileDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_company_profile_description, container, false);

        }
        return view;
    }

}
