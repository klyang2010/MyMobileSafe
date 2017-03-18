package com.mymobilesafe.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.dao.BlackDao;
import com.mymobilesafe.domain.BlackBean;
import com.mymobilesafe.domain.BlackTable;

import java.util.List;

/**
 * 通讯卫士主界面，分页版
 */

public class TelSmsSafeActivityPage extends AppCompatActivity {

    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private Button bt_add_safeNumber;
    private ListView lv_safeNumber;
    private ProgressBar pb_loading;
    private TextView tv_noDates;
    private BlackDao blackDao;
    private List<BlackBean> blackNumbersDates;
    private MyBlackNumberAdapter myBlackNumberAdapter;
    private TextView tv_current_and_totalPage;
    private EditText et_goToPage;

    int currentPage = 1;
    final int perPage = 28;
    int totalPages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        bt_add_safeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    System.out.println(blackNumbersDates.size());
                    if (blackNumbersDates.size() != 0) {
                        lv_safeNumber.setVisibility(View.VISIBLE);
                        tv_noDates.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                        tv_current_and_totalPage.setText(currentPage + "/" + totalPages);
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
                SystemClock.sleep(1000);

                blackNumbersDates = blackDao.getPageDates(currentPage, perPage);
                totalPages = blackDao.getTotalPages(perPage);
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class MyBlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (blackNumbersDates != null) {
                return blackNumbersDates.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = new ViewHolder();
            if (view == null) {
                view = View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);
                viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_number);
                viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_mode);
                viewHolder.iv_delete = (ImageView) view.findViewById(R.id.iv_telsmssafe_listview_item_delete);
                view.setTag(viewHolder);
                System.out.println("新建");
            } else {
                viewHolder = (ViewHolder) view.getTag();
                System.out.println("缓存");
            }
            BlackBean blackBean = blackNumbersDates.get(i);
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
            viewHolder = null;
            return view;
        }

    }

    private class ViewHolder {

        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    public void nextPage(View v) {
        if (currentPage == totalPages) {
            currentPage = 0;
        }
        currentPage++;
        initData();
    }

    public void prevPage(View v) {
        if (currentPage == 0) {
            currentPage = totalPages + 1;
        }
        currentPage--;
        initData();
    }

    public void endPage(View v) {
        currentPage = totalPages;
        initData();
    }

    public void jumpPage(View v) {
        String jump = et_goToPage.getText().toString().trim();
        if (TextUtils.isEmpty(jump)) {
            Toast.makeText(getApplicationContext(), "请输入页码", Toast.LENGTH_SHORT).show();
        } else {
            int jump_page = Integer.parseInt(jump);
            if (jump_page >= 0 && jump_page <= totalPages) {
                currentPage = jump_page;
                initData();
            } else {
                Toast.makeText(getApplicationContext(), "请输入正确的页码", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initView() {
        setContentView(R.layout.activity_telsmssafe);
        bt_add_safeNumber = (Button) findViewById(R.id.bt_telsms_addsafenumber);
        lv_safeNumber = (ListView) findViewById(R.id.lv_telsms_safenumber);
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);
        tv_noDates = (TextView) findViewById(R.id.tv_telsms_nodata);

        et_goToPage = (EditText) findViewById(R.id.et_telsms_gotopage);
        tv_current_and_totalPage = (TextView) findViewById(R.id.tv_telsms_current_and_totalpages);

        blackDao = new BlackDao(getApplicationContext());
        myBlackNumberAdapter = new MyBlackNumberAdapter();
        lv_safeNumber.setAdapter(myBlackNumberAdapter);
    }

}
