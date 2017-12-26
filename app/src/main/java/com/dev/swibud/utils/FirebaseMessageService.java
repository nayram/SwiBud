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

import com.dev.swibud.R;
import com.dev.swibud.activities.OpenChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.shadow.com.google.gson.JsonElement;
import com.sendbird.android.shadow.com.google.gson.JsonParser;

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
        Log.d(TAG, String.valueOf(remoteMessage.getData()));
        try {

            JsonElement payload = new JsonParser().parse(remoteMessage.getData().get("sendbird"));

            sendNotification(message, payload);
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    private void sendNotification(String message, JsonElement payload) {
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



    private void scheduleJob() {

    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }



}
