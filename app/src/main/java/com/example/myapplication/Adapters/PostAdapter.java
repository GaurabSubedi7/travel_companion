package com.example.myapplication.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private Context context;
    private String parentActivity;

    public PostAdapter(Context context, String parentActivity) {
        this.context = context;
        this.parentActivity = parentActivity;
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
        if(parentActivity.equalsIgnoreCase("profile")) {
            //load image into profile imageView
            if(userPosts.get(position).getImageURL().get(0) != null) {
                holder.btnClose.setVisibility(View.GONE);
                Glide.with(context).asBitmap()
                        .load(userPosts.get(position).getImageURL().get(0)).into(holder.userPostSmallImage);
            }
        }

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
        private ImageButton btnClose;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userPostSmallImage = itemView.findViewById(R.id.userPostSmallImage);
            btnClose = itemView.findViewById(R.id.btnClose);
        }
    }
}
