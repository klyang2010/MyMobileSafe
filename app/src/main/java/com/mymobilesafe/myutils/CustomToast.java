package com.mymobilesafe.myutils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.utils.MyConstants;

/**
 * Created by mrka on 17-2-8.
 */

public class CustomToast {

    /**
     * 弹出自定义吐司
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showLocationToast(final Context context, String text, final long duration) {
        /**
         *初始化吐司
         */
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = (int) Float.parseFloat(SpTools.getString(context, MyConstants.TOASTXX, "0"));
        layoutParams.y = (int) Float.parseFloat(SpTools.getString(context, MyConstants.TOASTYY, "0"));

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//吐司天生不响应事件
        layoutParams.setTitle("Toast");

        final View view_toast = View.inflate(context, R.layout.sys_toast, null);

        /**
         * 拖动土司
         */
        view_toast.setOnTouchListener(new View.OnTouchListener() {

            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        view_toast.measure(0, 0);
                        if (layoutParams.x < 0) {
                            layoutParams.x = 0;
                        } else if (layoutParams.x + view_toast.getMeasuredWidth() > windowManager.getDefaultDisplay().getWidth()) {
                            layoutParams.x = windowManager.getDefaultDisplay().getWidth() - view_toast.getMeasuredWidth();
                        }

                        if (layoutParams.y < 0) {
                            layoutParams.y = 0;
                        } else if (layoutParams.y + view_toast.getMeasuredHeight() > windowManager.getDefaultDisplay().getHeight()) {
                            layoutParams.y = windowManager.getDefaultDisplay().getHeight() - view_toast.getMeasuredHeight();
                        }
                        System.out.println("dddd:"  );
                        SpTools.putString(context, MyConstants.TOASTXX, layoutParams.x + "");
                        SpTools.putString(context, MyConstants.TOASTYY, layoutParams.y + "");
                        break;
                    case MotionEvent.ACTION_UP:// 松开
                        //记录当前土司位置,把x y坐标值保存到sp中
                        view_toast.measure(0, 0);
                        if (layoutParams.x < 0) {
                            layoutParams.x = 0;
                        } else if (layoutParams.x + view_toast.getMeasuredWidth() > windowManager.getDefaultDisplay().getWidth()) {
                            layoutParams.x = windowManager.getDefaultDisplay().getWidth() - view_toast.getMeasuredWidth();
                        }

                        if (layoutParams.y < 0) {
                            layoutParams.y = 0;
                        } else if (layoutParams.y + view_toast.getMeasuredHeight() > windowManager.getDefaultDisplay().getHeight()) {
                            layoutParams.y = windowManager.getDefaultDisplay().getHeight() - view_toast.getMeasuredHeight();
                        }
                        SpTools.putString(context, MyConstants.TOASTXX, layoutParams.x + "");
                        SpTools.putString(context, MyConstants.TOASTYY, layoutParams.y + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        TextView tv_location = (TextView) view_toast.findViewById(R.id.tv_toast_location);
        tv_location.setText(text);
        windowManager.addView(view_toast, layoutParams);


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (view_toast != null) {
                    windowManager.removeViewImmediate(view_toast);
                }
                super.handleMessage(msg);
            }
        };


        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(duration);
                handler.obtainMessage().sendToTarget();
                super.run();
            }
        }.start();
    }

}
