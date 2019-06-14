package com.example.mdkashif.new_fire;


import com.example.mdkashif.new_fire.Notification.MyResponse;
import com.example.mdkashif.new_fire.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAi6xhl80:APA91bH6bdVdcmi22C82gTECBzBtAzuCqoaHd2Me1Js5xojnrKtmRzaPwxigXKOppXcfnjS-E7yGUSbuiFltjRw-0SK81H-G8HNGdIt3tmROMkdnBLEHd2WvaGog-vZRLacyxYhy2adZ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
