package com.mymobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mymobilesafe.utils.MyConstants;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrka on 17-2-7.
 */

public class PhoneLocationEngine {

    /**
     * 通过判断电话号码是手机还是固定电话，调用不同的方法去查询归属地数据库
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static String locationQuery(Context context, String phoneNumber) {
        String res = phoneNumber;
        // []中表示取值范围，{}表示重复的次数。如[0-9]{9}表示有9位 范围是0-9 的数字。
        Pattern pattern = Pattern.compile("1{1}[358]{1}[0-9]{9}");
        Matcher matcher = pattern.matcher(phoneNumber);
        boolean b = matcher.matches();
        if (b) {
            res = mobileQuery(context, phoneNumber);
        } else if(phoneNumber.length() > 11){
            // 如果是固定号码
            res = landlinePhoneQuery(context, phoneNumber);
        } else{
            //服务号码
        }
        return res;
    }

    /**
     * @param phoneNumber 固定电话号码
     * @param context
     * @return 固定电话号码归属地
     */
    public static String landlinePhoneQuery(Context context, String phoneNumber) {
        /*
		 * phoneNumber 三种类型： 1， 手机号 2， 固定电话 3， 服务号码 110 120 95559 95555
		 * 0755-88888888 010-888888
		 */
        String res = phoneNumber;
        File file = new File(context.getFilesDir(), MyConstants.TEL_ADDRESS);
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String quHao = "";
        // 2位区号 3位区号
        if (phoneNumber.charAt(1) == '1' || phoneNumber.charAt(1) == '2') {
            // 2位区号
            quHao = phoneNumber.substring(1, 3);
        } else {
            // 3位区号
            quHao = phoneNumber.substring(1, 4);
        }

        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where area=?", new String[]{quHao});
        if (cursor.moveToNext()) {
            res = cursor.getString(cursor.getColumnIndex("location"));
        }
        cursor.close();
        return res;
    }

    /**
     * 查询手机号码的归属地
     *
     * @param mobileNumber 手机号
     * @param context
     * @return
     */
    public static String mobileQuery(Context context, String mobileNumber) {
        String res = mobileNumber;
        File file = new File(context.getFilesDir(), MyConstants.TEL_ADDRESS);
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outKey from data1 where id=?)", new String[]{mobileNumber.substring(0, 7)});
        if (cursor.moveToNext()) {
            res = cursor.getString(cursor.getColumnIndex("location"));
        }
        cursor.close();
        return res;
    }
}
