package com.mymobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.mymobilesafe.domain.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用管理的业务封装层
 */

public class AppMangerEngine {

    /**
     * 获取SD卡的剩余空间
     *
     * @return byte单位
     */
    public static long getSDAvail() {
        File file = Environment.getExternalStorageDirectory();
        return file.getFreeSpace();
    }

    /**
     * 获取系统的剩余空间
     *
     * @return byte单位
     */
    public static long getSystemAvail() {
        File file = Environment.getRootDirectory();
        return file.getFreeSpace();
    }

    public static List<AppBean> getAppInfo(Context context) {
        List<AppBean> appBeanList = new ArrayList<AppBean>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packInfo :
                installedPackages) {
            AppBean appBean = new AppBean();

            appBean.setAppName(packInfo.applicationInfo.loadLabel(packageManager) + "");
            appBean.setPackName(packInfo.applicationInfo.packageName);
            appBean.setIcon(packInfo.applicationInfo.loadIcon(packageManager));
            File file = new File(packInfo.applicationInfo.sourceDir);
            appBean.setSize(file.length());
            appBean.setSD((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0);
            appBean.setSystem((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            appBean.setApkPath(packInfo.applicationInfo.sourceDir);
            appBean.setUid(packInfo.applicationInfo.uid);

            appBeanList.add(appBean);
        }
        return appBeanList;
    }
}
