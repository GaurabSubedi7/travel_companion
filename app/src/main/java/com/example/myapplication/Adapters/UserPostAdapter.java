package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.CategoryFragment;
import com.example.myapplication.Fragments.EditPostFragment;
import com.example.myapplication.Fragments.MapFragment;
import com.example.myapplication.Fragments.OwnPostFragment;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder>{
    private static final String TAG = "UserPostAdapter";

    private Context context;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private PostImageAdapter adapter;
    private String currentUserId;
    private FragmentManager fm;
    private Dialog ownPostDialog;

    //firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public UserPostAdapter(Context context, String currentUserId, FragmentManager fm) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.fm = fm;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_user_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull UserPostAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            String uid = userPosts.get(position).getUserId();
            for(User user: users){
                if(user.getUserId().equals(uid)){
                    holder.uploaderUserName.setText(user.getUserName());
                    break;
                }
            }

            holder.userPopUpMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentUserId.equals(userPosts.get(position).getUserId())){
                        showUserSelfPostMenu(holder.userPopUpMenu, position);
                    } else {
                        showUserPostMenu(holder.userPopUpMenu, position);
                    }
                }
            });

            holder.likeUnchecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.likeUnchecked.setVisibility(View.GONE);
                    holder.likeChecked.setVisibility(View.VISIBLE);
                    String likeCountKey = databaseReference.push().getKey();
                    if (likeCountKey != null) {
                        databaseReference.child("Posts").child(userPosts.get(position).getPostId())
                                .child("likeCount").child(likeCountKey).setValue(currentUserId);
                    }
                }
            });

            holder.likeChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.likeUnchecked.setVisibility(View.VISIBLE);
                    holder.likeChecked.setVisibility(View.GONE);
                    //unlike
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(currentUserId != null){
                                for(DataSnapshot data : snapshot.child("Posts").child(userPosts.get(position).getPostId())
                                        .child("likeCount").getChildren()){
                                    String myKey = (String) data.getValue();
                                    if(myKey != null && myKey.equals(currentUserId)){
                                        data.getRef().removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Log.e(TAG, "onCancelled", error.toException());
                        }
                    });
                }
            });

            if(!userPosts.get(position).getLikeCount().isEmpty()) {
                ArrayList<User> liker = new ArrayList<>(userPosts.get(position).getLikeCount());
                holder.likeCount.setText(String.valueOf(liker.size()));
                for(User u: liker) {
                    if (currentUserId.equals(u.getUserId())) {
                        holder.likeUnchecked.setVisibility(View.GONE);
                        holder.likeChecked.setVisibility(View.VISIBLE);
                    }
                }
            }

            //load image into imageView
            adapter = new PostImageAdapter(context);
            if(!userPosts.get(position).getImageURL().isEmpty()) {
                ArrayList<String> images = new ArrayList<>(userPosts.get(position).getImageURL());
                if(images.size() == 1){
                    holder.imageCount.setVisibility(View.GONE);
                }else{
                    holder.imageCount.setVisibility(View.VISIBLE);
                    holder.imageCount.setText("1/" + images.size());
                }
                holder.userPostImageRecView.setAdapter(adapter);
                LinearLayoutManager llm = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.userPostImageRecView.setLayoutManager(llm);
                adapter.setImages(images);
                holder.userPostImageRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int visiblePosition = llm.findFirstVisibleItemPosition();
                        if(visiblePosition > -1) {
                            visiblePosition += 1;
                            holder.imageCount.setText(visiblePosition + "/" + images.size());
                        }
                    }
                });
            }

            holder.caption.setText(userPosts.get(position).getCaption());
            holder.postUploadDate.setText(userPosts.get(position).getUploadDate());
            holder.postLocation.setText(userPosts.get(position).getSpecificLocation());
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(context, "Image Failed To Load", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserSelfPostMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_self_post_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menuEdit:
                        editUserPost(position);
                        break;
                    case R.id.menuDelete:
                        deleteUserPost(position);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void editUserPost(int position){
        EditPostFragment editPostFragment = new EditPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postId", userPosts.get(position).getPostId());
        bundle.putString("caption", userPosts.get(position).getCaption());
        if (fm != null) {
            editPostFragment.setArguments(bundle);
            editPostFragment.show(fm, "edit caption");
        }
    }

    private void deleteUserPost(int position){
        AlertDialog.Builder postTerminator = new AlertDialog.Builder(Objects.requireNonNull(context))
                .setTitle("Want To Delete Post?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //should be empty
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete From Firebase
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(auth.getUid() != null){
                                    for(DataSnapshot data : snapshot.child("Posts").getChildren()){
                                        String myKey = data.getKey();
                                        if(myKey != null && myKey.equals(userPosts.get(position).getPostId())){
                                            data.getRef().removeValue();
                                            Toast.makeText(context, "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            if(ownPostDialog != null){
                                                ownPostDialog.dismiss();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                });
        postTerminator.create().show();
    }

    private void showUserPostMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_post_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuViewLocation){
                    if(userPosts.get(position).getSpecificLocation().equals("unknown")){
                        Toast.makeText(context, "Location is Unknown", Toast.LENGTH_SHORT).show();
                    }else {
                        FragmentTransaction ft = fm.beginTransaction();
                        MapFragment mapFragment = new MapFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", userPosts.get(position).getSpecificLocation());
                        bundle.putDouble("latitude", userPosts.get(position).getLatitude());
                        bundle.putDouble("longitude", userPosts.get(position).getLongitude());
                        mapFragment.setArguments(bundle);
                        ft.replace(R.id.FrameContainer, mapFragment).addToBackStack(null);
                        ft.commit();
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public void setUserPosts(ArrayList<UserPost> userPosts) {
        this.userPosts = userPosts;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setDialog(Dialog dialog){
        this.ownPostDialog = dialog;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView likeChecked, likeUnchecked, uploaderImage;
        private RecyclerView userPostImageRecView;
        private TextView likeCount, caption, postLocation, postUploadDate, imageCount, uploaderUserName;
        private ImageButton userPopUpMenu;
        private ProgressBar userPostImageProgressBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view){
            userPostImageRecView = view.findViewById(R.id.userPostImageRecView);

            likeChecked = view.findViewById(R.id.likeChecked);
            likeUnchecked = view.findViewById(R.id.likeUnchecked);
            likeCount = view.findViewById(R.id.likeCount);

            userPopUpMenu = view.findViewById(R.id.userPopUpMenu);

            caption = view.findViewById(R.id.caption);
            uploaderImage = view.findViewById(R.id.uploaderImage);
            uploaderUserName = view.findViewById(R.id.uploaderUserName);

            postLocation = view.findViewById(R.id.postLocation);
            postUploadDate = view.findViewById(R.id.postUploadDate);

            userPostImageProgressBar = view.findViewById(R.id.userPostImageProgressBar);
            imageCount = view.findViewById(R.id.imageCount);
        }
    }
}
