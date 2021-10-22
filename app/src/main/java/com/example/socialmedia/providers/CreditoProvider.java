package com.example.socialmedia.providers;

import com.example.socialmedia.models.Credito;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreditoProvider {
    CollectionReference mCollection;
    public CreditoProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Creditos");
    }

    public Task<Void> save(Credito credito)
    {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        credito.setId(id);
        return document.set(credito);
    }
}
