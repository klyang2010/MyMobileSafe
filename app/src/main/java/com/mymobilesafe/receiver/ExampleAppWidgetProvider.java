package com.mymobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.mymobilesafe.services.AppWidgetService;

/**
 * 窗口小控件
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, AppWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, AppWidgetService.class);
        context.stopService(intent);
    }
}
