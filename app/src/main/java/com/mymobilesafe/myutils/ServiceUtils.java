package com.mymobilesafe.myutils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by mrka on 17-2-3.
 */

public class ServiceUtils {
    public static boolean isServiceRunning(Context context, String service) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo running : runningServices) {
            String className = running.service.getClassName();
            if (className.equals(service)){
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
