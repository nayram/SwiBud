package com.dev.swibud.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cloudinary.android.MediaManager;
import com.dev.swibud.R;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.main.Devless;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseModule;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.ui.manager.UserInterfaceModule;
import io.realm.Realm;

/**
 * Created by nayrammensah on 8/15/17.
 */

public class App extends MultiDexApplication {

    public static Devless devless;
    public static SharedPreferences sp;
//    public static Configuration.Builder builder;
    @Override
    public void onCreate() {
        super.onCreate();
        Context context=getApplicationContext();
        sp= getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        devless = new Devless(this, Constants.AppUrl, Constants.token);
        Map config = new HashMap();
        config.put("cloud_name", "swibud");
        MediaManager.init(this, config);
        Realm.init(this);
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
