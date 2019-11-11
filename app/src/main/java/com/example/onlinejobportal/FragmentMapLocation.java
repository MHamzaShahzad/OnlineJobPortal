package com.example.onlinejobportal;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.onlinejobportal.interfaces.FragmentInteractionListenerInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class FragmentMapLocation extends Fragment implements OnMapReadyCallback {

    private static final String TAG = FragmentMapLocation.class.getName();
    Context context;
    View view;
    TextView text_location_address;
    ImageView btn_submit_location;
    RelativeLayout locationAddressLayout;

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;

    private GoogleMap mMap;
    private LatLng latLng;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private boolean shouldMoveToCurrentLocation = true;

    private FragmentInteractionListenerInterface mListener;

    public FragmentMapLocation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        /*if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_SELECT_LOCATION);*/
        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_map_location, container, false);


            initMap();

        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: ");
        mMap = googleMap;

    }


    private void initMap() {
        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement FragmentInteractionListenerInterface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_SELECT_LOCATION);*/
    }

}
