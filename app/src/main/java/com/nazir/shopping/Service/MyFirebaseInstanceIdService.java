package com.nazir.shopping.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Model.Token;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();

        if (Common.cuurentUser != null) {
            updateTokenToFirebase(tokenRefreshed);
        }
    }

    private void updateTokenToFirebase(String tokenRefreshed) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference tokens = firebaseDatabase.getReference("tokens");
        Token token = new Token(tokenRefreshed,false); //false because this token sent from Client app
        tokens.child(Common.cuurentUser.getPhone()).setValue(token);
    }
}
