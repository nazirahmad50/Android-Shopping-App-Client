package com.nazir.shopping.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.nazir.shopping.R;

import java.net.URI;

public class NotificationHelper extends ContextWrapper {

    private final String APP_CHANEL_ID = "com.nazir.shopping";
    private final String APP_CHANEL_NAME = "Shopping";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel(APP_CHANEL_ID, APP_CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {

        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundURI){

        return new Notification.Builder(getApplicationContext(), APP_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.magix_logo)
                .setSound(soundURI)
                .setAutoCancel(false);

    }
}
