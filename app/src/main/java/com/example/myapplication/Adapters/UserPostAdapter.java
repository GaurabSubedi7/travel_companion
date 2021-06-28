package com.example.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder>{

    private Context context;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private PostImageAdapter adapter;

    public UserPostAdapter(Context context) {
        this.context = context;
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

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public void setUserPosts(ArrayList<UserPost> userPosts) {
        this.userPosts = userPosts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView likeChecked, likeUnchecked;
        private RecyclerView userPostImageRecView;
        private TextView likeCount, caption, postLocation, postUploadDate, imageCount;
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
            postLocation = view.findViewById(R.id.postLocation);
            postUploadDate = view.findViewById(R.id.postUploadDate);
            userPostImageProgressBar = view.findViewById(R.id.userPostImageProgressBar);
            imageCount = view.findViewById(R.id.imageCount);
        }
    }
}
