package com.mymobilesafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mrka.mymobilesafe.R;




public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void prevActivity() {

    }

    @Override
    protected void nextActivity() {
        startActivity(Setup2Activity.class);
    }
}
