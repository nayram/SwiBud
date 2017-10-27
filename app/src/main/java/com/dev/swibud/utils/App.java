package com.dev.swibud.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.main.Devless;

/**
 * Created by nayrammensah on 8/15/17.
 */

public class App extends Application {

    public static Devless devless;
    public static SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        sp= getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        devless = new Devless(this, Constants.AppUrl, Constants.token);
        Map config = new HashMap();
        config.put("cloud_name", "swibud");
        MediaManager.init(this, config);
        //devless.addUserToken(sp);

    }
}
