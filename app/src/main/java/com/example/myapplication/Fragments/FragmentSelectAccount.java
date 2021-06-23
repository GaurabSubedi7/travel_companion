package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.RegistrationActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FragmentSelectAccount extends DialogFragment {

    private RadioGroup rgAccount;
    private Button btnSelectAccount;

    public FragmentSelectAccount() {
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.account_select_dialog_box, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Select Account Type");
        initView(view);

        btnSelectAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountType = "";
                switch (rgAccount.getCheckedRadioButtonId()){
                    case R.id.rbUser:
                        accountType = "userAccount";
                        break;
                    case R.id.rbBusiness:
                        accountType = "businessAccount";
                        break;
                    default:
                        break;
                }

                Intent intent = new Intent(getContext(), RegistrationActivity.class);
                intent.putExtra("accountType", accountType);
                Objects.requireNonNull(getActivity()).finish();
                startActivity(intent);
            }
        });

        return builder.create();
    }

    private void initView(View view){
        rgAccount = view.findViewById(R.id.rgAccount);
        btnSelectAccount = view.findViewById(R.id.btnSelectAccount);
    }
}
