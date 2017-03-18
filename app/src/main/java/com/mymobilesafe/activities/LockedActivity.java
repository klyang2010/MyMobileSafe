package com.mymobilesafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.fragment.LockedFragment;
import com.mymobilesafe.fragment.UnlockedFragment;

/**
 * Created by mrka .
 */

public class LockedActivity extends FragmentActivity {

    private TextView tv_locked;
    private TextView tv_unlock;
    private FrameLayout fl_content;
    private Fragment lockedFragment;
    private Fragment unlockedFragment;
    private FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

                if (v.getId() == R.id.tv_lockedactivity_unlock) {
                    fragmentTransaction.replace(R.id.fl_lockedactivity_content, unlockedFragment);
                    tv_locked.setBackgroundResource(R.drawable.tab_right_default);//不按下
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);//按下
                } else {
                    fragmentTransaction.replace(R.id.fl_lockedactivity_content, lockedFragment);
                    tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);//按下
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_default);//默认 不按下
                }
                fragmentTransaction.commit();
            }
        };

        tv_locked.setOnClickListener(onClickListener);
        tv_unlock.setOnClickListener(onClickListener);
    }

    /*-----   Fragment的替换   -----*/
    private void initData() {
        supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_lockedactivity_content, unlockedFragment);
        fragmentTransaction.commit();
    }

    private void initView() {
        setContentView(R.layout.activity_lock);
        // 加锁的textview
        tv_locked = (TextView) findViewById(R.id.tv_lockedactivity_locked);

        // 加锁的textview
        tv_unlock = (TextView) findViewById(R.id.tv_lockedactivity_unlock);

        // 要替换成fragment的组件
        fl_content = (FrameLayout) findViewById(R.id.fl_lockedactivity_content);

        lockedFragment = new LockedFragment();
        unlockedFragment = new UnlockedFragment();
    }
}
