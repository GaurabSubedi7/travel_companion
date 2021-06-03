package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.UserPost;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private Context context;

    public PostAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_image_scroll, parent, false);
        return (new ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.ViewHolder holder, int position) {
        //load image into the imageView
        Glide.with(context).asBitmap()
                .load(userPosts.get(position).getImageURL().get(0)).into(holder.userPostSmallImage);
        System.out.println("SECOND IMAGE : " + userPosts.get(position).getImageURL().get(0));
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    //set userPost array
    public void setUserPosts(ArrayList<UserPost> userPosts) {
        this.userPosts = userPosts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView userPostSmallImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userPostSmallImage = itemView.findViewById(R.id.userPostSmallImage);
        }
    }
}
