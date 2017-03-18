package com.mymobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.mymobilesafe.domain.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * 进程管理的业务封装类
 */

public class TaskManagerEngine {
    public static List<TaskBean> getAllRunningTaskInfo(Context context) {
        List<TaskBean> taskBeanList = new ArrayList<TaskBean>();
        //用来获取内存相关的数据
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //用来获取应用和图标等
        PackageManager packageManager = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info :
                runningAppProcesses) {
            TaskBean taskBean = new TaskBean();

            taskBean.setPackName(info.processName);
            PackageInfo packageInfo = null;
            try {
                //有些进程可能是没有名字的，就是说info.processName可能为null，就会发生
                // NameNotFoundException的异常。异常处理 continue;
                packageInfo = packageManager.getPackageInfo(info.processName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
                taskBean.setName(packageInfo.applicationInfo.loadLabel(packageManager) + "");
                taskBean.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    taskBean.setSystem(true);
                }else {
                    taskBean.setSystem(false);
                }

                //获取当前进程的内存占用
                Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{info.pid});
                long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
                taskBean.setMemSize(totalPrivateDirty);
                taskBeanList.add(taskBean);

        }
        return taskBeanList;
    }

    /**
     * 获取可用内存
     *
     * @param context
     * @return
     */
    public static long getAvailMemSize(Context context) {
        long size = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        size = outInfo.availMem;
        return size;
    }

    /**
     * 获取总内存大小
     */
    public static long getTotalMemSize() {
        long size = 0;
        //内存信息在 /proc/meminfo文件中   ”adb -s emulator 5554 shell“
        File file = new File("/proc/meminfo");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String totalMemInfo = bufferedReader.readLine();

            int startIndex = totalMemInfo.indexOf(":");
            int endIndex = totalMemInfo.indexOf("k");
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex).trim();
            //将kb转为Mb
            size = Long.parseLong(totalMemInfo) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
