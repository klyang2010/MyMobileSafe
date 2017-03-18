package com.mymobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mrka.mymobilesafe.R;

/*手机防盗的4个设置向导界面的基类*/
public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector gd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initGesture();
        initData();
        initEvent();
    }

    public void initEvent() {
    }

    public void initData() {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initGesture() {
        gd = new GestureDetector(getApplicationContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                if (v > 50){
                    float e = motionEvent1.getX() - motionEvent.getX();
                    if (Math.abs(e) < 50 ){
                        return  false;
                    }else {
                        if (e < 0){
                            next(null);
                        }else {
                            prev(null);
                        }
                    }
                }
                return false;
            }
        });
    }

    public abstract void initView();

    public void next(View v) {
        nextActivity();
        nextAnimatinon();
    }

    public void prev(View v) {
        prevActivity();
        prevAnimatinon();
    }

    private void nextAnimatinon() {
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    private void prevAnimatinon() {
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    public void startActivity(Class type) {
        Intent intent = new Intent(BaseSetupActivity.this, type);
        startActivity(intent);
        finish();
    }


    protected abstract void prevActivity();

    protected abstract void nextActivity();
}
