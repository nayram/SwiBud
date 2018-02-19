package com.dev.swibud.utils;

import com.dev.swibud.pojo.RootData;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by nayrammensah on 2/13/18.
 */

public interface MessagingService {
    public static final String domain ="https://swibud-firebase.herokuapp.com/";

    @POST("send")
    Call<JSONObject> sendDeviceSpecificMessage(@Body RootData body);

    @GET("api.php")
    Call<JSONObject> sendFCMNotificationMeetupInvite(@Query("type") String type, @Query("title") String title,
                                                     @Query("message") String message, @Query("reg_id") String regid,
                                                     @Query("meetup_id") String meetupId);

}
