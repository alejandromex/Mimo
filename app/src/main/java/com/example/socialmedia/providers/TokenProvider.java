package com.example.socialmedia.providers;

import com.example.socialmedia.models.Token;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    CollectionReference mCollection;
    public TokenProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("tokens");
    }

    public void create(String idUser)
    {
        if(idUser == null)
        {
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s ->{

            Token token = new Token(s);
            mCollection.document(idUser).set(token);

        });
    }

    public Task<DocumentSnapshot> getToken(String idUser)
    {
        return mCollection.document(idUser).get();
    }

}
