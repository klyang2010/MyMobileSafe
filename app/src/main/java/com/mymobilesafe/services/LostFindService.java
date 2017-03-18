package com.mymobilesafe.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;

/**
 * 手机防盗服务
 */

public class LostFindService extends Service {

    private SmsReceiver smsReceiver;
    private MediaPlayer player;
    private boolean isPlay;


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
                String messageBody = smsMessage.getMessageBody();
                Toast.makeText(getApplicationContext(), messageBody, Toast.LENGTH_SHORT).show();
                if (messageBody.equals("#*gps*#")) {
                    Intent service = new Intent(context, LocationService.class);
                    startService(service);
                    abortBroadcast();
                }else if(messageBody.equals("#*lockscreen*#")){
                    DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//                    policyManager.resetPassword("123", 0);
                    policyManager.lockNow();
                    abortBroadcast();
                }else if(messageBody.equals("#*wipedata*#")){
                    DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    policyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();
                }else if(messageBody.equals("#*music*#")){
                    if (isPlay == true){
                        return;
                    }
                    isPlay = true;

                    player = MediaPlayer.create(getApplicationContext(), R.raw.aaa);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            isPlay = false;
                        }
                    });
                }
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
        if (player != null){
            player.stop();
            player.release();
        }
        super.onDestroy();
    }
}
