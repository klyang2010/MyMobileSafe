package com.mymobilesafe.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.domain.ContactsBean;
import com.mymobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/*联系人界面*/

public abstract class BaseTelSmsFriendsActivity extends ListActivity {
    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private ProgressDialog pd;
    private List<ContactsBean> datas = new ArrayList<ContactsBean>();
    private MyAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = getListView();
        initData();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        initEvent();
    }

    private void initEvent() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactsBean bean = datas.get(i);
                String phone = bean.getPhone();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.SAFENUMBER, phone);
                setResult(1, intent);
                finish();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pd = new ProgressDialog(BaseTelSmsFriendsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据。。。。");
                    pd.show();
                    break;
                case FINISH:
                    if (pd != null) {
                        pd.dismiss();
                        pd = null;
                    }

                    if (datas.size() == 0) {//联系人没有数据
                        Intent datas = new Intent();
                        datas.putExtra(MyConstants.SAFENUMBER, "");//保存安全号码
                        //设置数据
                        setResult(1, datas);
                        //关闭自己
                        finish();
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private class MyAdapter extends BaseAdapter {

        private View view;

        @Override
        public int getCount() {
            return datas.size();
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
            ViewHolder holder = new ViewHolder();

            if (view == null) {
                view = View.inflate(getApplicationContext(), R.layout.item_friends_listview, null);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_friends_listview_name);
                holder.tv_phone = (TextView) view.findViewById(R.id.tv_friends_listview_phone);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            ContactsBean bean = datas.get(i);
            holder.tv_name.setText(bean.getName());
            holder.tv_phone.setText(bean.getPhone());

            return view;
        }
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_phone;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);

                SystemClock.sleep(1000);
                //核心代码
                datas = getDatas();

                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    protected abstract List<ContactsBean> getDatas();
}
