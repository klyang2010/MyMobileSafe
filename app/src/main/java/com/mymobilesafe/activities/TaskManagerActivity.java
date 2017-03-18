package com.mymobilesafe.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.domain.TaskBean;
import com.mymobilesafe.engine.TaskManagerEngine;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 进程管理
 */

public class TaskManagerActivity extends AppCompatActivity {

    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private TextView tv_tasknumber;
    private TextView tv_meminfo;
    private ListView lv_taskdatas;
    private TextView tv_list_tag;
    private ProgressBar pb_loading;

    List<TaskBean> taskBeanList = new CopyOnWriteArrayList<TaskBean>();
    List<TaskBean> userTaskBeanList = new CopyOnWriteArrayList<TaskBean>();
    List<TaskBean> systemTaskBeanList = new CopyOnWriteArrayList<TaskBean>();
    private long availMem;
    private long totalMem;
    private MyAdapter myAdapter;
    private ActivityManager activityManager;
    private Object obj = new Object();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //initData(); 已经在onresume方法中写过了
        initEvent();
    }

    private void initEvent() {
        lv_taskdatas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem <= userTaskBeanList.size()) {
                    // 用户的tag
                    tv_list_tag.setText("用户进程(" + userTaskBeanList.size() + ")");
                } else {
                    tv_list_tag.setText("系统进程(" + systemTaskBeanList.size() + ")");
                }
            }
        });
    }

    private void initData() {

        new Thread() {
            @Override
            public void run() {
                synchronized (obj){
                    handler.obtainMessage(LOADING).sendToTarget();
                    taskBeanList = TaskManagerEngine.getAllRunningTaskInfo(getApplicationContext());
                    availMem = TaskManagerEngine
                            .getAvailMemSize(getApplicationContext());
                    totalMem = TaskManagerEngine
                            .getTotalMemSize();
                    SystemClock.sleep(500);

                    userTaskBeanList.clear();
                    systemTaskBeanList.clear();
                    for (TaskBean task :
                            taskBeanList) {
                        if (task.isSystem()) {
                            systemTaskBeanList.add(task);
                        } else {
                            userTaskBeanList.add(task);
                        }
                    }

                    handler.obtainMessage(FINISH).sendToTarget();
                    super.run();
                }//synchronized 的结束
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_taskdatas.setVisibility(View.GONE);
                    tv_list_tag.setVisibility(View.GONE);
                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    lv_taskdatas.setVisibility(View.VISIBLE);
                    tv_list_tag.setVisibility(View.VISIBLE);

                    setTitleMessage();

                    myAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setTitleMessage() {
        // 设置运行中的进程个数
        if (SpTools.getBooean(getApplicationContext(), MyConstants.SHOWSYSTEM, false)) {
            tv_tasknumber.setText("运行中的进程:"
                    + (systemTaskBeanList.size() + userTaskBeanList.size()));
        } else {
            tv_tasknumber.setText("运行中的进程:"
                    + (userTaskBeanList.size()));
        }

        // 设置内存的使用信息
        // 格式化显示可用内存
        String availMemFormatter = Formatter.formatFileSize(
                getApplicationContext(), availMem);
        // 格式化显示可用内存
        String totalMemFormatter = Formatter.formatFileSize(
                getApplicationContext(), totalMem);
        // 设置内存的使用信息
        tv_meminfo.setText("可用/总内存:" + availMemFormatter + "/"
                + totalMemFormatter);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (!SpTools.getBooean(getApplicationContext(), MyConstants.SHOWSYSTEM, false)) {
                //不显示系统进程
                return userTaskBeanList.size() + 1;
            }
            return userTaskBeanList.size() + 1 + systemTaskBeanList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position <= userTaskBeanList.size() && position != 0) {
                return userTaskBeanList.get(position - 1);
            } else if (position >= userTaskBeanList.size() + 2) {
                return systemTaskBeanList.get(position - userTaskBeanList.size() - 2);
            } else {
                return null;
            }

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                // 用户apk的标签
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("个人软件(" + userTaskBeanList.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色
                return tv_userTable;
            } else if (position == userTaskBeanList.size() + 1) {
                // 系统apk标签
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("系统软件(" + systemTaskBeanList.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色
                return tv_userTable;
            } else {
                ViewHolder viewHolder = null;
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();
                } else {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_taskmanager_listview_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_icon = (ImageView) convertView
                            .findViewById(R.id.iv_taskmanager_listview_item_icon);
                    viewHolder.tv_title = (TextView) convertView
                            .findViewById(R.id.tv_taskmanager_listview_item_title);
                    viewHolder.tv_memsize = (TextView) convertView
                            .findViewById(R.id.tv_taskmanager_listview_item_memsize);
                    viewHolder.cb_checked = (CheckBox) convertView
                            .findViewById(R.id.tv_taskmanager_listview_item_checked);
                    // 绑定tag
                    convertView.setTag(viewHolder);
                }
                final TaskBean taskBean = (TaskBean) getItem(position);
                // 设置数据
                viewHolder.iv_icon.setImageDrawable(taskBean.getIcon());// 设置图标

                // 设置占用的内存大小
                viewHolder.tv_memsize.setText(Formatter.formatFileSize(
                        getApplicationContext(), taskBean.getMemSize()));

                viewHolder.tv_title.setText(taskBean.getName());// 设置名字

                //引用传递
                final ViewHolder mViewHolder = viewHolder;

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (taskBean.getPackName().equals(getPackageName())) {
                            mViewHolder.cb_checked.setChecked(false);
                        }
                        taskBean.setChecked(!taskBean.isChecked());

                        /**
                         * mViewHolder.cb_checked.setChecked(taskBean.isChecked());
                         * 没有这句话cb_checked不能实时改变选中的状态
                         *  viewHolder.cb_checked.setChecked(taskBean.isChecked());
                         *  没有这句话cb_checked会显示错乱
                         */
                        mViewHolder.cb_checked.setChecked(taskBean.isChecked());
                    }
                });


                /**
                 * mViewHolder.cb_checked.setChecked(taskBean.isChecked());
                 * 没有这句话cb_checked不能实时改变选中的状态
                 *  viewHolder.cb_checked.setChecked(taskBean.isChecked());
                 *  没有这句话cb_checked会显示错乱
                 */
                viewHolder.cb_checked.setChecked(taskBean.isChecked());

                //如果是自己，不能勾选，不能清理
                if (taskBean.getPackName().equals(getPackageName())) {
                    viewHolder.cb_checked.setVisibility(View.GONE);
                } else {
                    viewHolder.cb_checked.setVisibility(View.VISIBLE);
                }

                return convertView;
            }
        }
    }

    private class ViewHolder {
        ImageView iv_icon;// 图标
        TextView tv_title; // 名字
        TextView tv_memsize;// 占用内存大小
        CheckBox cb_checked;// 是否选择
    }

    public void selectAll(View v) {
        for (TaskBean taskBean : userTaskBeanList) {
            if (taskBean.getPackName().equals(getPackageName())) {
                taskBean.setChecked(false);
                continue;
            }
            taskBean.setChecked(true);
        }
        for (TaskBean taskBean : systemTaskBeanList) {
            taskBean.setChecked(true);
        }

        myAdapter.notifyDataSetChanged();

    }

    public void fanSelect(View v) {
        for (TaskBean taskBean : userTaskBeanList) {
            if (taskBean.getPackName().equals(getPackageName())) {
                taskBean.setChecked(false);
                continue;
            }
            taskBean.setChecked(!taskBean.isChecked());
        }
        for (TaskBean taskBean : systemTaskBeanList) {
            taskBean.setChecked(!taskBean.isChecked());
        }
        myAdapter.notifyDataSetChanged();
    }

    public void setting(View v) {
        Intent setting = new Intent(this, TastManagerSettingActivity.class);
        startActivity(setting);//启动设置界面
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    public void clearTask (View v) {
        long clearMem = 0;
        int clearNum = 0;

        for (TaskBean taskBean : userTaskBeanList) {
            if (taskBean.isChecked()) {
                clearNum++;
                clearMem += taskBean.getMemSize();
                activityManager.killBackgroundProcesses(taskBean.getPackName());
                userTaskBeanList.remove(taskBean);
            }
        }
        for (TaskBean taskBean : systemTaskBeanList) {
            if (taskBean.isChecked()) {
                clearNum++;
                clearMem += taskBean.getMemSize();
                activityManager.killBackgroundProcesses(taskBean.getPackName());
                systemTaskBeanList.remove(taskBean);
            }
        }

        Toast.makeText(getApplicationContext(), "清理了" + clearNum +
                "个进程，释放了" + Formatter.formatFileSize(getApplicationContext(), clearMem), Toast.LENGTH_SHORT).show();
        availMem += clearMem;
        setTitleMessage();
        myAdapter.notifyDataSetChanged();
    }

    private void initView() {
        setContentView(R.layout.activity_taskmanager);
        // 显示进程的个数
        tv_tasknumber = (TextView) findViewById(R.id.tv_taskmanager_tasknumber);

        // 显示使用的内存信息
        tv_meminfo = (TextView) findViewById(R.id.tv_taskmanager_meminfo);

        // 显示所有进程的信息
        lv_taskdatas = (ListView) findViewById(R.id.lv_taskmanager_appdatas);

        // 进程数据的标签
        tv_list_tag = (TextView) findViewById(R.id.tv_taskmanager_listview_lable);

        // 加载进程数据的 进度
        pb_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loading);

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        myAdapter = new MyAdapter();
        lv_taskdatas.setAdapter(myAdapter);
    }
}
