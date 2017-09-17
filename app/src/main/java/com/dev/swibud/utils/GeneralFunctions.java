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
        SharedPreferences sharedPreferences= ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Constants.User,user);
        editor.commit();
    }

    public static String getUser(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);

        return sharedPreferences.getString(Constants.User,null);
    }

    public static void saveToken(String token, Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.Token,token);
        editor.commit();
    }

    public static void saveUserImage(String image,Context ctx){
        SharedPreferences sharedPreferences= ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Constants.IMAGE,image);
        editor.commit();
    }

    public static void logout(Context ctx){
        SharedPreferences sharedPreferences= ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static String getUserImage(Context ctx) {
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);

        return sharedPreferences.getString(Constants.IMAGE,null);
    }

    public static void setUserId(Context ctx,int id){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(Constants.USERID,id);
        editor.commit();
    }

    public static int getUserId(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(ctx.getPackageName(),Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.USERID,0);
    }
}
