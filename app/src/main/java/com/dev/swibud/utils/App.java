package com.dev.swibud.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cloudinary.android.MediaManager;
import com.crashlytics.android.Crashlytics;
import com.dev.swibud.R;
import com.sendbird.android.SendBird;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.main.Devless;
import io.realm.Realm;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nayrammensah on 8/15/17.
 */

public class App extends MultiDexApplication {

    public static Devless devless;
    public static SharedPreferences sp;
    public static SwibudServices swibudServices;
    public static MessagingService messagingService;


//    public static Configuration.Builder builder;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Context context=getApplicationContext();
        devless = new Devless(this, Constants.AppUrl, Constants.token);
        sp= getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        Map config = new HashMap();
        config.put("cloud_name", "swibud");
        MediaManager.init(this, config);
        Realm.init(this);
        SendBird.init("96E3CA61-7EB5-4463-90AB-8399D6C12524", context);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);
        OkHttpClient.Builder fcmClient = new OkHttpClient.Builder();

        fcmClient.addInterceptor(logging);

        OkHttpClient fcmHttpClient = fcmClient.build();


        try{
            httpClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader("Api-Token", "0f237906255518390807fc71bdc921de6283b84b").build();
                    return chain.proceed(request);
                }
            });

        }catch (UnsupportedOperationException ex){
            ex.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SwibudServices.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        swibudServices=retrofit.create(SwibudServices.class);

        Retrofit fcmRetrofit=new Retrofit.Builder()
                .baseUrl(MessagingService.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .client(fcmHttpClient)
                .build();

        messagingService=fcmRetrofit.create(MessagingService.class);


    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
