package com.mymobilesafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.services.ComingPhoneService;
import com.mymobilesafe.services.TelSmsBlackService;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.ServiceUtils;
import com.mymobilesafe.utils.SpTools;
import com.mymobilesafe.view.SettingCenterItemView;
/*设置中心主界面*/


public class SettingCenterActivity extends AppCompatActivity {
    private SettingCenterItemView scv_autoupdate;
    private SettingCenterItemView scv_blackservice;
    private SettingCenterItemView sciv_phoneLocationService;
    private RelativeLayout rl_style_root;
    private TextView tv_locationStyle_content;
    private ImageView iv_locationSytle_click;
    private String[] styleNames = new String[]{"卫士蓝", "金属灰", "苹果绿", "活力橙",
            "半透明"};
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        sciv_phoneLocationService.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.mymobilesafe.services.ComingPhoneService"));
        scv_blackservice.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.mymobilesafe.services.TelSmsBlackService"));
        scv_autoupdate.setChecked(SpTools.getBooean(getApplicationContext(), MyConstants.ISAUTOUPDATE, false));
    }

    private void initEvent() {
        // 归属地根布局点击事件
        rl_style_root.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //showLocationToast(getApplicationContext(), "biti", 10000);
                /*switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                        iv_locationSytle_click
                                .setImageResource(R.drawable.jiantou1_pressed);
                        break;
                    case MotionEvent.ACTION_UP:// 松开
                        iv_locationSytle_click
                                .setImageResource(R.drawable.jiantou1_disable);
                        // 显示选择归属地样式的对话框
                        showStyleDialog();
                        break;
                    default:
                        break;
                }*/
                return false;
            }
        });

        //手机归属地服务的开启和关闭
        sciv_phoneLocationService.setOnItemClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 判断黑名单拦截服务是否运行
                if (ServiceUtils.isServiceRunning(getApplicationContext(),
                        "com.mymobilesafe.services.ComingPhoneService")) {
                    // 服务在运行,关闭服务
                    Intent comingPhoneService = new Intent(
                            SettingCenterActivity.this,
                            ComingPhoneService.class);
                    stopService(comingPhoneService);
                    // 设置复选框的状态
                    sciv_phoneLocationService.setChecked(false);
                } else {
                    // 服务停止,打开服务
                    Intent comingPhoneService = new Intent(
                            SettingCenterActivity.this,
                            ComingPhoneService.class);
                    startService(comingPhoneService);
                    // 设置复选框的状态
                    sciv_phoneLocationService.setChecked(true);
                }
            }
        });

        //黑名单拦截服务的监听：开启和关闭
        scv_blackservice.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.mymobilesafe.services.TelSmsBlackService")) {
                    Intent blackService = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    stopService(blackService);
                    scv_blackservice.setChecked(false);
                } else {
                    Intent blackService = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    startService(blackService);
                    scv_blackservice.setChecked(true);
                }
            }
        });
        //自动更新的点击事件监听
        scv_autoupdate.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scv_autoupdate.setChecked(!scv_autoupdate.isChecked());
                SpTools.putBoolean(getApplicationContext(), MyConstants.ISAUTOUPDATE, scv_autoupdate.isChecked());
            }
        });
    }

    private void showStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingCenterActivity.this);
        builder.setTitle("选择归属地样式");
        builder.setSingleChoiceItems(styleNames, Integer.parseInt(SpTools.getString(getApplicationContext(), MyConstants.STYLEBGINDEX, "0")), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 保存sp中 字符串的方式保存归属地样式
                SpTools.putString(getApplicationContext(),
                        MyConstants.STYLEBGINDEX, which + "");
                tv_locationStyle_content.setText(styleNames[which]);
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        //自动更新
        scv_autoupdate = (SettingCenterItemView) findViewById(R.id.scv_settingcenter_autoupdate);
        //黑名单拦截服务
        scv_blackservice = (SettingCenterItemView) findViewById(R.id.scv_settingcenter_blackservice);
        //手机号码归属地服务
        sciv_phoneLocationService = (SettingCenterItemView) findViewById(R.id.scv_settingcenter_phonelocationservice);
        // 归属地样式的根布局
        rl_style_root = (RelativeLayout) findViewById(R.id.rl_settingcenter_locationsytle_root);
        // 归属地样式的名字
        tv_locationStyle_content = (TextView) findViewById(R.id.tv_settingcenter_locationsytle_content);
        // 点击图片按钮来显示样式选择对话框
        iv_locationSytle_click = (ImageView) findViewById(R.id.iv_settingcenter_locationsytle_select);
    }

}
