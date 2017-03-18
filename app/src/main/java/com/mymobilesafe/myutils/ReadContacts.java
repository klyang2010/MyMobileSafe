package com.mymobilesafe.myutils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mymobilesafe.domain.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrka on 17-2-3.
 */

public class ReadContacts {
    public static List<ContactsBean> readContacts(Context context) {
        List<ContactsBean> datas = new ArrayList<ContactsBean>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uriContacts, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()){
            ContactsBean bean = new ContactsBean();
            String id = cursor.getString(0);
            Cursor cursor_inner = context.getContentResolver().query(uriData, new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
            while (cursor_inner.moveToNext()){
                String data = cursor_inner.getString(0);
                String mimeType = cursor_inner.getString(1);
                if (mimeType.equals("vnd.android.cursor.item/name")){
                    bean.setName(data);
                }else if (mimeType.equals("vnd.android.cursor.item/phone_v2")){
                    bean.setPhone(data);
                }
            }
            cursor_inner.close();
            datas.add(bean);
        }
        return  datas;
    }
}
