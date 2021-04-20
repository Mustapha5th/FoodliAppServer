package com.example.foodliappserver.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.foodliappserver.R;

public class NotificationHelper extends ContextWrapper {

    public static final String FOODLI_CHANNEL_ID = "com.example.foodliapp";
    public static final String FOODLI_CHANNEL_NAME = "FoodliApp";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){  //only use this function if ApI is 26 or higher
            createChannel();

        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foodliChannel = new NotificationChannel(FOODLI_CHANNEL_ID,
                    FOODLI_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            foodliChannel.enableLights(false);
            foodliChannel.enableVibration(true);
            foodliChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            
            getManager().createNotificationChannel(foodliChannel);
        }
    }

   public NotificationManager getManager() {
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getFoodliChannelNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(), FOODLI_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
