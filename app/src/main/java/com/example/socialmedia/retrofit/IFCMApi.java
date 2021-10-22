package com.example.socialmedia.retrofit;

import com.example.socialmedia.models.FCMBody;
import com.example.socialmedia.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=\tAAAA1Ts4L2o:APA91bFiB79znbph2knROKZ0z9OXtKR8cVP_eufKn4Gm0CKwyuY88BR-TbQ6EOvbA6n8syuHZiTdbrVmLXypFATK5pXSb9H3T2_hRg3z4YsqcHMQ8dagrr-BnZ1d8uCLeiP7kssDeczZ"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);


}
