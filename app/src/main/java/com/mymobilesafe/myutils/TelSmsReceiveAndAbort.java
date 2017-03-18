package com.mymobilesafe.myutils;

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
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.mymobilesafe.dao.BlackDao;
import com.mymobilesafe.domain.BlackTable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*黑名单拦截（短信拦截和电话拦截）的服务*/

public class TelSmsReceiveAndAbort extends Service {

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
                        if ((mode & BlackTable.TEL) != 0){
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

    /**根据内容提供者来删除电话日志*/
    private void deleteCalllogs(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }

    /**  通过反射和aidl来挂断电话  */
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
