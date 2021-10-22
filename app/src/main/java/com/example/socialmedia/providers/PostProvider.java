package com.example.socialmedia.providers;

import com.example.socialmedia.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post post)
    {
        return mCollection.document().set(post);
    }

    public Query getAll()
    {
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }


    public Query getPostsByUser(String idUser)
    {
        return mCollection.whereEqualTo("idUser", idUser);
    }

    public Query getPostByTitle(String title)
    {
        return mCollection.orderBy("title").startAt(title).endAt(title+'\uf8ff');
    }

    public Task<DocumentSnapshot> getPostById(String postId)
    {
        return mCollection.document(postId).get();
    }

    public Task<Void> delete(String id)
    {
        return mCollection.document(id).delete();
    }

    public Query getPostByCategoryAndTimestamp(String category)
    {
        return mCollection.whereEqualTo("category", category).orderBy("timeStamp", Query.Direction.DESCENDING);
    }

}
