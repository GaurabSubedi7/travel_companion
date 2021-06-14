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
import com.example.myapplication.Models.Image;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private Context context;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_image_scroll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.ViewHolder holder, int position) {
        //load image into imageView
        holder.btnClose.setVisibility(View.VISIBLE);
        if(imageUris.get(position) != null) {
            Glide.with(context).asBitmap()
                    .load(imageUris.get(position)).into(holder.userPostSmallImage);
        }
        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image.getInstance().getImageUris().remove(imageUris.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void setImageUris(ArrayList<Uri> imageUris) {
        this.imageUris = imageUris;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView userPostSmallImage;
            ImageButton btnClose;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            //init view
            userPostSmallImage = itemView.findViewById(R.id.userPostSmallImage);
            btnClose = itemView.findViewById(R.id.btnClose);
        }
    }
}
