package com.example.comg3.toomuchstuff;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by comg3 on 19/05/2018.
 */



public class notificationReceiver extends BroadcastReceiver {
    public static int requestCode =100;


    @Override

    // on the receiving of the broadcast do the following
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //intent so when the notification is tapped the user is taken the the main activity
        Intent repeatIntent = new Intent(context, MainActivity.class);
        repeatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, repeatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "test";

        //creates the appearance of the notification in the notification screen
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("Too Much Stuff") //text and content of the notification
                .setContentText("Bought More Stuff? Add Your Stuff Now!")
                .setAutoCancel(true);
        if(intent.getAction().equals("MY_NOTIFICATION_MESSAGE")){
            // if the action of the intents are equal then build the notification
            notificationManager.notify(requestCode,builder.build());
        }


    }
}
