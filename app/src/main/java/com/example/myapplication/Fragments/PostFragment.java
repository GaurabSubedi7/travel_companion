package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

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
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.MY_DATABASE;

public class PostFragment extends DialogFragment {
    private Button post, addImage;
    private EditText postEditText;

    //Calendar
    private Calendar calendar = Calendar.getInstance();

    //Loading prompt class
    private ProgressDialog progressDialog;

    //Firebase Classes
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //URI are similar to URLs but they are used for local storage
    //Image URI
    private Uri imageUri;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_post, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Share Your Experience");

        initView(view);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the image (ALL IMAGES TYPE) from  gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = postEditText.getText().toString();
                if(imageUri != null){
                    addToFirebase(imageUri, status);
                }else{
                    Toast.makeText(getActivity(),"Something not happen as it should be, gibb try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
        }
    }

    private void addToFirebase(Uri uri, String writePost){
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Date date = calendar.getTime();
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        UserPost userPost = new UserPost(uri.toString(), writePost, currentTime);
                        String postId = databaseReference.push().getKey();
                        if(auth.getUid() != null && postId != null) {
                            databaseReference.child("Users").child(auth.getUid()).child("Posts").child(postId).setValue(userPost);
                            System.out.println("======================> " + userPost.getImageURL() + "===" + userPost.getStatus());
                            Toast.makeText(getActivity(),"Feww That was close, but ur post has been uploaded successfully)",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Something not happen as it should be, gibb try again",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Something not happen as it should be, gibb try again",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //This will return the file extension to  store in Fire Storage
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
