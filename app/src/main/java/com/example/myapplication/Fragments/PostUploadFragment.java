package com.example.myapplication.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ImageAdapter;
import com.example.myapplication.Models.Image;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostUploadFragment extends Fragment {
    // request code to pick image
    private static final int GALLERY_ACCESS_REQUEST_CODE = 201;
    //  Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST = 9002;

    private Boolean mLocationPermissionGranted = false;

    // request code to capture image
    private static final int CAMERA_ACCESS_REQUEST_CODE = 101;
    private static final String TAG = "PostUploadFragment";
    //image uris list
    private ArrayList<Uri> imageUris;

    //UserPost objects
    private UserPost userPost;

    private Button btnPost, btnCancel, currentLocationBtn;
    private ImageButton addImage;
    private Spinner tripLocationSpinner;
    private EditText captionEditText, locationEditText;
    private RecyclerView newPhotoRecView;
    private BottomNavigationView bottomNavigationView;
    private String tripLocation, specificLocation, title = null;
    private double latitude = 0.01, longitude = 0.01;
    
    //Calendar
    private Calendar calendar = Calendar.getInstance();

    //Progress Dialog box
    private ProgressDialog progressDialog;

    //Firebase
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_upload, container, false);
        initView(view);
        if(getActivity() != null){
            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
            bottomNavigationView.setVisibility(View.GONE);
        }
        imageUris = new ArrayList<>();

        if(getArguments() != null){
            title = getArguments().getString("title");
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
            locationEditText.setText(title);
            specificLocation = locationEditText.getText().toString();
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image from gallery
                pickImageIntent();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager()!= null) {
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().popBackStack();
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new HomeFragment());
                    }
                }
            }
        });

        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
                if(mLocationPermissionGranted){
                    getDeviceLocation(new LocationCallback() {
                        @Override
                        public void onCallback(LatLng latlng) {
                            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = gcd.getFromLocation(latlng.latitude, latlng.longitude, 1);
                                if (addresses.size() > 0) {
                                    title = addresses.get(0).getLocality();
                                    latitude = latlng.latitude;
                                    longitude = latlng.longitude;
                                    locationEditText.setText(title);
                                    specificLocation = locationEditText.getText().toString();
                                }
                            } catch (IOException e) {
                                System.out.println("Exception occurred : " + e);
                            }
                        }
                    });
                }else {
                    getLocationPermission();
                }
            }
        });

        //runs after user is ready to post
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripLocation = tripLocationSpinner.getSelectedItem().toString();
                if(tripLocation.equalsIgnoreCase("Choose Trip Location")){
                    tripLocation = "unknown";
                }

                if(locationEditText.getText().toString().isEmpty() || title == null){
                    specificLocation = "unknown";
                }else{
                    specificLocation = locationEditText.getText().toString();
                }

                if(Image.getInstance().getImageUris() != null && !Image.getInstance().getImageUris().isEmpty()) {
                    //start progressDialog
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Have Patience, Your Post Is Being Uploaded");
                    progressDialog.show();
                    addToFirebase(Image.getInstance().getImageUris(), tripLocation, specificLocation, latitude, longitude,
                            new MyCallback() {
                        @Override
                        public void success() {
                            progressDialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "You Must Add An Image(s) First", Toast.LENGTH_SHORT).show();
                }
                if(getFragmentManager() != null){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new HomeFragment()).commit();
                }
            }
        });

        locationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if(fm != null){
                    FragmentTransaction ft = fm.beginTransaction();
                    MapFragment mapFragment = new MapFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("identifier", "new post");
                    mapFragment.setArguments(bundle);
                    ft.replace(R.id.FrameContainer, mapFragment).addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return view;
    }

    private void addToUser(String tripLoc, String specificLoc, double lat,
                           double lon){
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat")
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        userPost = new UserPost((String) auth.getUid(), captionEditText.getText().toString(), currentTime,
                tripLoc, specificLoc, lat, lon);
    }

    //get image from gallery
    @SuppressLint("IntentReset")
    private void pickImageIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), GALLERY_ACCESS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_ACCESS_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            if(data.getClipData() != null){
                //multiple images selected
                int imgCount = data.getClipData().getItemCount(); //number of images selected
                for (int i = 0; i < imgCount; i++){
                    //get imageUri at dat faking index
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri); //adding to da list
                }
            }else{
                //selected just one image (coz you suck and don't have many photos)
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }

            Image.getInstance().setImageUris(imageUris);

            //load image on the post dialog
            ImageAdapter adapter = new ImageAdapter(getContext());
            newPhotoRecView.setAdapter(adapter);
            newPhotoRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            Collections.reverse(Image.getInstance().getImageUris());
            adapter.setImageUris(Image.getInstance().getImageUris());
        }
    }

    //addToFirebase function
    private void addToFirebase(ArrayList<Uri> imgUris, String tripLoc, String specificLoc, double lat,
                               double lon, MyCallback myCallback) {
        StorageReference fileRef = null;

        //add user caption and upload date to firebase
        addData(tripLoc, specificLoc, lat, lon);
        //add images to storage
        for (Uri imageUri : imgUris) {
            fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageReference finalFileRef = fileRef;
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    finalFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            addImageToFirebase(url);
                            myCallback.success();
                        }
                    });
                }
            });
        }
        Image.getInstance().getImageUris().clear();
    }

    //add image of the post
    private void addImageToFirebase(String url){
        HashMap<String, String> images = new HashMap<>();
        images.put("img", url);
        if(auth.getUid() != null) {
            String imgId = databaseReference.push().getKey();
            if(imgId != null) {
                databaseReference.child("Posts").child(userPost.getPostId()).child("Images")
                        .child(imgId).setValue(images);
            }
        }
    }

    //add other data except image
    private void addData(String tripLoc, String specificLoc, double lat,
                         double lon){
        String postId = databaseReference.push().getKey();
        if (auth.getUid() != null && postId != null){
            //add caption and currentTime to userPost object
            addToUser(tripLoc, specificLoc, lat, lon);
            databaseReference.child("Posts").child(postId).setValue(userPost);
        }else{
            Toast.makeText(getActivity(),"Something went wrong, Try Again",Toast.LENGTH_SHORT).show();
        }
        //set userPostId
        userPost.setPostId(postId);
    }

    //This will return the file extension to  store in Fire Storage
    private String getFileExtension(Uri uri){
        if (getActivity() != null){
            ContentResolver cr = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
        return "null";
    }

    private void getDeviceLocation(LocationCallback myCallback) {
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
                                LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                myCallback.onCallback(latlng);
                            } else {
                                Toast.makeText(getActivity(), "Turn on device location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception : " + e.getMessage());
        }
    }

    //    get location permissions
    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST);
        }
    }

    private void initView(View view){
        btnPost = view.findViewById(R.id.btnPost);
        btnCancel = view.findViewById(R.id.btnCancel);
        addImage = view.findViewById(R.id.addImage);
        currentLocationBtn = view.findViewById(R.id.currentLocationBtn);
        tripLocationSpinner = view.findViewById(R.id.tripLocationSpinner);
        captionEditText = view.findViewById(R.id.captionEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        newPhotoRecView = view.findViewById(R.id.newPhotoRecView);
    }

    private interface MyCallback{
        void success();
    }

    private interface LocationCallback{
        void onCallback(LatLng latLng);
    }
}
