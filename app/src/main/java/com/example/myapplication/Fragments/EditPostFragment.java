package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPostFragment extends DialogFragment {

    private Button cancel_edit, update_post;
    private EditText postEditText;
    private String postId, caption;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_post_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Edit Post");
        initView(view);
        setCancelable(false);
        if(getArguments() != null){
            postId = getArguments().getString("postId");
            caption = getArguments().getString("caption");
            postEditText.setText(caption);
        }

        cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        update_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postEditText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Caption cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    caption = postEditText.getText().toString();
                    if(auth.getUid() != null && postId != null){
                        databaseReference.child("Posts").child(postId).child("caption")
                                .setValue(caption);
                        dismiss();
                        Toast.makeText(getContext(), "Post Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return builder.create();
    }

    private void initView(View view){
        cancel_edit = view.findViewById(R.id.cancel_edit);
        update_post = view.findViewById(R.id.update_post);
        postEditText = view.findViewById(R.id.postEditText);
    }
}
