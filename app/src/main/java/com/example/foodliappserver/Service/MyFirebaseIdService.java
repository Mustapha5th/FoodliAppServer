package com.example.foodliappserver.Service;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        updateTokenToFirebase(tokenRefreshed);

    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token token = new Token(tokenRefreshed,false); // false because this token send from client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);

    }
}
