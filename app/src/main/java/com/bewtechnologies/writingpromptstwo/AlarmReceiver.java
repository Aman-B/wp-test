package com.bewtechnologies.writingpromptstwo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by ab on 27/02/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    int MID =1001;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub



                //for tapping on notification to open activity

                Intent contentIntent = new Intent(context, MainActivity.class);
                contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentPendingIntent = PendingIntent.getActivity(context,0,contentIntent,0);


                //for dismiss action button
                Intent cancelIntent= new Intent(context,CancelNotifReceiver.class);
                cancelIntent.putExtra("cancel_notif", true);
                PendingIntent pendingCancelIntent =
                        PendingIntent.getBroadcast(context, 1001, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT) ;


                int MID=1001;
                long when = System.currentTimeMillis();

                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                //for action button "lets write"
                Intent notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notificationIntent.putExtra("by_notif",true);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.mipmap.ic_stat_wp)
                        .setContentTitle("Did you write today?")
                        .setContentText("How about a new prompt?").setSound(alarmSound)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Keep writing. That's the only way to be the best!"))
                        .setAutoCancel(true).setWhen(when)
                        .setContentIntent(contentPendingIntent)
                        .addAction(R.mipmap.ic_stat_wp,"Let's Write",pendingIntent)
                        .addAction(R.mipmap.ic_stat_wp,"Dismiss",pendingCancelIntent)

                        .setVibrate(new long[]{100, 100, 100, 100, 100});
                notificationManager.notify(MID, mNotifyBuilder.build());
                MID++;






    }

}
