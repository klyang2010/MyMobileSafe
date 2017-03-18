package com.mymobilesafe.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;
/*手机防盗主界面*/

public class LostFindActivity extends AppCompatActivity {

//    private AlertDialog dialog;
    private PopupWindow popupWindow;
    private ScaleAnimation scaleAnimation;
    private View view_popup_modify_name;
//    private View view_modify_name;
    private LinearLayout ll_lostfind_root;
    private TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBooean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            inintView();
            initPopupView();
            initPopupWindow();
        } else {
            Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initPopupWindow() {
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this, null);
//        layoutParams.WRAP_CONTENT
        popupWindow = new PopupWindow(view_popup_modify_name, -2, -2, true);
        scaleAnimation = new ScaleAnimation(0.0f, 1.0f,0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);
    }

    private void initPopupView() {
        view_popup_modify_name = View.inflate(this, R.layout.modify_lostfind_name, null);
        final EditText et_name = (EditText) view_popup_modify_name.findViewById(R.id.et_modify_lostfind_name);
        Button sure = (Button) view_popup_modify_name.findViewById(R.id.bt_modify_lostfind_name_sure);
        Button cancel = (Button) view_popup_modify_name.findViewById(R.id.bt_modify_lostfind_name_cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(LostFindActivity.this);
        builder.setView(view_popup_modify_name);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "名字不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    SpTools.putString(getApplicationContext(), MyConstants.MODIFYLOSTFINDNAME, name);
                    tv_title.setText(name + "界面");
                    popupWindow.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* getMenuInflater().inflate(R.menu.main, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()){
            case R.id.menu_main:
                show_Menu_ModifyLostFindName_Dialog();
        }*/
        return super.onOptionsItemSelected(item);
    }

    /*private void show_Menu_ModifyLostFindName_Dialog() {
        view_modify_name = View.inflate(LostFindActivity.this, R.layout.modify_lostfind_name, null);
        final EditText et_name = (EditText) view_modify_name.findViewById(R.id.et_modify_lostfind_name);
        Button bt_sure = (Button) view_modify_name.findViewById(R.id.bt_modify_lostfind_name_sure);
        Button bt_cancle = (Button) view_modify_name.findViewById(R.id.bt_modify_lostfind_name_cancel);

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "名字不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    SpTools.putString(getApplicationContext(), MyConstants.MODIFYLOSTFINDNAME, name);
                    dialog.dismiss();
                }
            }
        });
    }*/

    public void enterSetupl(View v) {
        Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
        startActivity(intent);
        SpTools.putBoolean(getApplicationContext(), MyConstants.ISSETUP, false);
        finish();
    }

    private void inintView() {

        setContentView(R.layout.activity_lostfind);
        ll_lostfind_root = (LinearLayout) findViewById(R.id.ll_lostfind_root);
        tv_title = (TextView) findViewById(R.id.tv_lostfind_title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU){
            popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.popupwindow)));
            view_popup_modify_name.startAnimation(scaleAnimation);
            int width_window = getWindowManager().getDefaultDisplay().getWidth();
            int height_window = getWindowManager().getDefaultDisplay().getHeight();
            view_popup_modify_name.measure(0, 0);
            int with = (width_window - view_popup_modify_name.getMeasuredWidth()) / 2;
            int height = (height_window - view_popup_modify_name.getMeasuredHeight()) / 2;

            popupWindow.showAtLocation(ll_lostfind_root, Gravity.TOP | Gravity.LEFT, with, height);
        }
        return super.onKeyDown(keyCode, event);
    }
}

