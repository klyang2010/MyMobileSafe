package com.mrka.widgetdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 在这里可以写 管理控件的代码
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, ExampleAppWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews("com.mrka.widgetdemo", R.layout.example_appwidget);

        appWidgetManager.updateAppWidget(provider, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
