package com.dev.swibud.utils;

/**
 * Created by nayrammensah on 12/14/17.
 */

import android.os.Bundle;
import android.util.Log;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dev.swibud.R;
import com.dev.swibud.activities.ActivityGuests;
import com.dev.swibud.activities.GuestStatusActivity;
import com.dev.swibud.activities.OpenChatActivity;
import com.dev.swibud.viewholders.MeetupViewHolder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.shadow.com.google.gson.JsonElement;
import com.sendbird.android.shadow.com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidsdk.devless.io.devless.interfaces.SearchResponse;
import androidsdk.devless.io.devless.messages.ErrorMessage;
import androidsdk.devless.io.devless.messages.ResponsePayload;

public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getData().get("message");
//        Toast.makeText(this, remoteMessage.getData().toString(), Toast.LENGTH_SHORT).show();


        try {
            Log.e(TAG,"Remote Message"+ String.valueOf(remoteMessage.getData()));
            if (remoteMessage.getData().containsKey("sendbird")){
                Log.d(TAG,"sendbird");
                JsonElement payload = new JsonParser().parse(remoteMessage.getData().get("sendbird"));

                sendChatNotification(message, payload);
            }else{
                Log.d(TAG,"Other Notificatopm");
                JSONObject rm = new JSONObject(remoteMessage.getData().toString());
                JSONObject data=rm.getJSONObject("data");
                JSONObject payload=data.getJSONObject("payload");
                Log.d(TAG,payload.getString("type"));
                switch (payload.getString("type")){
                    case Constants.MEETUP_INVITATION:
                            deployMeetupInvitation(data,payload);

                        break;
                }

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    private void sendChatNotification(String message, JsonElement payload) {
        // Your own way to show notifications to users.
        Intent intent=new Intent(this,OpenChatActivity.class);

        Bundle bundle=new Bundle();
        bundle.putString(Constants.EXTRA_NEW_CHANNEL_URL,payload.getAsJsonObject().get("channel").getAsJsonObject().get("channel_url").getAsString());
        bundle.putString("name",payload.getAsJsonObject().get("sender").getAsJsonObject().get("name").getAsString());
        intent.putExtras(bundle);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = payload.getAsJsonObject().get("channel").getAsJsonObject().get("channel_url").getAsString();
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("SwiBud - "+payload.getAsJsonObject().get("sender").getAsJsonObject().get("name").getAsString())
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        sendBroadcast(intent);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }

    void deployMeetupInvitation(final JSONObject data, JSONObject payload){
        Log.d(TAG,"deploy Meetup Notification");
        try {

            String meetup_id=payload.getString("meetup_id");
            Map<String, Object> params = new HashMap<>();
            params.put("where", "id,"+String.valueOf(meetup_id));

            App.devless.search("GetMeetup", "profile", params, new SearchResponse() {
                @Override
                public void onSuccess(ResponsePayload response) {
                    try {
                        JSONObject object=new JSONObject(response.toString());
                        if (object.getInt("status_code")==1001){
                            JSONObject jsonObject= object.getJSONArray(Constants.Payload).getJSONObject(0);

                            if (GeneralFunctions.getUserId()==jsonObject.getInt("users_id")){
                                ActivityGuests.jsonArray=jsonObject.getJSONArray("participants");

                                Intent intent=new Intent(getApplicationContext(),ActivityGuests.class);

                                Bundle bundle=new Bundle();
                                bundle.putInt("meetup_id",jsonObject.getInt("id"));

                                intent.putExtras(bundle);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                try {
                                    launchNotication(Constants.MEETUP_INVITATION,data.getString("title"),data.getString("message"),pendingIntent);
                                }catch (JSONException ex){
                                    ex.printStackTrace();
                                }


                            }else{
                                GuestStatusActivity.meetup_json=jsonObject;
                                Intent intent=new Intent(getApplicationContext(),GuestStatusActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                try {
                                    launchNotication(Constants.MEETUP_INVITATION,data.getString("title"),data.getString("message"),pendingIntent);
                                }catch (JSONException ex){
                                    ex.printStackTrace();
                                }

                            }
                        }
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }


                }

                @Override
                public void userNotAuthenticated(ErrorMessage errorMessage) {

                }
            });
        }catch (JSONException ex){

        }

    }


    private void launchNotication(String channel,String title,String message, PendingIntent pendingIntent){
        Log.d(TAG,"launch Meetup Notification");
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channel )
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(2 /* ID of notification */, notificationBuilder.build());

    }



    private void scheduleJob() {

    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }



}
