package com.example.myapplication.Adapters;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.ServicePost;
import com.example.myapplication.Models.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServicePostAdapter extends RecyclerView.Adapter<ServicePostAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ServicePost> servicePosts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private FragmentManager fm;

    //firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private PostImageAdapter adapter;

    public ServicePostAdapter(Context context, FragmentManager fm){
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_service_post, parent, false);
        return new ServicePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String uid = servicePosts.get(position).getUserId();
        for(User user: users){
            if(user.getUserId().equals(uid)){
                holder.uploaderUserName.setText(user.getUserName());
                break;
            }
        }

        holder.serviceName.setText(servicePosts.get(position).getServiceName());
        holder.serviceType.setText(servicePosts.get(position).getServiceType());
        holder.postUploadDate.setText(servicePosts.get(position).getUploadDate());
        holder.postLocation.setText(servicePosts.get(position).getServiceLocation());
        holder.servicePrice.setText(String.valueOf(servicePosts.get(position).getServicePrice()));
        holder.serviceRatingBar.setRating((float)servicePosts.get(position).getRating());

        holder.serviceRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(auth.getUid() != null) {
                    databaseReference.child("Services").child(servicePosts.get(position).getServiceId())
                            .child("rating").child(auth.getUid()).setValue(rating);
                    holder.serviceRatingBar.setRating(rating);
                }
            }
        });

        //load image into imageView
        adapter = new PostImageAdapter(context);
        if(!servicePosts.get(position).getImageURL().isEmpty()) {
            ArrayList<String> images = new ArrayList<>(servicePosts.get(position).getImageURL());
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
    }

    @Override
    public int getItemCount() {
        return servicePosts.size();
    }

    public void setServicePosts(ArrayList<ServicePost> servicePosts) {
        this.servicePosts = servicePosts;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView uploaderImage;
        private RecyclerView userPostImageRecView;
        private TextView serviceName, serviceType, servicePrice, imageCount, uploaderUserName,
                postLocation, postUploadDate;
        private ImageButton userPopUpMenu;
        private ProgressBar userPostImageProgressBar;
        private Button contactUsBtn;
        private RatingBar serviceRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view){
            userPostImageRecView = view.findViewById(R.id.servicePostImageRecView);

            userPopUpMenu = view.findViewById(R.id.servicePopUpMenu);

            serviceName = view.findViewById(R.id.serviceName);
            serviceType = view.findViewById(R.id.serviceType);
            servicePrice = view.findViewById(R.id.servicePrice);
            uploaderImage = view.findViewById(R.id.uploaderImage);
            uploaderUserName = view.findViewById(R.id.uploaderUserName);

            postLocation = view.findViewById(R.id.servicePostLocation);
            postUploadDate = view.findViewById(R.id.servicePostUploadDate);

            contactUsBtn = view.findViewById(R.id.contactUsBtn);
            serviceRatingBar = view.findViewById(R.id.serviceRatingBar);

            userPostImageProgressBar = view.findViewById(R.id.userPostImageProgressBar);
            imageCount = view.findViewById(R.id.serviceImageCount);
        }
    }
}
