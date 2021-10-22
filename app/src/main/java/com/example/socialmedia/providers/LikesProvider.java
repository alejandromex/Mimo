package com.example.socialmedia.providers;

import com.example.socialmedia.models.Like;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;

public class LikesProvider {

    CollectionReference mCollection;

    public LikesProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("likes");
    }

    public Task<Void> create(Like like)
    {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikeByPostAndUser(String userId, String postId)
    {
        return mCollection.whereEqualTo("idPost", postId).whereEqualTo("idUser",userId);
    }

    public Task<Void> delete(String id)
    {
        return mCollection.document(id).delete();
    }

    public Query getLikesByPost(String idPost)
    {
        return mCollection.whereEqualTo("idPost", idPost);
    }

}
