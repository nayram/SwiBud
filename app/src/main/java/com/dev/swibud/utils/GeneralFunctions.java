package com.dev.swibud.utils;




import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dev.swibud.R;
import com.dev.swibud.activities.MainActivity;
import com.dev.swibud.pojo.Users;

/**
 * Created by nayrammensah on 8/3/17.
 */

public class GeneralFunctions {

    String sharedPrefs="SharedPrefs";

    public static void addFragmentFromRight(FragmentManager fragmentManager, Fragment fragment,
                                            int containerId) {
        fragmentManager.beginTransaction()
                .add(containerId, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName()).commit();
    }

    public static void addFragmentFromRightWithTag(FragmentManager fragmentManager, Fragment fragment,
                                                   int containerId, String tag) {
        fragmentManager.beginTransaction()
                .add(containerId, fragment, tag)
                .addToBackStack(null).commit();
    }

    public static void saveUser(String user,Context ctx){
        SharedPreferences sharedPreferences= App.sp;
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Constants.User,user);
        editor.apply();
    }

    public static String getUser(Context ctx){
        SharedPreferences sharedPreferences=App.sp;

        return sharedPreferences.getString(Constants.User,null);
    }

    public static void saveToken(String token){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.Token,token);
        editor.apply();
    }

    public static void saveUserImage(String image,Context ctx){
        SharedPreferences sharedPreferences= App.sp;
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Constants.IMAGE,image);
        editor.apply();
    }

    public static void logout(Context ctx){
        SharedPreferences sharedPreferences= App.sp;
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserImage(Context ctx) {
        SharedPreferences sharedPreferences=App.sp;

        return sharedPreferences.getString(Constants.IMAGE,null);
    }

    public static void setUserId(Context ctx,int id){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(Constants.USERID,id);
        editor.apply();
    }

    public static int getUserId(){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getInt(Constants.USERID,0);
    }



    public static void setLatitude(Context ctx,float lat){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat(Constants.LAT,lat);
        editor.apply();
    }

    public static void setLongitude(Context ctx,float lng){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat(Constants.LNG,lng);
        editor.apply();
    }

    public static float getLongitude(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getFloat(Constants.LNG,0);
    }

    public static float getLatitude(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getFloat(Constants.LAT,0);
    }

    public static void setUserExtraDetail(Context ctx,String profile){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.USER_EXTRA_DETAIL,profile);
        editor.apply();
    }

    public static void addUserExtraDetail(String profile){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.USER_EXTRA_DETAIL,profile);
        editor.apply();
    }

    public static String getUserExtraDetail(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getString(Constants.USER_EXTRA_DETAIL,null);
    }

    public static String userExtraDet(){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getString(Constants.USER_EXTRA_DETAIL,null);
    }

    public static boolean isSendBirdConnected(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getBoolean(Constants.SENDBIRD_CONNECT,false);
    }

    public static void setSendBirdConnect(Context ctx,boolean connect){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.SENDBIRD_CONNECT,connect);
        editor.apply();
    }

    public static boolean isSendBirdLogin(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getBoolean(Constants.SENDBIRD_LOGIN,false);
    }

    public static void setSendBirdLogin(Context ctx,boolean status){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.SENDBIRD_LOGIN,status);
        editor.apply();
    }

    public static String getSendBirdAccessToken(Context ctx){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getString(Constants.SENDBIRD_ACCESS_TOKEN,null);
    }

    public static void setSendBirdAccessToken(Context ctx,String token){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.SENDBIRD_ACCESS_TOKEN,token);
        editor.apply();
    }

    public static void setChatUrl(String title,String chatUrl){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(title,chatUrl);
        editor.apply();
    }

    public static String getChatUrl(String title){
        return App.sp.getString(title,null);
    }

    public static void setFCMToken(String token){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.FCM_TOKEN,token);
        editor.apply();
    }

    public static String getFCMToken(){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getString(Constants.FCM_TOKEN,null);

    }

    public static boolean isVisible(){
        SharedPreferences sharedPreferences=App.sp;
        return sharedPreferences.getBoolean(Constants.VISIBLILTY,false);
    }

    public static void setVisibility(boolean visibility){
        SharedPreferences sharedPreferences=App.sp;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.VISIBLILTY,visibility);
        editor.apply();
    }

}
