package com.mymobilesafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;



public class Setup2Activity extends BaseSetupActivity {

    private Button bt_bindsim;
    private ImageView iv_isbind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }


    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))){
            Toast.makeText(getApplicationContext(), "请先绑定SIM卡", Toast.LENGTH_LONG).show();
            return;
        }
        super.next(v);
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))){
            iv_isbind.setImageResource(R.drawable.unlock);
        }else {
            iv_isbind.setImageResource(R.drawable.lock_9);
        }
        super.initData();
    }

    @Override
    public void initEvent() {
        bt_bindsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
                    //获取SIM卡的信息
                    {
                        TelephonyManager tm = (TelephonyManager) Setup2Activity.this.getSystemService(TELEPHONY_SERVICE);
                        String simSerialNumber = tm.getSimSerialNumber();
                        SpTools.putString(getApplicationContext(), MyConstants.SIM, simSerialNumber);
                    }
                    //设置图标
                    {
                        iv_isbind.setImageResource(R.drawable.lock_9);
                    }
                } else {
                    SpTools.putString(getApplicationContext(), MyConstants.SIM, "");
                    {
                        iv_isbind.setImageResource(R.drawable.unlock);
                    }
                }
            }
        });
    }

    public void initView() {
        setContentView(R.layout.activity_setup2);
        iv_isbind = (ImageView) findViewById(R.id.iv_setup2_isbind);
        bt_bindsim = (Button) findViewById(R.id.bt_setup2_bindsim);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup1Activity.class);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup3Activity.class);
    }
}
