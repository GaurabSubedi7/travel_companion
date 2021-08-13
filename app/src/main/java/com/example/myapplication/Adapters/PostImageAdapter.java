package com.example.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> images = new ArrayList<>();

    public PostImageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostImageAdapter.ViewHolder holder, int position) {
//        if (!images.get(position).isEmpty()){
//            holder.userPostImageProgressBar.setVisibility(View.VISIBLE);
//            Glide.with(context).asBitmap()
//                    .placeholder(R.mipmap.loading)
//                    .load(images.get(position))
//                    .listener(new RequestListener<Bitmap>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                            holder.userPostImageProgressBar.setVisibility(View.VISIBLE);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                            holder.userPostImageProgressBar.setVisibility(View.GONE);
//                            return false;
//                        }
//                    })
//                    .into(holder.singlePostImage);
//        }
        if (!images.get(position).isEmpty()){
            holder.userPostImageProgressBar.setVisibility(View.GONE);
            Glide.with(context).load(images.get(position))
                    .thumbnail(Glide.with(context).load(R.drawable.loadingsplash))
                    .fitCenter()
                    .into(holder.singlePostImage);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView singlePostImage;
        private final ProgressBar userPostImageProgressBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            singlePostImage = itemView.findViewById(R.id.singlePostImage);
            userPostImageProgressBar = itemView.findViewById(R.id.userPostImageProgressBar);
        }
    }
}
