package com.example.foodliappserver.Screens.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodliapp.Common.Common;
import com.example.foodliapp.R;
import com.example.foodliapp.Screens.ui.Authentication.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SendMessageFragment extends Fragment {
    Button btnLogout;
    CheckBox chkNotification;

    TextView txtFullName, txtPhone, txtChangePassword,txtEditProfile,txtItemBought,txtFavItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        chkNotification = root.findViewById(R.id.chkNotification);

        txtFullName = root.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        txtPhone = root.findViewById(R.id.txtPhone);
        txtPhone.setText(Common.currentUser.getPhone());

        Paper.init(requireContext());
        txtEditProfile = root.findViewById(R.id.txtEditProfile);
        txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        txtChangePassword = root.findViewById(R.id.txtChangePassword);
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
        txtItemBought = root.findViewById(R.id.txtItemBought);
        txtFavItems = root.findViewById(R.id.txtFavItems);

        String isSubscribe = Paper.book().read("sub_news");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals(false)){
            chkNotification.setChecked(false);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
            Paper.book().write("sub_news", "false");
        }else {
            chkNotification.setChecked(true);
            FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
            Paper.book().write("sub_news", "true");
        }

        btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout ?");
                builder.setPositiveButton(Html.fromHtml("<font color= '#DE8405'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete remember user
                        Paper.book().destroy();
                        // log out
                        Intent intent = new Intent(getContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color= '#DE8405'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // show dialog
                builder.show();

            }
        });
        return root;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("CHANGE PASSWORD");
        builder.setMessage("Please fill all information");
        LayoutInflater inflater =  LayoutInflater.from(requireContext());
        View layout_change_password = inflater.inflate(R.layout.change_password_layout, null);

        MaterialEditText edtPassword = layout_change_password.findViewById(R.id.edtPassword);
        MaterialEditText edtNewPassword = layout_change_password.findViewById(R.id.edtNewPassword);
        MaterialEditText edtConfirmPassword = layout_change_password.findViewById(R.id.edtConfirmPassword);
        builder.setIcon(R.drawable.ic_baseline_security);
        builder.setView(layout_change_password);
        builder.setPositiveButton(Html.fromHtml("<font color= '#DE8405'>SAVE</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Change password

                AlertDialog waitingDialog = new SpotsDialog(requireContext());
                waitingDialog.show();
                // chack old password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())){

                    // check new and confirm password mismatch
                    if (edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){

                        Map<String,Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("Password",edtNewPassword.getText());
                        // Make Update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else {
                        waitingDialog.dismiss();
                        Toast.makeText(requireContext(), "new password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    waitingDialog.dismiss();
                    Toast.makeText(requireContext(), "Wrong old password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color= '#DE8405'>CANCEL</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // show dialog
        builder.show();

    }


}