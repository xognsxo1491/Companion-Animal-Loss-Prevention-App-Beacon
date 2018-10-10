package com.example.taehun.totalmanager.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.taehun.totalmanager.MainActivity;
import com.example.taehun.totalmanager.R;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static  final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From" + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "message data payload " + remoteMessage.getData());
        }
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Notification Body : " + remoteMessage.getNotification().getBody());
        }
    }
    private void sendNotification(String messageBody){
        Log.d("MyFirebaseIDService", "recived message : " + messageBody);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM TEST")
                .setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(5000);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
