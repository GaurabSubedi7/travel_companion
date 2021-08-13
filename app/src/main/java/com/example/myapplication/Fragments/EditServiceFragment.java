package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditServiceFragment extends DialogFragment {

    private EditText serviceNameUpdate, servicePriceUpdate, serviceDescUpdate;
    private Button cancelEdit, updateService;

    private String serviceName, serviceDescription, serviceId;
    private int servicePrice;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_service_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Edit Service");
        initView(view);
        setCancelable(false);

        if(getArguments() != null){
            serviceName = getArguments().getString("serviceName");
            serviceId = getArguments().getString("serviceId");
            servicePrice = getArguments().getInt("servicePrice");
            serviceDescription = getArguments().getString("serviceDesc");

            //set values for editTexts
            serviceNameUpdate.setText(serviceName);
            servicePriceUpdate.setText(String.valueOf(servicePrice));
            serviceDescUpdate.setText(serviceDescription);
        }

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        updateService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serviceNameUpdate.getText().toString().isEmpty() || servicePriceUpdate.getText().toString().isEmpty()
                    || serviceDescUpdate.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    serviceName = serviceNameUpdate.getText().toString();
                    serviceDescription = serviceDescUpdate.getText().toString();
                    servicePrice = Integer.parseInt(servicePriceUpdate.getText().toString());
                    if(auth.getUid() != null && serviceId != null){
                        databaseReference.child("Services").child(serviceId).child("serviceName").setValue(serviceName);
                        databaseReference.child("Services").child(serviceId).child("serviceDescription").setValue(serviceDescription);
                        databaseReference.child("Services").child(serviceId).child("servicePrice").setValue(servicePrice);
                        dismiss();
                        Toast.makeText(getContext(), "Service Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return builder.create();
    }

    private void initView(View view) {
        serviceNameUpdate = view.findViewById(R.id.serviceNameUpdate);
        servicePriceUpdate = view.findViewById(R.id.servicePriceUpdate);
        serviceDescUpdate = view.findViewById(R.id.serviceDescUpdate);
        cancelEdit = view.findViewById(R.id.cancel_edit_service);
        updateService = view.findViewById(R.id.update_service);
    }
}
