package com.example.myapplication.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;


public class MapFragment extends Fragment {

    //  Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    //  Variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    private static final String TAG = "MapFragment";

    //  Constants
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST = 9002;
    private static final float DEFAULT_ZOOM = 15f;

    //Map
    private MapView map;

    public MapFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);
        getLocationPermission();
        if(mLocationPermissionGranted){
            Log.d(TAG, "onCreateView: Location Permission Granted");
            Toast.makeText(getActivity(), "Location Permission Granted", Toast.LENGTH_SHORT).show();

//            Osmdroid configuration
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

//          Map View Configurations
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
        }else{
            getLocationPermission();
        }
        return view;
    }

    private void initView(View view){
        map = view.findViewById(R.id.map);
    }







    //    get location permissions
    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
//            Initialize Map here

        } else {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST);
        }
    }

    //    Map request result
//    Map initialization is called here if permission granted
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: LOG 5 Request Permission called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: LOG 6 Permission failed");
                            mLocationPermissionGranted = false;
                            return;
                        }
                        Log.d(TAG, "onRequestPermissionsResult: LOG 7 Permission granted");
                        mLocationPermissionGranted = true;
//                      Initialize our map here

                    }
                }
            }
        }
    }
}
