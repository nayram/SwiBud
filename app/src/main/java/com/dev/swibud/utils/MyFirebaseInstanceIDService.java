package com.dev.swibud.utils;

import android.util.Log;
import android.view.View;

import com.dev.swibud.pojo.ExtraUserProfile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.EditDataResponse;
import androidsdk.devless.io.devless.interfaces.PostDataResponse;
import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;

/**
 * Created by nayrammensah on 12/14/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
       final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        SendBird.registerPushTokenForCurrentUser(refreshedToken, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus ptrs, SendBirdException e) {
                if (e != null) {
                    return;
                }

                if (ptrs == SendBird.PushTokenRegistrationStatus.PENDING) {
                    // Try registering the token after a connection has been successfully established.

                }else if (ptrs==SendBird.PushTokenRegistrationStatus.SUCCESS){


                }
            }
        });
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        checkUserIdAvailability(token);

    }


    void updateService(JSONObject proile,String token){
        Map<String, Object> params = new HashMap<>();
        params.put("users_id",GeneralFunctions.getUserId());
        params.put("fcm_reg_id",token);
        try {
            int id=proile.getInt("id");
            App.devless.edit("devless", "user_profile", params,String.valueOf(id), new EditDataResponse() {
                @Override
                public void onSuccess(ResponsePayload response) {

                    Log.d(TAG,response.toString());
                }

                @Override
                public void onFailed(ErrorMessage errorMessage) {

                }

                @Override
                public void userNotAuthenticated(ErrorMessage message) {

                    Log.d(TAG,message.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void checkUserIdAvailability(final String token){

//        Toast.makeText(ctx, "Check UserId Availability", Toast.LENGTH_SHORT).show();
        Map<String, Object> params = new HashMap<>();
        params.put("where","users_id,"+GeneralFunctions.getUserId());

        App.devless.search("devless", "user_profile", params, new SearchResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {


                try {
                    JSONObject jsonObject=new JSONObject(response.toString());
                    String userobjParam=GeneralFunctions.userExtraDet();
                    if (userobjParam==null){
                        if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){

                        }
                    }
                    if (jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).length()>0){
//                        Toast.makeText(ctx, "Update Location Success", Toast.LENGTH_SHORT).show();
                        JSONObject profile=jsonObject.getJSONObject(Constants.Payload).getJSONArray(Constants.Result).getJSONObject(0);
                        updateService(profile,token);
                    }else{
//                        Toast.makeText(ctx, "Save Location Success", Toast.LENGTH_SHORT).show();
                        saveToService(token);
                    }

                } catch (JSONException e) {
                    //hideProgress();
                    e.printStackTrace();

                }
            }



            @Override
            public void userNotAuthenticated(ErrorMessage errorMessage) {
                Log.d(TAG,errorMessage.toString());
            }
        });
    }

    void saveToService(String token){
        Map<String, Object> params = new HashMap<>();
        params.put("users_id",String.valueOf(GeneralFunctions.getUserId()));
        params.put("fcm_reg_id",token);
        Log.d(TAG,params.toString());
        App.devless.postData("devless", "user_profile", params, new PostDataResponse() {
            @Override
            public void onSuccess(ResponsePayload response) {
                //hideProgress();
                Log.d(TAG,response.toString());

            }

            @Override
            public void onFailed(ErrorMessage errorMessage) {
                //hideProgress();
                Log.d(TAG,errorMessage.toString());
            }

            @Override
            public void userNotAuthenticated(ErrorMessage message) {

                Log.d(TAG,message.toString());
            }
        });
    }
}
