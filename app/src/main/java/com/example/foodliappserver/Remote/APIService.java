package com.example.foodliappserver.Remote;

import com.example.foodliappserver.Model.MyResponse;
import com.example.foodliappserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAk7hka68:APA91bH-ZZSph0lNI8TbOm8MtRfJPjQ5CJvChGCv3_UpHOGKzEknzfCGPfLp_E_EbhRadWf1NjNFQ6myKKGLICSPSMPXV3jgSAsCE7I1a9opdoe-W19V2N3OPJ0Z-cMY35URQxY0y9nP"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
