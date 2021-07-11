package com.example.myapplication.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    //  Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    //UI elements
    private ImageView mGps;
    private EditText inputSearch;
    private CardView searchCardView;


    //  Variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    private static final String TAG = "MapFragment";

    //  Constants
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST = 9002;
    private static final float DEFAULT_ZOOM = 15f;

    // Google Places

    public MapFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);

        if (isServicesOK()) {
//           get Location Permission
            getLocationPermission();
        }

//        Gibb try to places
        if(!Places.isInitialized()){
            String apiKey = getResources().getString(R.string.google_maps_API_key);
            // Initialize the SDK
            Places.initialize(getContext(),apiKey);
        }

        // Create a new PlacesClient instance
        placeAutoComplete();
        return view;
    }

    public void placeAutoComplete(){
        if(getFragmentManager()!=null){
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    if(place.getLatLng()!= null && place.getName()!= null){
                        moveCamera(place.getLatLng(), place.getName());
                    }else{
                        Toast.makeText(getContext(), "Something went doodooo", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred BOII: " + status);
                }
            });
        }
    }

    //    On Map Ready
    public void onMapReady(@NotNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: LOG 1 Map is Ready");
        Toast.makeText(getActivity(), "Maps is ready ;) ", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //This function places a marker in my current location
            mMap.setMyLocationEnabled(true);
            //For now disabling the reset location to current location button (we will make our custom version later)
            //if gibb try to other UI materials we can go is in mMap.getUiSettings() explore (All of them are booleans)
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);

            init();
        }
    }

    //    get Device Location
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device's current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                Log.d(TAG, "Longitude Latitude: " + currentLocation);
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "My Location");
                            } else {
                                Toast.makeText(getActivity(), "u not Turn on location on device... Turn it on and gibb try again :|", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception : " + e.getMessage());
        }

    }

    //    Move Camera from the map
    private void moveCamera(LatLng latLng, String title) {
        Log.d(TAG, "moveCamera: Moving camera to given Location coordinates : " + latLng.latitude + "::" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    Toast.makeText(getContext(), "Markerr is Clicked", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
//        hideSoftKeyboard();
    }

    //    Check for the connection with Google Services API and app compatibility
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: LOG 2 Checking Google Services Version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Objects.requireNonNull(getContext()));

        //        If connection to google API succeeded
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: LOG 3 Google API service is working smooth");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //            Problem with connectivity but can be resolved from user's end (version issue most of the time)
            Log.d(TAG, "isServicesOK: LOG 4 Error occurred but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Objects.requireNonNull(getActivity()), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Some failure of application end... cannot make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //    get location permissions
    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            initMap();
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
                        initMap();
                    }
                }
            }
        }
    }

    //    Initialize Map
    private void initMap() {
        Log.d(TAG, "initMap: LOG 8 Map initialization complete");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }


    //Master Initialization
    private void init() {
        Log.d(TAG, "init: Initializing UI elements");

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Moving Camera to Device Current Location");
                getDeviceLocation();
            }
        });
    }

    private void initView(View view){
        mGps = view.findViewById(R.id.gps);
        searchCardView = view.findViewById(R.id.searchBarCardView);

    }


}
//LOOKS CUTE MIGHT REMOVE LATER
/*
googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull @NotNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + ":" + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                        googleMap.addMarker(markerOptions);
                    }
                });
 */