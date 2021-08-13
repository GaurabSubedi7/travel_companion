package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class ContactUsFragment extends DialogFragment {

    private TextView serviceType, serviceName, description, email, contactNumber;
    private Button phoneBtn, emailBtn;
    private ImageView closeDesc;

    private String sName, sType, sDesc, sEmail, sContactNumber;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.contact_us_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Description");
        initView(view);
        setCancelable(false);

        if(getArguments() != null){
            sName = getArguments().getString("serviceName");
            sType = getArguments().getString("serviceType");
            sDesc = getArguments().getString("serviceDesc");
            sEmail = getArguments().getString("email");
            sContactNumber = getArguments().getString("serviceContact");

            serviceName.setText(sName);
            serviceType.setText(sType);
            description.setText(sDesc);
            email.setText(sEmail);
            contactNumber.setText(sContactNumber);
        }

        closeDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhone();
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });

        return builder.create();
    }

    private void openPhone() {

    }

    private void openEmail(){

    }

    private void initView(View view) {
        serviceName = view.findViewById(R.id.serviceNameView);
        serviceType = view.findViewById(R.id.serviceTypeView);
        description = view.findViewById(R.id.serviceDescriptionView);
        email = view.findViewById(R.id.emailInfo);
        contactNumber = view.findViewById(R.id.contactNumberInfo);
        phoneBtn = view.findViewById(R.id.phoneBtn);
        emailBtn = view.findViewById(R.id.emailBtn);
        closeDesc = view.findViewById(R.id.closeDesc);
    }
}
