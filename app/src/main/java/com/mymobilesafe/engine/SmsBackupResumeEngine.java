package com.mymobilesafe.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.mymobilesafe.utils.EncryptTools;
import com.mymobilesafe.utils.JsonStrTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by mrka on 17-2-9.
 */

public class SmsBackupResumeEngine {

    public interface BackupProgress {
        /**
         * 设置进度条显示
         */
        void show();

        void setMax(int max);

        void setProgress(int progress);

        /**
         * 设置进度条消失
         */
        void end();
    }

    /**
     * 为了避免定义静态属性，故意定义了一个静态类
     * progress为当前的进度值
     */
    private static class CurrentProgress {
        int progress;
    }

    /**
     * json格式的短信还原
     *
     * @param context
     * @param backupProgress
     */
    public static void SmsResumeJson(final Activity context, final BackupProgress backupProgress) {
        new Thread() {

            private BufferedReader bufferedReader;
            private FileInputStream fileInputStream;
            Uri uri = Uri.parse("content://sms");
            CurrentProgress currentProgress = new CurrentProgress();

            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory(), "sms.json");
                try {
                    fileInputStream = new FileInputStream(file);
                    bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    StringBuffer stringBuffer = new StringBuffer();
                    String length;
                    while ((length = bufferedReader.readLine()) != null) {
                        stringBuffer.append(length);
                    }

                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                    //获取短信总条数
                    final int count = Integer.parseInt(jsonObject.getString("count"));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backupProgress.show();
                            backupProgress.setMax(count);
                        }
                    });
                    //循环读取短信
                    JSONArray jarray = (JSONArray) jsonObject.get("smses");
                    for (int i = 0; i < count; i++) {
                        SystemClock.sleep(400);
                        currentProgress.progress = i;
                        //获取一条短信
                        JSONObject smsjson = jarray.getJSONObject(i);

                        ContentValues values = new ContentValues();
                        values.put("address", smsjson.getString("address"));
                        values.put("body", EncryptTools.decrypt(smsjson.getString("body")));
                        values.put("date", smsjson.getString("date"));
                        values.put("type", smsjson.getString("type"));

                        //往短信数据中加一条记录
                        context.getContentResolver().insert(uri, values);

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                backupProgress.setProgress(currentProgress.progress);
                            }
                        });
                    }

                    //重新将progress值置为0
                    currentProgress.progress = 0;
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backupProgress.end();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fileInputStream.close();
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        }.start();
    }

    /**
     * 备份短信成json格式
     *
     * @param context        Activity， 方便调用ruanonuithread
     * @param backupProgress 通过借口回调传递数据 传递 progress的数值，让调用此工具的程序员 在自己调用的地方完成自己想要做的功能
     */
    public static void SmsBackupJson(final Activity context, final BackupProgress backupProgress) {
        new Thread() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms");
                // 获取电话记录的联系人游标
                final Cursor cursor = context.getContentResolver().query(uri,
                        new String[]{"address", "date", "body", "type"}, null, null,
                        " _id desc");
                final CurrentProgress currentProgress = new CurrentProgress();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backupProgress.show();
                        backupProgress.setMax(cursor.getCount());
                    }
                });
                File file = new File(Environment.getExternalStorageDirectory(), "sms.json");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PrintWriter printWriter = new PrintWriter(fileOutputStream);
                    printWriter.println("{\"count\":\"" + cursor.getCount() + "\"");
                    printWriter.println(",\"smses\":[");
                    while (cursor.moveToNext()) {
                        SystemClock.sleep(500);
                        currentProgress.progress++;
                        if (cursor.getPosition() == 0) {
                            printWriter.println("{");
                        } else {
                            printWriter.println(",{");
                        }
                        // address 封装 "address":"hello"
                        printWriter.println("\"address\":\"" + cursor.getString(0)
                                + "\",");
                        // date 封装
                        printWriter.println("\"date\":\"" + cursor.getString(1) + "\",");
                        // body 封装
                        //对短信加密处理
                        printWriter.println("\"body\":\"" + EncryptTools.encrypt(JsonStrTools.changeStr(cursor.getString(2) + "\",")));
                        // type 封装
                        printWriter.println("\"type\":\"" + cursor.getString(3) + "\"");

                        printWriter.println("}");

                        printWriter.flush();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                backupProgress.setProgress(currentProgress.progress);
                            }
                        });
                    }
                    currentProgress.progress = 0;
                    printWriter.println("]}");
                    printWriter.flush();
                    fileOutputStream.close();
                    printWriter.close();
                    cursor.close();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backupProgress.end();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }


    /**
     * 将短信备份成XML格式
     *
     * @param context        Activity类型，方便调用runonuithread方法
     * @param backupProgress 自定义的接口
     */
    public static void SmsBackupXml(Activity context, final BackupProgress backupProgress) {
        Uri uri = Uri.parse("content://sms");
        // 获取电话记录的联系人游标
        final Cursor cursor = context.getContentResolver().query(uri,
                new String[]{"address", "date", "body", "type"}, null, null,
                " _id desc");
        final CurrentProgress currentProgress = new CurrentProgress();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backupProgress.show();
                backupProgress.setMax(cursor.getCount());
            }
        });
        File file = new File(Environment.getExternalStorageDirectory(), "sms.xml");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.println("<smses count='" + cursor.getCount() + "'>");
            while (cursor.moveToNext()) {
                SystemClock.sleep(500);
                currentProgress.progress++;
                printWriter.println("<sms>");
                printWriter.println("<address>" + cursor.getString(0) + "</address>");
                printWriter.println("<date>" + cursor.getString(1) + "</date>");
                printWriter.println("<body>" + cursor.getString(2) + "</body>");
                printWriter.println("<type>" + cursor.getString(3) + "</type>");
                printWriter.println("</sms>");
                printWriter.flush();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backupProgress.setProgress(currentProgress.progress);
                    }
                });
            }
            printWriter.println("</smses>");
            printWriter.flush();
            fileOutputStream.close();
            printWriter.close();
            cursor.close();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    backupProgress.end();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
