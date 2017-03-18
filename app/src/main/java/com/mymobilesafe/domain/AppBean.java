package com.mymobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by mrka on 17-2-9.
 */

/**
 * app信息的封装类
 */
public class AppBean {
    private Drawable icon; //图标
    private String appName; //名字
    private String packName; //包名
    private String apkPath;
    private int uid;
    private long size; //大小
    private boolean isSD; //是否安装在SD卡
    private boolean isSystem; //是否是系统软件

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSD() {
        return isSD;
    }

    public void setSD(boolean SD) {
        isSD = SD;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
