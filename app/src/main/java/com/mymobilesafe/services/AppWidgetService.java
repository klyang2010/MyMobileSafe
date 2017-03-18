package com.mymobilesafe.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.engine.TaskManagerEngine;
import com.mymobilesafe.receiver.ExampleAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mrka on 17-2-12.
 */

public class AppWidgetService extends Service {

    private AppWidgetManager appWidgetManager;
    private Timer timer;
    private TimerTask task;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

 System.out.println("widgetclearservice:" );
        timer = new Timer();
        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        task = new TimerTask() {
            @Override
            public void run() {
                ComponentName provider = new ComponentName(getApplicationContext(), ExampleAppWidgetProvider.class);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);

                int size = TaskManagerEngine.getAllRunningTaskInfo(getApplicationContext()).size();
                long availMem = TaskManagerEngine.getAvailMemSize(getApplicationContext());
                String availMemStr = android.text.format.Formatter.formatFileSize(getApplicationContext(), availMem);

                Intent intent = new Intent("com.it.widget.cleartask");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);

                remoteViews.setTextViewText(R.id.process_count, "正在运行软件:" + size);
                remoteViews.setTextViewText(R.id.process_memory, "可用内存:" + availMemStr);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                appWidgetManager.updateAppWidget(provider, remoteViews);
            }
        };

        timer.schedule(task, 0, 1000 * 5);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (timer != null){
            timer.cancel();
            timer = null;
            task = null;
        }
        super.onDestroy();
    }
}
