package com.example.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ImageAdapter;
import com.example.myapplication.Models.Image;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

public class PostFragment extends DialogFragment {

    // request code to pick image
    private static final int GALLERY_ACCESS_REQUEST_CODE = 201;
    // request code to capture image
    private static final int CAMERA_ACCESS_REQUEST_CODE = 101;
    //image uris list
    private ArrayList<Uri> imageUris;

    //UserPost objects
    private UserPost userPost;

    private Button post, addImage;
    private EditText postEditText;
    private RecyclerView newPhotoRecView;

    //Calendar
    private Calendar calendar = Calendar.getInstance();

    //Progress Dialog box
    private ProgressDialog progressDialog;

    //Firebase Classes
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts");

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
                if(Image.getInstance().getImageUris() != null && !Image.getInstance().getImageUris().isEmpty()) {
                    //start progressDialog box
//                    progressDialog = new ProgressDialog(getContext());
//                    progressDialog.setMessage("Have Patience, Your Post Is Being Uploaded");
//                    progressDialog.show();
                    //add to firebase
                    addToFirebase(Image.getInstance().getImageUris());
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "You Must Add An Image(s) First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return builder.create();
    }

    private void addToUser(){
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") //just a line to remove yellow thingy (erasable)
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        userPost = new UserPost((String) auth.getUid(), postEditText.getText().toString(), currentTime);
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
            adapter.setImageUris(Image.getInstance().getImageUris());
        }
    }

    //addToFirebase function
    private void addToFirebase(ArrayList<Uri> imgUris) {
        StorageReference fileRef = null;

        //add user caption and upload date to firebase
        addData();
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
    private void addData(){
        String postId = databaseReference.push().getKey();
        if (auth.getUid() != null && postId != null){
            //add caption and currentTime to userPost object
            addToUser();
            databaseReference.child("Posts").child(postId).setValue(userPost);
        }else{
            Toast.makeText(getActivity(),"Something went wrong, Try Again",Toast.LENGTH_SHORT).show();
        }
        //set userPostId
        userPost.setPostId(postId);
    }

//    This will return the file extension to  store in Fire Storage
    private String getFileExtension(Uri uri){
        if (getActivity() != null){
            ContentResolver cr = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
        return "null";
    }
    private void initView(View view){
        post = view.findViewById(R.id.post);
        addImage = view.findViewById(R.id.addImage);
        postEditText = view.findViewById(R.id.postEditText);

        newPhotoRecView = view.findViewById(R.id.newPhotoRecView);

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
