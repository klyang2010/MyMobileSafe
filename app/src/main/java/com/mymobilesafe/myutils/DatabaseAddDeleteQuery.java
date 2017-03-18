package com.mymobilesafe.myutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mymobilesafe.db.BlackDb;
import com.mymobilesafe.domain.BlackBean;
import com.mymobilesafe.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;

/*黑名单数据的业务封装层*/
public class DatabaseAddDeleteQuery {
    BlackDb blackDb;

    public DatabaseAddDeleteQuery(Context context) {
        this.blackDb = new BlackDb(context);
    }

    public void add(String phone, int mode, long time) {
        delete(phone);
        SQLiteDatabase database = blackDb.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(BlackTable.phone, phone);
        value.put(BlackTable.mode, mode);
        value.put(BlackTable.time, time);
        database.insert(BlackTable.blackTable, null, value);
        database.close();
    }
    public void delete(String phone){
        SQLiteDatabase database = blackDb.getWritableDatabase();
        database.delete(BlackTable.blackTable, BlackTable.phone + "=?", new String[]{phone});
        database.close();
    }

    public void addBean(BlackBean blackBean) {
        add(blackBean.getPhone(), blackBean.getMode(), blackBean.getTime());
    }

    public int getTotalDates() {
        SQLiteDatabase database = blackDb.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(1) from " + BlackTable.blackTable, null);
        cursor.moveToNext();
        int totalDates = cursor.getInt(0);
        cursor.close();
        return totalDates;
    }

    public int getTotalPages(int perPage) {
        int totalDatas = getTotalDates();
        int totalPages = (int) Math.ceil(totalDatas * 1.0 / perPage);
        return totalPages;
    }

    public List<BlackBean> getPageDates(int currentPage, int perPage) {
        SQLiteDatabase database = blackDb.getWritableDatabase();
        List<BlackBean> listBlackBean = new ArrayList<BlackBean>();
        Cursor query = database.rawQuery("select * from " + BlackTable.blackTable + " limit ?,?", new String[]{((currentPage - 1) * perPage) + "", perPage + ""});
        while (query.moveToNext()) {
            BlackBean blackBean = new BlackBean();
            String phone = query.getString(query.getColumnIndex(BlackTable.phone));
            int mode = query.getInt(query.getColumnIndex(BlackTable.mode));
            long time = query.getLong(query.getColumnIndex(BlackTable.time));
            blackBean.setPhone(phone);
            blackBean.setMode(mode);
            blackBean.setTime(time);
            listBlackBean.add(blackBean);
        }
        query.close();
        return listBlackBean;
    }

    public List<BlackBean> getMoreDates(int startIndex, int moreDates) {
        SQLiteDatabase database = blackDb.getWritableDatabase();
        List<BlackBean> listBlackBean = new ArrayList<BlackBean>();
        Cursor query = database.rawQuery("select * from " + BlackTable.blackTable + " order by time desc limit ?,?", new String[]{startIndex + "", moreDates + ""});
        while (query.moveToNext()) {
            BlackBean blackBean = new BlackBean();
            String phone = query.getString(query.getColumnIndex(BlackTable.phone));
            int mode = query.getInt(query.getColumnIndex(BlackTable.mode));
            long time = query.getLong(query.getColumnIndex(BlackTable.time));
            blackBean.setPhone(phone);
            blackBean.setMode(mode);
            blackBean.setTime(time);
            listBlackBean.add(blackBean);
        }
        query.close();
        return listBlackBean;
    }

    public List<BlackBean> getAllDates() {
        SQLiteDatabase database = blackDb.getWritableDatabase();
        List<BlackBean> listBlackBean = new ArrayList<BlackBean>();
        Cursor query = database.query(BlackTable.blackTable, null, null, null, null, null, null);
        while (query.moveToNext()) {
            BlackBean blackBean = new BlackBean();
            String phone = query.getString(query.getColumnIndex(BlackTable.phone));
            int mode = query.getInt(query.getColumnIndex(BlackTable.mode));
            long time = query.getLong(query.getColumnIndex(BlackTable.time));
            blackBean.setPhone(phone);
            blackBean.setMode(mode);
            blackBean.setTime(time);
            listBlackBean.add(blackBean);
        }
        query.close();
        return listBlackBean;
    }

    public int getMode(String originatingAddress) {
        SQLiteDatabase database = blackDb.getWritableDatabase();
        Cursor query = database.query(BlackTable.blackTable, new String[]{BlackTable.mode}, BlackTable.phone + " = ?", new String[]{originatingAddress}, null, null, null);
        int mode = 0;
        if (query.moveToNext()) {// 是黑名单号码
            mode = query.getInt(query.getColumnIndex(BlackTable.mode));// 取出对应号码的拦截模式
        } else {
            mode = 0;// 不是黑名单号码
        }
        query.close();
        return mode;
    }
}
