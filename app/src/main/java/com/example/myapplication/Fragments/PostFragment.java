package com.example.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.example.myapplication.RegistrationActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

public class PostFragment extends DialogFragment {

    // request code to pick image
    private static final int GALLERY_ACCESS_REQUEST_CODE = 201;
    //image uris list
    private ArrayList<Uri> imageUris;

    //UserPost objects
    private UserPost userPost;

    private Button post, addImage;
    private EditText postEditText;

    //Calendar
    private Calendar calendar = Calendar.getInstance();

    private ArrayList<String> stringUris = new ArrayList<>();

    //Loading prompt class
    private ProgressDialog progressDialog;

    //Firebase Classes
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_post, null);

        initView(view);
        imageUris = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Share Your Experience");

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image from gallery
                pickImageIntent();
            }
        });

        //runs after user is ready to post
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writePost = postEditText.getText().toString();
                addToFirebase(writePost, imageUris);
                dismiss();
            }
        });
        return builder.create();
    }

    private void addToUser(){
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") //just a line to remove yellow thingy (erasable)
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        userPost = new UserPost(postEditText.getText().toString(), currentTime);
    }

    //get image from gallery
    @SuppressLint("IntentReset") //just to remove that annoying yellow thingy (erasable)
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
                    //get imageUri at dat fakin index
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri); //adding to da list
                }
                Toast.makeText(getActivity(), "multiple image selected", Toast.LENGTH_SHORT).show();
            }else{
                //selected just one image (coz you suck and don't have many photos)
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                Toast.makeText(getActivity(), "single image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //addToFirebase function
    private void addToFirebase(String writePost, ArrayList<Uri> imageUris) {
        //add images to Storage
        StorageReference fileRef = null;
        addData();
        for (Uri imageUri : imageUris) {
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
                        }
                    });
                }
            });
        }
    }

    private void addImageToFirebase(String url){
        HashMap<String, String> images = new HashMap<>();
        images.put("img", url);
        if(auth.getUid() != null) {
            String imgId = databaseReference.push().getKey();
            if(imgId != null) {
                databaseReference.child("Users").child(auth.getUid()).child("Posts").child(userPost.getPostId()).child("Images")
                        .child(imgId).setValue(images);
            }
        }
    }

    private void addData(){
        String postId = databaseReference.push().getKey();
        addToUser();
        if (auth.getUid() != null && postId != null){
            databaseReference.child("Users").child(auth.getUid()).child("Posts").child(postId).setValue(userPost);
            Toast.makeText(getActivity(),"Phew That was close, but ur post has been uploaded successfully)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Something not happen as it should be, gibb try again",Toast.LENGTH_SHORT).show();
        }
        userPost.setPostId(postId);
    }

//    This will return the file extension to  store in Fire Storage
    private String getFileExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void initView(View view){
        post = view.findViewById(R.id.post);
        addImage = view.findViewById(R.id.addImage);
        postEditText = view.findViewById(R.id.postEditText);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Keep Kalmm... we posting ur memories");
        progressDialog.setMessage("mean while, how are things ?? All good ?");
    }
}

/* TODO : Limit user's post character count
          Sanitize code (FAK DIS)
          Check all the possible null values and fix it
          Timeout pani rakhne (mile samma)
 */
