package com.example.myapplication.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment{

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
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    GeoPoint currentLocation = new GeoPoint(location);
                    Log.d(TAG, "onLocationChanged: Current Location " + currentLocation);
                }
            };


//          Map View Configurations
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            IMapController mapController = map.getController();
            mapController.setZoom(DEFAULT_ZOOM);

            GeoPoint startPoint = new GeoPoint(27.700769, 85.300140);
                        mapController.setCenter(startPoint);



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
