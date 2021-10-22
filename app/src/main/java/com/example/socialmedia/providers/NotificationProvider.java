package com.example.socialmedia.providers;

import com.example.socialmedia.models.FCMBody;
import com.example.socialmedia.models.FCMResponse;
import com.example.socialmedia.retrofit.IFCMApi;
import com.example.socialmedia.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body)
    {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
