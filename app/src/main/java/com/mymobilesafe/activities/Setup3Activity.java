package com.mymobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.utils.EncryptTools;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;



public class Setup3Activity extends BaseSetupActivity {

    private EditText et_safenumber;
    private Button bt_selector_safenumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initData() {
        String safenumber = SpTools.getString(getApplicationContext(), MyConstants.SAFENUMBER, "");
        if (!TextUtils.isEmpty(safenumber)){
            String decryptSafeNumber = EncryptTools.decrypt(safenumber);
            et_safenumber.setText(decryptSafeNumber);
        }
        super.initData();
    }

    @Override
    public void initEvent() {
        bt_selector_safenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setup3Activity.this, FriendsActivity.class);
                startActivityForResult(intent,1);
            }
        });
        super.initEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data !=null){
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            String encryptPhone = EncryptTools.encrypt(phone);
            SpTools.putString(getApplicationContext(), MyConstants.SAFENUMBER, encryptPhone);
            et_safenumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void next(View v) {
        String safenumber = et_safenumber.getText().toString().trim();
        if (TextUtils.isEmpty(safenumber)) {
            Toast.makeText(getApplicationContext(), "安全号码不能为空", Toast.LENGTH_LONG).show();
            return;
        } else {
            String encryptSafeNumber = EncryptTools.encrypt(safenumber);
            SpTools.putString(getApplicationContext(), MyConstants.SAFENUMBER, encryptSafeNumber);
        }
        super.next(v);
    }

    public void initView() {
        setContentView(R.layout.activity_setup3);
        et_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
        bt_selector_safenumber = (Button) findViewById(R.id.bt_setup3_selector_safenumber);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup4Activity.class);
    }
}
