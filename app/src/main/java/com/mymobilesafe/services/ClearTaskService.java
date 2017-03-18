package com.mymobilesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by mrka on 17-2-12.
 */

public class ClearTaskService extends Service {

    private ClearTaskReceive clearTaskReceive;
    private ActivityManager activityManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ClearTaskReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                activityManager.killBackgroundProcesses(info.processName);
            }

        }
    }

    @Override
    public void onCreate() {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        clearTaskReceive = new ClearTaskReceive();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(clearTaskReceive, intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(clearTaskReceive);
        super.onDestroy();
    }
}
