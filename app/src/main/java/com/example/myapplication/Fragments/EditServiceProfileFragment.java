package com.example.myapplication.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Image;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EditServiceProfileFragment extends Fragment {
    // request code to pick image
    private static final int GALLERY_ACCESS_REQUEST_CODE = 202;

    private Button btnSave;
    private TextView editEmailView;
    private ImageView userImage;
    private ImageButton addImage;

    public Uri imageUri;
    public ArrayList<Uri> imageUris = new ArrayList<>();

    private String email, profilePic;

    //Firebase
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_service_profile, container, false);
        initView(view);

        getDataFromFirebase();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image from gallery
                pickImageIntent();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Image.getInstance().getImageUris().isEmpty() || profilePic != null) {
                    if(Image.getInstance().getImageUris().isEmpty()){
                        Toast.makeText(getContext(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Updating Profile");
                        progressDialog.show();
                        addToFirebase(Image.getInstance().getImageUris().get(0), new EditServiceCallback() {
                            @Override
                            public void success() {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(getFragmentManager() != null){
                        getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new MyServicesFragment()).commit();
                    }
                }else{
                    Toast.makeText(getContext(), "Add an image first", Toast.LENGTH_SHORT).show();
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
        galleryIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(galleryIntent, GALLERY_ACCESS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_ACCESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                //multiple images selected
                Toast.makeText(getContext(), "Please Select a Single Image", Toast.LENGTH_SHORT).show();
            } else {
                imageUri = data.getData();
                if(imageUri != null && getContext() != null){
                    Glide.with(getContext()).load(imageUri)
                            .thumbnail(Glide.with(getContext()).load(R.drawable.ic_user))
                            .into(userImage);
                    imageUris.add(imageUri);
                }

                if(!imageUris.isEmpty()){
                    Image.getInstance().setImageUris(imageUris);
                }
            }
        }
    }

    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && auth.getUid() != null){
                    email = (String) snapshot.child("Users").child(auth.getUid()).child("email").getValue();
                    profilePic = (String) snapshot.child("Users").child(auth.getUid()).child("img").getValue();
                    editEmailView.setText(email);
                    if(profilePic != null && getContext() != null){
                        Glide.with(getContext()).load(profilePic)
                                .thumbnail(Glide.with(getContext()).load(R.drawable.ic_user))
                                .into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToFirebase(Uri uri, EditServiceCallback editCallback) {
        StorageReference fileRef = null;

        fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        StorageReference finalFileRef = fileRef;
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finalFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri img) {
                        String url = String.valueOf(img);
                        if(auth.getUid() != null)
                            databaseReference.child("Users").child(auth.getUid()).child("img").setValue(url);
                        editCallback.success();
                    }
                });
            }
        });
        Image.getInstance().getImageUris().clear();
    }

    private String getFileExtension(Uri uri){
        if (getActivity() != null){
            ContentResolver cr = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
        return "null";
    }

    private void initView(View view){
        btnSave = view.findViewById(R.id.btnServiceSave);
        userImage = view.findViewById(R.id.editUserImage);
        addImage = view.findViewById(R.id.addServiceImageProfile);
        editEmailView = view.findViewById(R.id.editEmailView);
    }

    public interface EditServiceCallback{
        void success();
    }
}
