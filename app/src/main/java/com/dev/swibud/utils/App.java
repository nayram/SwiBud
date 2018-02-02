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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nayrammensah on 8/15/17.
 */

public class App extends MultiDexApplication {

    public static Devless devless;
    public static SharedPreferences sp;
    public static SwibudServices swibudServices;

//    public static Configuration.Builder builder;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Context context=getApplicationContext();
        sp= getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        devless = new Devless(this, Constants.AppUrl, Constants.token);
        Map config = new HashMap();
        config.put("cloud_name", "swibud");
        MediaManager.init(this, config);
        Realm.init(this);
        SendBird.init("96E3CA61-7EB5-4463-90AB-8399D6C12524", context);

        OkHttpClient httpClient = new OkHttpClient();

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
                .client(httpClient)
                .build();

        swibudServices=retrofit.create(SwibudServices.class);
/*
        Configuration.Builder builder = new Configuration.Builder(context);
        builder.firebaseRootPath("prod");
        builder.firebase("prod",getResources().getString(R.string.firebase_server_key));
        builder.facebookLoginEnabled(false)
                .twitterLoginEnabled(false)
                .googleLoginEnabled(false);
        ChatSDK.initialize(builder.build());
        UserInterfaceModule.activate(getApplicationContext());

        FirebaseModule.activate();
        FirebaseFileStorageModule.activate();*/


    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
