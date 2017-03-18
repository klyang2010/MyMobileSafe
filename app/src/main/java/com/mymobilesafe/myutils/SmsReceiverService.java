package com.mymobilesafe.myutils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

/**
 * Created by mrka on 17-2-3.
 */

public class SmsReceiverService extends Service {
    private SmsReceiver smsReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object pdus[] = (Object[]) extras.get("pdus");
            for (Object data :
                    pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
            }

        }
    }

    @Override
    public void onCreate() {
        smsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }
}
