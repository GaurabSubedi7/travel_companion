package com.example.myapplication.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class ContactUsFragment extends DialogFragment {

    private static final int REQUEST_PHONE_CALL = 444;

    private TextView serviceType, serviceName, description, email, contactNumber;
    private Button phoneBtn, emailBtn;
    private ImageView closeDesc;

    private String sName, sType, sDesc, sEmail, sContactNumber;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.contact_us_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
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
        Uri u = Uri.parse("tel:" + sContactNumber);
        Intent i = new Intent(Intent.ACTION_DIAL, u);
        if(getContext() != null && getActivity() != null){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                openPhone();
            }else
            {
                startActivity(i);
            }
        }
    }

    private void openEmail(){
        if(getContext() != null){
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{sEmail});

            getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
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
