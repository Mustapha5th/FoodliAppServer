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

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Model.MyResponse;
import com.example.foodliappserver.Model.Notification;
import com.example.foodliappserver.Model.Sender;
import com.example.foodliappserver.R;
import com.example.foodliappserver.Remote.APIService;
import com.example.foodliappserver.Screens.ui.Authentication.Login;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageFragment extends Fragment {
    Button btnSend;
    MaterialEditText edtTitle, edtMessage;
    APIService mService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_send_message, container, false);

        mService = Common.getFCMService();
        edtMessage = root.findViewById(R.id.edtMessage);
        edtTitle = root.findViewById(R.id.edtTitle);

        btnSend = root.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitle.getText().toString().isEmpty()||edtMessage.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

                }else {
                // create message
                Notification notification = new Notification(edtTitle.getText().toString(), edtMessage.getText().toString());
                Sender toTopic = new Sender();
                toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                toTopic.notification = notification;
                mService.sendNotification(toTopic).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful()){
                            edtTitle.setText("");
                            edtMessage.setText("");
                            Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
            }
        });

        return root;
    }



}