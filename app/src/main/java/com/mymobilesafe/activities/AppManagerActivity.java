package com.mymobilesafe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mrka.mymobilesafe.R;
import com.mymobilesafe.domain.AppBean;
import com.mymobilesafe.engine.AppMangerEngine;
import com.mymobilesafe.utils.DensityUtil;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class AppManagerActivity extends AppCompatActivity {

    private final int LOADING = 0;
    private final int FINISH = 1;
    private TextView tv_sdAvail;
    private TextView tv_romAvail;
    private ListView lv_datas;
    private ProgressBar pb_loading;
    private TextView tv_listview_lable;
    private List<AppBean> userApks = new ArrayList<AppBean>();
    private List<AppBean> systemApks = new ArrayList<AppBean>();
    private long sdAvail;
    private long systemAvail;
    private MyAdapter myAdapter;
    private AppBean clickItemBean;
    private PopupWindow popupWindow;
    private View view_popup;
    private ScaleAnimation scaleAnimation;
    private PackageManager packageManager;
    private BroadcastReceiver removeApkReceive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initPopupWindow();
        initRemoveApkReceive();
    }

    /**
     * 软件卸载的监听
     */
    private void initRemoveApkReceive() {
        removeApkReceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(removeApkReceive, filter);
    }

    /**
     * 初始化弹出窗体
     */
    private void initPopupWindow() {
        view_popup = View.inflate(getApplicationContext(), R.layout.popup_appmanger, null);
        LinearLayout ll_remove = (LinearLayout) view_popup
                .findViewById(R.id.ll_appmanager_pop_remove);
        LinearLayout ll_setting = (LinearLayout) view_popup
                .findViewById(R.id.ll_appmanager_pop_setting);
        LinearLayout ll_share = (LinearLayout) view_popup
                .findViewById(R.id.ll_appmanager_pop_share);
        LinearLayout ll_start = (LinearLayout) view_popup
                .findViewById(R.id.ll_appmanager_pop_start);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_appmanager_pop_remove:// 卸载软件
                        removeApk();// 卸载apk
                        break;
                    case R.id.ll_appmanager_pop_setting:// 设置中心
                        settingCenter();// 设置中心
                        break;
                    case R.id.ll_appmanager_pop_share:// 软件分享
                        shareApk();// 软件分享
                        break;
                    case R.id.ll_appmanager_pop_start:// 启动软件
                        startApk();// 启动软件
                        break;

                    default:
                        break;
                }
                closePopupWindow();
            }
        };
        ll_remove.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);

        popupWindow = new PopupWindow(view_popup, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        scaleAnimation.setDuration(300);
    }

    /**
     * 显示弹出窗体
     *
     * @param parent    父组件
     * @param locationX 当前窗体要显示的位置
     * @param locationY
     */
    private void showPopupWindow(View parent, int locationX, int locationY) {
        closePopupWindow();
        view_popup.startAnimation(scaleAnimation);
        popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, locationX, locationY);
    }

    /**
     * 关闭弹出窗体
     */
    private void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 打开apk
     */
    private void startApk() {
        String packName = clickItemBean.getPackName();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packName);
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage);
        } else {
            Toast.makeText(getApplicationContext(), "该app没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享apk
     */
    private void shareApk() {

    }

    /**
     * apk的设置
     */
    private void settingCenter() {
        Intent intent = new Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + clickItemBean.getPackName()));
        startActivity(intent);
    }

    /**
     * 卸载apk
     */
    private void removeApk() {
        if (!clickItemBean.isSystem()) {
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + clickItemBean.getPackName()));
            startActivity(intent);
        } else {
            try {
                if (!RootTools.isRootAvailable()) {
                    Toast.makeText(getApplicationContext(), "请先root刷机", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (RootTools.isRootAvailable() && !RootTools.isAccessGiven()) {
                    Toast.makeText(getApplicationContext(), "请给予root权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                //修改系统的读写权限可读可写
                RootTools.sendShell("mount -o remount rw /system", 8000);
                //删除软件
                RootTools.sendShell("rm -r " + clickItemBean.getApkPath(), 8000);
                //重写修改系统的读写权限为可读
                RootTools.sendShell("mount -o remount r /system", 8000);

            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootToolsException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initEvent() {
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == userApks.size() + 1) {
                    return;
                }
                clickItemBean = (AppBean) lv_datas.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                //显示窗体
                int dp = 50;
                int px = DensityUtil.dip2px(getApplicationContext(), dp);
                showPopupWindow(view, location[0] + px, location[1]);
            }
        });
        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                closePopupWindow();

                if (firstVisibleItem >= userApks.size() + 1) {
                    tv_listview_lable.setText("系统软件(" + systemApks.size() + ")");
                } else {
                    tv_listview_lable.setText("用户软件(" + userApks.size() + ")");
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userApks.size() + 1 + systemApks.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position <= userApks.size() && position != 0) {
                return userApks.get(position - 1);
            } else if (position >= userApks.size() + 2) {
                return systemApks.get(position - userApks.size() - 2);
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
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("个人软件(" + userApks.size() + ")");
                tv_userTable.setBackgroundColor(Color.GRAY);
                tv_userTable.setTextColor(Color.WHITE);

                return tv_userTable;
            } else {
                if (position == userApks.size() + 1) {
                    TextView tv_userTable = new TextView(getApplicationContext());
                    tv_userTable.setText("系统软件(" + systemApks.size() + ")");
                    tv_userTable.setBackgroundColor(Color.GRAY);
                    tv_userTable.setTextColor(Color.WHITE);

                    return tv_userTable;
                } else {
                    ViewHolder viewHolder = new ViewHolder();
                    if (convertView != null && convertView instanceof RelativeLayout) {
                        viewHolder = (ViewHolder) convertView.getTag();
                    } else {
                        convertView = View.inflate(getApplicationContext(), R.layout.item_appmanger_listview_item, null);
                        viewHolder.iv_icon = (ImageView) convertView
                                .findViewById(R.id.iv_appmanager_listview_item_icon);
                        viewHolder.tv_title = (TextView) convertView
                                .findViewById(R.id.tv_appmanager_listview_item_title);
                        viewHolder.tv_location = (TextView) convertView
                                .findViewById(R.id.tv_appmanager_listview_item_location);
                        viewHolder.tv_size = (TextView) convertView
                                .findViewById(R.id.tv_appmanager_listview_item_size);
                        // 绑定tag
                        convertView.setTag(viewHolder);
                    }
                    AppBean bean = (AppBean) getItem(position);

                    // 设置数据
                    viewHolder.iv_icon.setImageDrawable(bean.getIcon());// 设置图标

                    if (bean.isSD()) {
                        viewHolder.tv_location.setText("SD存储");// 设置存储位置
                    } else {
                        viewHolder.tv_location.setText("Rom存储");
                    }

                    viewHolder.tv_title.setText(bean.getAppName());// 设置名字
                    // 设置占用的大小
                    viewHolder.tv_size.setText(Formatter.formatFileSize(
                            getApplicationContext(), bean.getSize()));

                    viewHolder = null;
                    return convertView;
                }
            }
        }
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_location;
        TextView tv_size;
    }

    private void initData() {
        new Thread() {

            @Override
            public void run() {
                systemApks.clear();
                userApks.clear();

                handler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(500);

                List<AppBean> appBeanLists = AppMangerEngine.getAppInfo(getApplicationContext());
                for (AppBean appBean :
                        appBeanLists) {
                    if (appBean.isSystem()) {
                        systemApks.add(appBean);
                    } else {
                        userApks.add(appBean);
                    }
                }
                sdAvail = AppMangerEngine.getSDAvail();
                systemAvail = AppMangerEngine.getSystemAvail();

                handler.obtainMessage(FINISH).sendToTarget();
                super.run();
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);// 显示加载数据进度
                    lv_datas.setVisibility(View.GONE);// 隐藏listview
                    tv_listview_lable.setVisibility(View.GONE);
                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);// 显示加载数据进度
                    lv_datas.setVisibility(View.VISIBLE);// 隐藏listview
                    tv_listview_lable.setVisibility(View.VISIBLE);
                    // 设置内存剩余大小
                    tv_sdAvail.setText("SD卡可用空间:"
                            + Formatter.formatFileSize(getApplicationContext(),
                            sdAvail));
                    tv_romAvail.setText("ROM可用空间:"
                            + Formatter.formatFileSize(getApplicationContext(),
                            systemAvail));
                    // 初始化标签
                    tv_listview_lable.setText("用户软件(" + userApks.size() + ")");
                    myAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initView() {
        setContentView(R.layout.activity_appmanager);
        // sd剩余空间的显示
        tv_sdAvail = (TextView) findViewById(R.id.tv_appmanager_sdsize);

        // rom剩余空间
        tv_romAvail = (TextView) findViewById(R.id.tv_appmanager_romsize);

        // 显示所有apk的listview
        lv_datas = (ListView) findViewById(R.id.lv_appmanager_appdatas);

        // 加载数据进度
        pb_loading = (ProgressBar) findViewById(R.id.pb_appmanager_loading);

        tv_listview_lable = (TextView) findViewById(R.id.tv_appmanager_listview_lable);

        myAdapter = new MyAdapter();
        lv_datas.setAdapter(myAdapter);

        packageManager = getPackageManager();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(removeApkReceive);
        super.onDestroy();

    }
}
