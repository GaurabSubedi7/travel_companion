package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Fragments.EditPostFragment;
import com.example.myapplication.Fragments.OwnPostFragment;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private Context context;
    private String parentActivity;
    private FragmentManager fm;

    public PostAdapter(Context context, String parentActivity, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
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
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(parentActivity.equalsIgnoreCase("profile")) {
            try{
                //load image into profile imageView
                if(!userPosts.get(position).getImageURL().get(0).isEmpty()) {
                    holder.btnClose.setVisibility(View.GONE);
                    Glide.with(context).asBitmap()
                            .load(userPosts.get(position).getImageURL().get(0)).into(holder.userPostSmallImage);

                    //small image onclick action here
                    holder.userPostSmallImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OwnPostFragment ownPostFragment = new OwnPostFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("postId", userPosts.get(position).getPostId());
                            if (fm != null) {
                                ownPostFragment.setArguments(bundle);
                                ownPostFragment.show(fm, "own post");
                            }
                        }
                    });
                }
            }catch (IndexOutOfBoundsException e){
                System.out.println("Image not loaded in firebase yet");
            }
        }
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    //set userPost array
    @SuppressLint("NotifyDataSetChanged")
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
