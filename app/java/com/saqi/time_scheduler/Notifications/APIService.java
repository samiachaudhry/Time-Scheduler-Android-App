package com.saqi.time_scheduler.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAafA73R8:APA91bGmG3-Ckybnd5ViJKuKIJgFGMOi4ev9_GuxRQzlfCyYILVUhwXKxe3rumQ63sfomoZtZDmV0_aVgfJJ2xmc_b4-ZdqOW7r9NcyrId1Ke69lWj4NpcESaQwldLjc2WT61YalH1wK"
            }
    )
    @POST("fcm/send")
    Call<MessageResponseModel> sendNotifcation(@Body NotificationSenderModel body);
}