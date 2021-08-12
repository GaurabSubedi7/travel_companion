package com.example.myapplication.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.Adapters.ImageAdapter;
import com.example.myapplication.Models.Image;
import com.example.myapplication.Models.ServicePost;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class AddServicesFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Button btnAddService, btnCancelService;
    private RecyclerView serviceNewPostRecView;
    private EditText descriptionEditText, priceEditText, serviceNameEditText;
    private Spinner serviceLocationSpinner, serviceTypeSpinner;
    private ImageView addImage;

    private String serviceName, description, serviceType, serviceLocation;
    private int servicePrice;

    // request code to pick image
    private static final int GALLERY_ACCESS_REQUEST_CODE = 201;

    //Calendar
    private Calendar calendar = Calendar.getInstance();

    //image uris list
    private ArrayList<Uri> imageUris;
    private ServicePost servicePost;

    //Progress Dialog box
    private ProgressDialog progressDialog;

    //Firebase
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("services");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_services, container, false);
        initView(view);
        if(getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationService);
            bottomNavigationView.setVisibility(View.GONE);
        }
        imageUris = new ArrayList<>();

        btnCancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager()!= null) {
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().popBackStack();
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new ServiceHomeFragment());
                    }
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image from gallery
                pickImageIntent();
            }
        });

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceName = serviceNameEditText.getText().toString();
                servicePrice = Integer.parseInt(priceEditText.getText().toString());
                serviceType = serviceTypeSpinner.getSelectedItem().toString();
                serviceLocation = serviceLocationSpinner.getSelectedItem().toString();
                description = descriptionEditText.getText().toString();

                if(serviceName.isEmpty() || priceEditText.getText().toString().isEmpty()
                    || description.isEmpty()){
                    Toast.makeText(getContext(), "Fields Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(serviceType.isEmpty()){
                        serviceType = "unknown";
                    }

                    if(serviceLocation.isEmpty()){
                        serviceLocation = "unknown";
                    }

                    if(Image.getInstance().getImageUris() != null && !Image.getInstance().getImageUris().isEmpty()) {
                        //start progressDialog
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Have Patience, Your Service Is Being Uploaded");
                        progressDialog.show();
                        addToFirebase(Image.getInstance().getImageUris(), serviceName, serviceType, description,
                            servicePrice, serviceLocation, new ServiceCallback() {
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
                        getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new ServiceHomeFragment()).commit();
                    }
                }
            }
        });

        return view;
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
            serviceNewPostRecView.setAdapter(adapter);
            serviceNewPostRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            Collections.reverse(Image.getInstance().getImageUris());
            adapter.setImageUris(Image.getInstance().getImageUris());
        }
    }

    //add to firebase
    private void addToFirebase(ArrayList<Uri> imgUris, String name, String type, String desc,
                               int price, String loc, ServiceCallback serviceCallback){
        StorageReference fileRef = null;

        //add user caption and upload date to firebase
        addData(name, type, desc, price, loc);
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
                            serviceCallback.success();
                        }
                    });
                }
            });
        }
        Image.getInstance().getImageUris().clear();

    }

    private void addData(String name, String type, String desc, int price, String loc){
        String serviceId = databaseReference.push().getKey();
        if (auth.getUid() != null && serviceId != null){
            //add desc and currentTime to userPost object
            Date date = calendar.getTime();
            @SuppressLint("SimpleDateFormat")
            String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
            servicePost = new ServicePost((String) auth.getUid(), name, currentTime,
                    loc, type, desc, price);

            databaseReference.child("Services").child(serviceId).setValue(servicePost);
        }else{
            Toast.makeText(getActivity(),"Something went wrong, Try Again",Toast.LENGTH_SHORT).show();
        }
        //set userPostId
        servicePost.setServiceId(serviceId);
    }

    private void addImageToFirebase(String url){
        HashMap<String, String> images = new HashMap<>();
        images.put("img", url);
        if(auth.getUid() != null) {
            String imgId = databaseReference.push().getKey();
            if(imgId != null) {
                databaseReference.child("Services").child(servicePost.getServiceId()).child("Images")
                        .child(imgId).setValue(images);
            }
        }
    }

    private String getFileExtension(Uri uri){
        if (getActivity() != null){
            ContentResolver cr = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
        return "null";
    }

    private void initView(View view) {
        btnAddService = view.findViewById(R.id.btnAddNewService);
        btnCancelService = view.findViewById(R.id.btnCancelService);
        serviceLocationSpinner = view.findViewById(R.id.serviceLocationSpinner);
        serviceTypeSpinner = view.findViewById(R.id.serviceTypeSpinner);
        serviceNameEditText = view.findViewById(R.id.serviceNameEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        addImage = view.findViewById(R.id.addImage);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        serviceNewPostRecView = view.findViewById(R.id.serviceNewPhotoRecView);
    }

    private interface ServiceCallback{
        void success();
    }
}