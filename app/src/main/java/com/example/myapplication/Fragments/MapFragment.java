 package com.example.myapplication.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.jetbrains.annotations.NotNull;

import java.util.List;
//import com.mapbox.android.core.location.LocationEngine;
//import com.mapbox.android.core.location.LocationEngineCallback;
//import com.mapbox.android.core.location.LocationEngineProvider;
//import com.mapbox.android.core.location.LocationEngineResult;
//import com.mapbox.android.core.permissions.PermissionsListener;
//import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.location.LocationComponent;
//import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
//import com.mapbox.mapboxsdk.location.modes.CameraMode;
//import com.mapbox.mapboxsdk.location.modes.RenderMode;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.maps.SupportMapFragment;


public class MapFragment extends Fragment implements PermissionsListener, LocationListener {

    //  Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final String TAG = "MapFragment";

    //  Constants
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST = 9002;
    private static final double DEFAULT_ZOOM = 13.0;
    //map
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private SupportMapFragment mapFragment;
    private Location originLocation;
    private LocationManager locationManager;
    private LocationEngine locationEngine;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // configure mapbox token
        Mapbox.getInstance(requireContext(), getString(R.string.access_token));

        initView(view);

        // create support fragment
        if(savedInstanceState == null) {
            // Create fragment
            final FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            // Build a Mapbox map
            MapboxMapOptions options = MapboxMapOptions.createFromAttributes(requireContext(), null);
            options.camera(new CameraPosition.Builder()
                    .target(new LatLng(27.700769, 85.300140))
                    .zoom(13.0)
                    .build());

            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(options);

            // Add map fragment to parent container
            transaction.add(R.id.mapFrameLayout, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else
            mapFragment = (SupportMapFragment) getParentFragmentManager().findFragmentByTag("com.mapbox.map");
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                    MapFragment.this.mapboxMap = mapboxMap;
                    mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            enableLocationComponent(style);
                        }
                    });
                }
            });
        }

        return view;
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted((requireContext()))) {
            // Get an instance of the LocationComponent.
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate the LocationComponent
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle).build());

            // Enable the LocationComponent so that it's actually visible on the map
            locationComponent.setLocationComponentEnabled(true);

            // Set the LocationComponent's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the LocationComponent's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);

            locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext());
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location location = result.getLastLocation();
                    if (location != null) {
                        originLocation = location;
                        setCameraLocation(location);
                    }
                    locationManager = (LocationManager) requireActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && location != null){
                        //starting point when gps is not turned on
                        setCameraLocation(location);
                    } else {
                        //set listener to get current location
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5f, MapFragment.this);
                    }
                }

                @Override
                public void onFailure(@NonNull @NotNull Exception exception) {
                    exception.printStackTrace();
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    private void setCameraLocation(Location location) {
        System.out.println("MyLocation " + location.getLatitude() + " " + location.getLongitude());
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 13.0));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // need to change to better explanation
        Toast.makeText(getContext(), "listen man, we seriously need this permission. So please cooperate", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getContext(), "damn, permission denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        mapboxMap.getStyle(new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull @NotNull Style style) {
//                enableLocationComponent(style);
//            }
//        });
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(requireContext(), "location turned on", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(requireContext(), "location turned off", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(requireContext(), "status changed", Toast.LENGTH_SHORT).show();
    }

    private void initView(View view) {
    }
}
