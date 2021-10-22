package com.example.socialmedia.providers;

import com.example.socialmedia.models.Credito;
import com.example.socialmedia.models.Evento;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class EventsProvider {

    CollectionReference mCollection;
    public EventsProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("events");
    }

    public Query getAll(String idUser)
    {
        return mCollection.whereEqualTo("idUser", idUser);
    }

    public Task<Void> save(Evento evento)
    {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        evento.setId(id);
        return document.set(evento);
    }

    public Task<DocumentSnapshot> getEvento(String id)
    {
        return mCollection.document(id).get();
    }


}
