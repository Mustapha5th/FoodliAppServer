package com.example.foodliappserver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Helper.NotificationHelper;
import com.example.foodliappserver.MainActivity;
import com.example.foodliappserver.R;
import com.example.foodliappserver.Screens.ui.OrderStatusFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            sendNotificationAPI26(remoteMessage);
        }else{

            sendNotification(remoteMessage);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String  title = notification.getTitle();
        String content = notification.getBody();

        Intent intent = new Intent(this, OrderStatusFragment.class);
        intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder = helper.getFoodliChannelNotification(title,content,pendingIntent,defaultSoundUri);
       // Get Random id for notification to show all  notification
        helper.getManager().notify(new Random().nextInt(),builder.build());


    }

    private void sendNotification(RemoteMessage remoteMessage) {
        int requestID = (int) System.currentTimeMillis();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }
}
