package com.bewtechnologies.writingpromptstwo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by ab on 28/02/18.
 */

public  class CancelNotifReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.i(TAG, "onReceive: "+intent);
            if (intent.getExtras().getBoolean("cancel_notif")) {
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.cancel(1001);
            }
        }
    }
}
