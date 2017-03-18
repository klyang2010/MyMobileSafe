package com.mymobilesafe.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.mrka.mymobilesafe.R;
import com.mymobilesafe.domain.BlackTable;
import com.mymobilesafe.dao.BlackDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*黑名单拦截（短信拦截和电话拦截）的服务*/
public class TelSmsBlackService extends Service {

    private SmsReceive receiver;
    private BlackDao blackDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SmsReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object pdus[] = (Object[]) extras.get("pdus");
            for (Object data :
                    pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
                String originatingAddress = smsMessage.getOriginatingAddress();
                int mode = blackDao.getMode(originatingAddress);
                if ((mode & BlackTable.SMS) != 0) {
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        //提高服务的运行级别，直接设置为前台进程
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this);
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher);
        notificationCompat.setContentTitle("黑名单拦截");
        notificationCompat.setContentText("手机状态：优");
        Intent intent = new Intent("com.it.homeactivity");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
        notificationCompat.setContentIntent(pendingIntent);

        notificationManager.notify(1, notificationCompat.build());

        /*Notification notification = new Notification();
        notification.iconLevel = R.mipmap.ic_launcher;
        Intent intent = new Intent("com.it.homeactivity");
        intent.addCategory("com.mobile.safe");
        notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);*/


        /*短信拦截*/
        blackDao = new BlackDao(getApplicationContext());
        receiver = new SmsReceive();
        // 短信广播意图
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        // 设置拦截模式为最高
        filter.setPriority(Integer.MAX_VALUE);
        // 注册短信广播
        registerReceiver(receiver, filter);

        /*电话拦截*/
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        int mode = blackDao.getMode(incomingNumber);
                        if ((mode & BlackTable.TEL) != 0) {
                            endCall();
                            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    deleteCalllogs(incomingNumber);
                                    super.onChange(selfChange);
                                }
                            });
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    /**
     * 根据内容提供者来删除电话日志
     */
    private void deleteCalllogs(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }

    /**
     * 通过反射和aidl来挂断电话
     */
    private void endCall() {
        try {
            Class clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
