package com.dev.swibud.utils;




import com.dev.swibud.pojo.User;
import com.sendbird.android.SendBird;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by nayrammensah on 12/4/17.
 */

public interface SwibudServices {
    public static final String domain="https://api.sendbird.com/v3/";

    @Headers("Api-Token: 0f237906255518390807fc71bdc921de6283b84b")
    @POST("users")
    Call<User> registerSendBirdUser(@Body User user);

    @Headers("Api-Token: 0f237906255518390807fc71bdc921de6283b84b")
    @GET("users/{user_id}")
    Call<User> getCurrentUser(@Path("user_id") String user_id);


}
