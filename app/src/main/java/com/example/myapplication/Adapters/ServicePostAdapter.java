package com.example.myapplication.Adapters;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Fragments.ContactUsFragment;
import com.example.myapplication.Fragments.EditServiceFragment;
import com.example.myapplication.Models.ServicePost;
import com.example.myapplication.Models.User;
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
    private String caller, email;

    public ServicePostAdapter(Context context, FragmentManager fm, String caller, String email){
        this.context = context;
        this.fm = fm;
        this.caller = caller;
        this.email = email;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_service_post, parent, false);
        return new ServicePostAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String uid = servicePosts.get(position).getUserId();
        for(User user: users){
            if(user.getUserId().equals(uid)){
                holder.uploaderUserName.setText(user.getUserName());
                if(user.getProfilePic() != null){
                    Glide.with(context).load(user.getProfilePic())
                            .thumbnail(Glide.with(context).load(R.drawable.ic_user))
                            .into(holder.uploaderImage);
                }
                break;
            }
        }

        if(caller.equals("myServices")){
            holder.edtDltServiceRelLayout.setVisibility(View.VISIBLE);
            holder.contactUsBtn.setVisibility(View.GONE);
            holder.serviceRatingBar.setIsIndicator(true);
        }

        if(caller.equals("users")){
            holder.edtDltServiceRelLayout.setVisibility(View.GONE);
            holder.contactUsBtn.setVisibility(View.VISIBLE);
            holder.serviceRatingBar.setIsIndicator(false);
        }

        holder.serviceName.setText(servicePosts.get(position).getServiceName());
        holder.serviceType.setText(servicePosts.get(position).getServiceType());
        holder.postUploadDate.setText(servicePosts.get(position).getUploadDate());
        holder.postLocation.setText(servicePosts.get(position).getServiceLocation());
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

        //manipulation of buttons
        holder.deleteServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteService(position);
            }
        });

        holder.editServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editService(position);
            }
        });

        holder.contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactUs(position);
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

    private void deleteService(int position){
        AlertDialog.Builder serviceTerminator = new AlertDialog.Builder(Objects.requireNonNull(context))
                .setTitle("Want To Delete This Service?")
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
                                    for(DataSnapshot data : snapshot.child("Services").getChildren()){
                                        String myKey = data.getKey();
                                        if(myKey != null && myKey.equals(servicePosts.get(position).getServiceId())){
                                            data.getRef().removeValue();
                                            Toast.makeText(context, "Service Deleted Successfully", Toast.LENGTH_SHORT).show();
//                                            if(ownPostDialog != null){
//                                                ownPostDialog.dismiss();
//                                            }
                                            // TODO: 8/13/21 open own service dialog box
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
        serviceTerminator.create().show();
    }

    private void editService(int position){
        EditServiceFragment editServiceFragment = new EditServiceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("serviceId", servicePosts.get(position).getServiceId());
        bundle.putString("serviceName", servicePosts.get(position).getServiceName());
        bundle.putString("serviceContact", servicePosts.get(position).getContactNumber());
        bundle.putString("serviceDesc", servicePosts.get(position).getServiceDescription());
        if (fm != null) {
            editServiceFragment.setArguments(bundle);
            editServiceFragment.show(fm, "edit service");
        }
    }

    private void openContactUs(int position){
        ContactUsFragment contactUsFragment = new ContactUsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("serviceId", servicePosts.get(position).getServiceId());
        bundle.putString("serviceName", servicePosts.get(position).getServiceName());
        bundle.putString("serviceContact", servicePosts.get(position).getContactNumber());
        bundle.putString("serviceDesc", servicePosts.get(position).getServiceDescription());
        bundle.putString("serviceType", servicePosts.get(position).getServiceType());
        bundle.putString("email", email);
        if (fm != null) {
            contactUsFragment.setArguments(bundle);
            contactUsFragment.show(fm, "show service description");
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
        private TextView serviceName, serviceType, imageCount, uploaderUserName,
                postLocation, postUploadDate;
        private ProgressBar userPostImageProgressBar;
        private Button contactUsBtn, deleteServiceBtn, editServiceBtn;
        private RatingBar serviceRatingBar;
        private RelativeLayout edtDltServiceRelLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view){
            userPostImageRecView = view.findViewById(R.id.servicePostImageRecView);

            serviceName = view.findViewById(R.id.serviceName);
            serviceType = view.findViewById(R.id.serviceType);
            uploaderImage = view.findViewById(R.id.uploaderImage);
            uploaderUserName = view.findViewById(R.id.uploaderUserName);

            postLocation = view.findViewById(R.id.servicePostLocation);
            postUploadDate = view.findViewById(R.id.servicePostUploadDate);

            contactUsBtn = view.findViewById(R.id.contactUsBtn);
            deleteServiceBtn = view.findViewById(R.id.deleteServiceBtn);
            editServiceBtn = view.findViewById(R.id.editServiceBtn);

            serviceRatingBar = view.findViewById(R.id.serviceRatingBar);

            edtDltServiceRelLayout = view.findViewById(R.id.edtDltServiceRelLayout);

            userPostImageProgressBar = view.findViewById(R.id.userPostImageProgressBar);
            imageCount = view.findViewById(R.id.serviceImageCount);
        }
    }
}
