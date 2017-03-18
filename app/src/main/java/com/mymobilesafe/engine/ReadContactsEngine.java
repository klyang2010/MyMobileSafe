package com.mymobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mymobilesafe.domain.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrka on 16-12-30.
 */

public class ReadContactsEngine extends Object {

    /**  读取手机短信记录的日志  */
    public static List<ContactsBean> readSmslog(Context context){
        //1，电话日志的数据库
        //2,通过分析，db不能直接访问，需要内容提供者访问该数据库
        //3,看上层源码 找到uri content://sms
        Uri uri = Uri.parse("content://sms");
        //获取电话记录的联系人游标
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, " _id desc");
        List<ContactsBean> datas = new ArrayList<ContactsBean>();

        while (cursor.moveToNext()) {
            ContactsBean bean = new ContactsBean();

            String phone = cursor.getString(0);//获取号码
            //String name = cursor.getString(1);//获取名字

            //bean.setName(name);
            bean.setPhone(phone);

            //添加数据
            datas.add(bean);

        }
        cursor.close();
        return datas;

    }

   /**  读取通话记录的日志  */
    public static List<ContactsBean> readCalllog(Context context){
        //1，电话日志的数据库
        //2,通过分析，db不能直接访问，需要内容提供者访问该数据库
        //3,看上层源码 找到uri content://calls
        Uri uri = Uri.parse("content://call_log/calls");
        //获取电话记录的联系人游标
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"number","name"}, null, null, " _id desc");
        List<ContactsBean> datas = new ArrayList<ContactsBean>();

        while (cursor.moveToNext()) {

            ContactsBean bean = new ContactsBean();

            String phone = cursor.getString(0);//获取号码
            String name = cursor.getString(1);//获取名字

            bean.setName(name);
            bean.setPhone(phone);

            //添加数据
            datas.add(bean);

        }
        cursor.close();
        return datas;

    }

    /**  读取手机联系人  */
    public static List<com.mymobilesafe.domain.ContactsBean> readContacts(Context context) {
        List<com.mymobilesafe.domain.ContactsBean> datas = new ArrayList<com.mymobilesafe.domain.ContactsBean>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uriContacts, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()){
            com.mymobilesafe.domain.ContactsBean bean = new com.mymobilesafe.domain.ContactsBean();
            String id = cursor.getString(0);
            Cursor cursor1 = context.getContentResolver().query(uriData, new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
            while (cursor1.moveToNext()){
                String data = cursor1.getString(0);
                String mimeType = cursor1.getString(1);
                if (mimeType.equals("vnd.android.cursor.item/name")){
                    bean.setName(data);
                }else if (mimeType.equals("vnd.android.cursor.item/phone_v2")){
                    bean.setPhone(data);
                }
            }
            cursor1.close();
            datas.add(bean);
        }
        return  datas;
    }

}
