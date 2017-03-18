package com.mymobilesafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.dao.BlackDao;
import com.mymobilesafe.domain.BlackBean;
import com.mymobilesafe.domain.BlackTable;
import com.mymobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/*通讯卫士主界面*/

public class TelSmsSafeActivity extends AppCompatActivity {

    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private Button bt_add_safeNumber;
    private ListView lv_safeNumber;
    private ProgressBar pb_loading;
    private TextView tv_noDates;
    private BlackDao blackDao;
    private List<BlackBean> blackNumbers = new ArrayList<BlackBean>();
    private MyBlackNumberAdapter myBlackNumberAdapter = new MyBlackNumberAdapter();
    private final int MOREDATESCOUNTS = 10;
    private List<BlackBean> moreDates;
    private AlertDialog dialog_input;
    private PopupWindow popupWindow;
    private ScaleAnimation sa;
    private View view_popupblacknumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initPopupWindow();
    }

    private void initPopupWindow() {
        view_popupblacknumber = View.inflate(getApplicationContext(), R.layout.popup_blacknumber, null);
        final TextView tv_shoudong = (TextView) view_popupblacknumber.findViewById(R.id.tv_popup_black_shoudong);
        // 联系人添加
        TextView tv_contact = (TextView) view_popupblacknumber.findViewById(R.id.tv_popup_black_contacts);
        // 电话添加
        TextView tv_phonelog = (TextView) view_popupblacknumber.findViewById(R.id.tv_popup_black_phonelog);
        // 短信添加
        TextView tv_smslog = (TextView) view_popupblacknumber.findViewById(R.id.tv_popup_black_smslog);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_popup_black_shoudong://手动输入
                        showInputBlackNumberDialog("");
                        break;
                    case R.id.tv_popup_black_contacts://联系人导入
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, FriendsActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    break;
                    case R.id.tv_popup_black_phonelog://电话记录导入
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, CalllogsActivity.class);
                        startActivityForResult(intent, 1);
                    }
                        break;
                    case R.id.tv_popup_black_smslog://短信记录导入
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, SmslogsActivity.class);
                        startActivityForResult(intent, 1);
                    }
                        break;
                }
                closePopupWindows();
            }
        };
        tv_smslog.setOnClickListener(listener);
        tv_contact.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_shoudong.setOnClickListener(listener);

        popupWindow = new PopupWindow(view_popupblacknumber, -2, -2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sa = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f);
        sa.setDuration(500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            showInputBlackNumberDialog(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closePopupWindows() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void showPopupWindows() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            view_popupblacknumber.startAnimation(sa);
            int[] location = new int[2];
            bt_add_safeNumber.getLocationInWindow(location);

            bt_add_safeNumber.measure(0, 0);
            int height = bt_add_safeNumber.getMeasuredHeight();

            view_popupblacknumber.measure(0, 0);
            int view_pop_width = view_popupblacknumber.getMeasuredWidth();

            popupWindow.showAtLocation(bt_add_safeNumber, Gravity.LEFT | Gravity.TOP, getWindowManager().getDefaultDisplay().getWidth() - view_pop_width, location[1] + height);
        }
    }

    private void initEvent() {
        lv_safeNumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int lastVisiblePosition = lv_safeNumber.getLastVisiblePosition();
                    if (lastVisiblePosition == blackNumbers.size() - 1) {
                        initData();

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        bt_add_safeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindows();
              /*  BlackDao blackDao = new BlackDao(getApplicationContext());
                for (int i = 0; i < 20; i++){
                    blackDao.add("12" + i, BlackTable.SMS);
                    blackDao.add("123" + i, BlackTable.TEL);
                    blackDao.add("1234" + i, BlackTable.ALL);
                }
                myBlackNumberAdapter.notifyDataSetChanged();*/
            }
        });

    }

    private void showInputBlackNumberDialog(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
        View dialog_inputBlackNumber = View.inflate(getApplicationContext(), R.layout.dialog_addblacknumber, null);
        builder.setView(dialog_inputBlackNumber);

        final EditText et_blackNumber = (EditText) dialog_inputBlackNumber.findViewById(R.id.et_telsmssafe_blacknumber);
        final CheckBox cb_mode_sms = (CheckBox) dialog_inputBlackNumber.findViewById(R.id.cb_telsmssafe_smsmode);
        final CheckBox cb_mode_phone = (CheckBox) dialog_inputBlackNumber.findViewById(R.id.cb_telsmssafe_phonemode);
        Button bt_sure = (Button) dialog_inputBlackNumber.findViewById(R.id.bt_dialog_telsmssafe_add);
        Button bt_cancel = (Button) dialog_inputBlackNumber.findViewById(R.id.bt_dialog_telsmssafe_cancel);
        dialog_input = builder.create();
        dialog_input.show();

        et_blackNumber.setText(phone);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_input.dismiss();
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_blackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!cb_mode_phone.isChecked() && !cb_mode_sms.isChecked()) {
                    // 两个拦截都没选
                    Toast.makeText(getApplicationContext(), "至少选择一种拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mode = 0;
                if (cb_mode_phone.isChecked()) {
                    mode |= BlackTable.TEL;// 设置电话拦截模式
                }
                if (cb_mode_sms.isChecked()) {
                    mode |= BlackTable.SMS;// 设置电话拦截模式
                }

                BlackBean bean = new BlackBean();
                bean.setMode(mode);
                bean.setPhone(phone);
                bean.setTime(System.currentTimeMillis());
                blackDao.addBean(bean);

                //重写了BlackBean的hashcode和equals方法
                blackNumbers.remove(bean);
                blackNumbers.add(0, bean);
                myBlackNumberAdapter.notifyDataSetChanged();

                lv_safeNumber.setVisibility(View.VISIBLE);
                tv_noDates.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                dialog_input.dismiss();
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    lv_safeNumber.setVisibility(View.GONE);
                    tv_noDates.setVisibility(View.GONE);
                    pb_loading.setVisibility(View.VISIBLE);
                    break;
                case FINISH:
                    if (blackNumbers.size() != 0) {
                        lv_safeNumber.setVisibility(View.VISIBLE);
                        tv_noDates.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                        myBlackNumberAdapter.notifyDataSetChanged();
                    } else {
                        lv_safeNumber.setVisibility(View.GONE);
                        tv_noDates.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                    }
                    break;
            }

        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(200);
                moreDates = blackDao.getMoreDates(blackNumbers.size(), MOREDATESCOUNTS);

                //第一次进入的时候不弹出吐司
                if (blackNumbers.size() != 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreDates.size() == 0) {
                                Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "成功加载数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                blackNumbers.addAll(moreDates);
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class MyBlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (blackNumbers != null) {
                return blackNumbers.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return blackNumbers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = new ViewHolder();
            if (view == null) {
                view = android.view.View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);
                viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_number);
                viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_mode);
                viewHolder.iv_delete = (ImageView) view.findViewById(R.id.iv_telsmssafe_listview_item_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final BlackBean blackBean = blackNumbers.get(i);
            viewHolder.tv_phone.setText(blackBean.getPhone());

            switch (blackBean.getMode()) {
                case BlackTable.SMS:
                    viewHolder.tv_mode.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    viewHolder.tv_mode.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    viewHolder.tv_mode.setText("全部拦截");
                    break;
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    builder.setTitle("删除黑名单号码");
                    builder.setPositiveButton("真删", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            blackDao.delete(blackBean.getPhone());
                            blackNumbers.remove(i);
                            if (blackNumbers.size() == 0){
                                lv_safeNumber.setVisibility(View.GONE);
                                tv_noDates.setVisibility(View.VISIBLE);
                                pb_loading.setVisibility(View.GONE);
                            }
                            if (blackNumbers.size() < 15 || i == blackNumbers.size()){
                                initData();
                            }else {
                                myBlackNumberAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
             viewHolder = null;
            return view;
        }

    }

    private class ViewHolder {

        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }


    private void initView() {
        setContentView(R.layout.activity_telsmssafe);
        bt_add_safeNumber = (Button) findViewById(R.id.bt_telsms_addsafenumber);
        lv_safeNumber = (ListView) findViewById(R.id.lv_telsms_safenumber);
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);
        tv_noDates = (TextView) findViewById(R.id.tv_telsms_nodata);

        blackDao = new BlackDao(getApplicationContext());

        lv_safeNumber.setAdapter(myBlackNumberAdapter);
    }

}
