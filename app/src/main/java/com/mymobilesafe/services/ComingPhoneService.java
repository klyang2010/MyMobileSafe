package com.mymobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.engine.PhoneLocationEngine;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;

/**
 * Created by mrka on 17-2-8.
 */

public class ComingPhoneService extends Service {
    private WindowManager windowManager;
    private View view_toast;
    private WindowManager.LayoutParams layoutParams;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    int bgStyles[] = new int[]{R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green,R.drawable.call_locate_orange,R.drawable.call_locate_white};
    private boolean isOutCall;
    private OutCallReceiver outCallReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class OutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String phoneNumber = getResultData();
            isOutCall = true;
            showLocationToast(phoneNumber);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            closeLocationToast();
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        //注册外拨电话的广播接受者
        outCallReceiver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outCallReceiver, intentFilter);

        //初始化自定义吐司的参数
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        initToastParams();

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //空闲状态
                    case TelephonyManager.CALL_STATE_IDLE:
                        closeLocationToast();
                        break;
                    //响铃状态
                    case TelephonyManager.CALL_STATE_RINGING:
                        showLocationToast(incomingNumber);
                        break;
                    //挂断状态
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (isOutCall){
                            isOutCall = false;
                            new Thread(){
                                @Override
                                public void run() {
                                    SystemClock.sleep(5000);
                                    handler.obtainMessage().sendToTarget();
                                    super.run();
                                }
                            }.start();
                        }else {
                            closeLocationToast();
                        }
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private void initToastParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = (int) Float.parseFloat(SpTools.getString(getApplicationContext(), MyConstants.TOASTX, "0"));
        layoutParams.y = (int) Float.parseFloat(SpTools.getString(getApplicationContext(), MyConstants.TOASTY, "0"));

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//吐司天生不响应事件
        layoutParams.setTitle("Toast");
    }

    private void closeLocationToast() {
        if (view_toast != null) {
            windowManager.removeView(view_toast);
            view_toast = null;
        }
    }

    private void showLocationToast(String incomingNumber) {

        //如果同时有两个电话打进来，在显示另一个他吐司的时候，需要先关闭第一个电话的吐司
        closeLocationToast();

        view_toast = View.inflate(getApplicationContext(), R.layout.sys_toast, null);
        int position = Integer.parseInt(SpTools.getString(getApplicationContext(), MyConstants.STYLEBGINDEX, "0"));
        view_toast.setBackgroundResource(bgStyles[position]);
        view_toast.setOnTouchListener(new View.OnTouchListener() {

            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拖动土司
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 按下移动，拖动
                        //新的 x  y坐标
                        float moveX = event.getRawX();//移动后的x坐标
                        float moveY = event.getRawY();//移动后的x坐标

                        //dx x方向的位置变化值 dy y方向的位置变化值
                        float dx = moveX - startX;
                        float dy = moveY - startY;
                        //改变土司的坐标
                        layoutParams.x += dx;
                        layoutParams.y += dy;
                        //重新获取新的x y坐标
                        startX = moveX;
                        startY = moveY;

                        //更新土司的位置
                        windowManager.updateViewLayout(view_toast, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:// 松开
                        //记录当前土司位置,把x y坐标值保存到sp中
                        if (layoutParams.x < 0) {
                            layoutParams.x = 0;
                        } else if (layoutParams.x + view_toast.getWidth() > windowManager.getDefaultDisplay().getWidth()) {
                            layoutParams.x =  windowManager.getDefaultDisplay().getWidth() - view_toast.getWidth();
                        }

                        if (layoutParams.y < 0) {
                            layoutParams.y = 0;
                        } else if (layoutParams.y + view_toast.getHeight() > windowManager.getDefaultDisplay().getHeight()) {
                            layoutParams.y = windowManager.getDefaultDisplay().getHeight() - view_toast.getHeight();
                        }
                        SpTools.putString(getApplicationContext(), MyConstants.TOASTX, layoutParams.x + "");
                        SpTools.putString(getApplicationContext(), MyConstants.TOASTY, layoutParams.y + "");

                    default:
                        break;
                }
                return false;
            }
        });

        TextView tv_location = (TextView) view_toast.findViewById(R.id.tv_toast_location);
        tv_location.setText(PhoneLocationEngine.locationQuery(getApplicationContext(), incomingNumber));
        windowManager.addView(view_toast, layoutParams);
    }

    @Override
    public void onDestroy() {
        if (view_toast != null) {
            windowManager.removeView(view_toast);
            view_toast = null;
        }
        unregisterReceiver(outCallReceiver);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}
