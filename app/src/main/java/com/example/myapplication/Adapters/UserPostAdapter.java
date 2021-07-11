package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder>{

    private Context context;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private PostImageAdapter adapter;
    private String currentUserId;

    public UserPostAdapter(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_user_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserPostAdapter.ViewHolder holder, int position) {
        try{
            for(User user: users){
                if(user.getUserId().equals(userPosts.get(position).getUserId())){
                    holder.uploaderUserName.setText(user.getUserName());
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
            //load image into imageView
            adapter = new PostImageAdapter(context);
            if(!userPosts.get(position).getImageURL().isEmpty()) {
                ArrayList<String> images = new ArrayList<>(userPosts.get(position).getImageURL());
                holder.imageCount.setText("1/" + images.size());
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
                        Toast.makeText(context, "Edit Button Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuDelete:
                        Toast.makeText(context, "Delete Button Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void showUserPostMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_post_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuViewLocation){
                    Toast.makeText(context, "View Location Clicked", Toast.LENGTH_SHORT).show();
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
