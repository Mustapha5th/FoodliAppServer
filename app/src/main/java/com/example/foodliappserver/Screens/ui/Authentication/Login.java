package com.example.foodliappserver.Screens.ui.Authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Model.User;
import com.example.foodliappserver.R;
import com.example.foodliappserver.Screens.Home;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;


public class Login extends AppCompatActivity {
    MaterialEditText edtPhone, edtPassword;
    TextView txtLogin;
    Button btnLogin;
    RelativeLayout rootLayout;
    FirebaseDatabase database;
    DatabaseReference table_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!Common.isConnectedToInternet(getBaseContext())) {
            Snackbar snackbar = Snackbar.make(rootLayout,"Please check your internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        txtLogin = findViewById(R.id.txtLogin);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Caroline.otf");
        txtLogin.setTypeface(face);

        btnLogin = findViewById(R.id.btnLogin);
        // init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               SignInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }

        });


    }

    private void SignInUser(String toString, String toString1) {
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(Login.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //checking if user exists
                    if (snapshot.child(edtPhone.getText().toString()).exists()) {
                        //Get User information
                        mDialog.dismiss();
                        User user = snapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());//get phone number

                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            Intent home = new Intent(Login.this, Home.class);
                            Common.currentUser = user;
                            startActivity(home);
                            finish();
                            table_user.removeEventListener(this);
                        } else {
                            Toast.makeText(Login.this, "Wrong Password or Username...", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(Login.this, "User does not exist", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else  {

            // TO DO: convert to snack bar ...
            Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}