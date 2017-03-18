package com.mymobilesafe.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.receiver.DeviceAdminSample;
import com.mymobilesafe.services.LostFindService;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.ServiceUtils;
import com.mymobilesafe.utils.SpTools;



public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_isprotected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initData() {
        if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.mymobilesafe.services.LostFindService")) {
            cb_isprotected.setChecked(true);
        } else {
            cb_isprotected.setChecked(false);
        }
    }

    @Override
    public void initEvent() {
        cb_isprotected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            private AlertDialog dialog;

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cb_isprotected.isChecked()) {
                    SpTools.putBoolean(getApplicationContext(), MyConstants.ISLOSTFIND, true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Setup4Activity.this);
                    builder.setMessage("开启远程锁屏需要打开设备管理员");
                    builder.setPositiveButton("确定打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            ComponentName admin = new ComponentName(Setup4Activity.this, DeviceAdminSample.class);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "开启设备管理员");
                            startActivityForResult(intent, 1);
                            dialog.dismiss();
                            enterLostFindService();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            enterLostFindService();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                } else {
                    SpTools.putBoolean(getApplicationContext(), MyConstants.ISLOSTFIND, false);
                    Intent intent = new Intent(Setup4Activity.this, LostFindService.class);
                    stopService(intent);
                }
            }

            private void enterLostFindService() {
                Intent intent = new Intent(Setup4Activity.this, LostFindService.class);
                startService(intent);
            }
        });
    }

    public void initView() {
        setContentView(R.layout.activity_setup4);
        cb_isprotected = (CheckBox) findViewById(R.id.cb_setup4_isprotected);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup3Activity.class);
    }

    @Override
    protected void nextActivity() {
        SpTools.putBoolean(getApplicationContext(), MyConstants.ISSETUP, true);
        startActivity(LostFindActivity.class);
    }
}
