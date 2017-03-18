package com.mymobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.services.ClearTaskService;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.ServiceUtils;
import com.mymobilesafe.utils.SpTools;

/**
 * Created by mrka on 17-2-12.
 */

public class TastManagerSettingActivity extends AppCompatActivity {

    private CheckBox cb_lockscreenClear;
    private CheckBox cb_showsystemapp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        //通过标记来初始化是否显示系统进程的状态
        cb_showsystemapp.setChecked(SpTools.getBooean(this, MyConstants.SHOWSYSTEM, false));
        //通过清理进程的服务判断 是否开启
        cb_lockscreenClear.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.mymobilesafe.services.ClearTaskService"));
    }

    private void initEvent() {
        cb_showsystemapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpTools.putBoolean(getApplicationContext(), MyConstants.SHOWSYSTEM, isChecked);
            }
        });
        cb_lockscreenClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent service = new Intent(TastManagerSettingActivity.this,ClearTaskService.class);
                    startService(service);
                } else {
                    Intent service = new Intent(TastManagerSettingActivity.this,ClearTaskService.class);
                    stopService(service);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_taskmanagersettingcenter);

        //锁屏清理
        cb_lockscreenClear = (CheckBox) findViewById(R.id.cb_taskmanager_settingcenter_lockscree_clear);

        //显示系统进程
        cb_showsystemapp = (CheckBox) findViewById(R.id.cb_taskmanager_settingcenter_lockscree_showsystemapp);

    }
}
