package com.mymobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.engine.SmsBackupResumeEngine;

/**
 * Created by mrka on 17-2-7.
 */

public class AToolsActivity extends AppCompatActivity {

    private ProgressBar pb_smsBackup;
    private ProgressBar pb_smsResume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void smsResume(View v){
        SmsBackupResumeEngine.SmsResumeJson(this, new SmsBackupResumeEngine.BackupProgress() {
            @Override
            public void show() {
                pb_smsResume.setVisibility(View.VISIBLE);
            }

            @Override
            public void setMax(int max) {
                pb_smsResume.setMax(max);
            }

            @Override
            public void setProgress(int progress) {
                pb_smsResume.setProgress(progress);
            }

            @Override
            public void end() {
                pb_smsResume.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 手机短信备份
     */
    public void smsBackup(View v) {

        SmsBackupResumeEngine.SmsBackupJson(AToolsActivity.this, new SmsBackupResumeEngine.BackupProgress() {
            @Override
            public void show() {
                pb_smsBackup.setVisibility(View.VISIBLE);
            }

            @Override
            public void setMax(int max) {
                pb_smsBackup.setMax(max);
            }

            @Override
            public void setProgress(int progress) {
                pb_smsBackup.setProgress(progress);
            }

            @Override
            public void end() {
                pb_smsBackup.setVisibility(View.GONE);
            }


        });
    }

    /**
     * 手机归属地查询
     * @param v 点击事件
     */
    public void phoneQuery(View v) {
        Intent intent = new Intent(AToolsActivity.this, PhoneLocationActivity.class);
        startActivity(intent);
    }

    /**
     * 程序锁
     * @param v
     */
    public void lockActivity(View v){
        Intent intent = new Intent(AToolsActivity.this, LockedActivity.class);
        startActivity(intent);
    }

    private void initView() {
        setContentView(R.layout.activity_atools);
        pb_smsBackup = (ProgressBar) findViewById(R.id.pb_smsbackup_progress);
        pb_smsResume = (ProgressBar) findViewById(R.id.pb_smsresume_progress);
    }
}
