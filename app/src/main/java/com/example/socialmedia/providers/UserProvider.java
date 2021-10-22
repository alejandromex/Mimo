package com.example.socialmedia.providers;

import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProvider {

    private CollectionReference mCollection;

    public UserProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> GetUser(String id)
    {
        return mCollection.document(id).get();
    }

    public Task<Void> CreateUser(User user)
    {
        return mCollection.document(user.getId()).set(user);
    }

    public Task<Void> Update(User user)
    {
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("username", user.getUsername());
        mapUser.put("phone", user.getPhone());
        mapUser.put("timeStamp", new Date().getTime());
        mapUser.put("image_profile", user.getImageProfile());
        mapUser.put("image_cover", user.getImageCover());

        return mCollection.document(user.getId()).update(mapUser);
    }



}
