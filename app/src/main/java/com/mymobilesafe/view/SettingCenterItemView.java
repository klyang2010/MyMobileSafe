package com.mymobilesafe.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;

/**
 * 设置中心的自定义 item_view
 */

public class SettingCenterItemView extends LinearLayout {

    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_check;
    private View view;
    private String[] contents;

    public SettingCenterItemView(Context context) {
        super(context, null);
    }

    public SettingCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-com.mrka.mymobilesafe", "title");
        String content = attrs.getAttributeValue("http://schemas.android.com/apk/res-com.mrka.mymobilesafe", "content");
        contents = content.split("-");
        tv_title.setText(title);
        tv_content.setText(contents[0]);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    public boolean isChecked() {
        return cb_check.isChecked();
    }

    public void setChecked(boolean isChecked) {
        cb_check.setChecked(isChecked);
    }

    private void initEvent() {
        /*view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_check.setChecked(!cb_check.isChecked());
            }
        });*/

        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tv_content.setTextColor(Color.GREEN);
                    tv_content.setText(contents[1]);
                } else {
                    tv_content.setTextColor(Color.RED);
                    tv_content.setText(contents[0]);
                }
            }
        });
    }


    private void initView() {
        view = View.inflate(getContext(), R.layout.item_settingcenter_view, null);
        tv_title = (TextView) view.findViewById(R.id.tv_item_settingcenter_title);
        tv_content = (TextView) view.findViewById(R.id.tv_item_settingcenter_content);
        cb_check = (CheckBox) view.findViewById(R.id.cb_item_settingcenter);
        addView(view);
    }
}
