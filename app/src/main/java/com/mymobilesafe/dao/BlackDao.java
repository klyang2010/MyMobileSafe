package com.mymobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mymobilesafe.db.BlackDb;
import com.mymobilesafe.domain.BlackBean;
import com.mymobilesafe.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;

/**黑名单数据的数据存取层*/
public class BlackDao {
    BlackDb blackDb;

    /**
     * 获取数据库
     * @param context
     */
    public BlackDao(Context context) {
        this.blackDb = new BlackDb(context);
    }

    /**
     * 向黑名单数据库添加黑名单号码
     * @param phone 电话号码
     * @param mode  拦截模式
     * @param time  添加的时间
     */
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

    /**
     * 从黑名单数据库中移除黑名单号码
     * @param phone 要移除的电话号码
     */
    public void delete(String phone){
        SQLiteDatabase database = blackDb.getWritableDatabase();
        database.delete(BlackTable.blackTable, BlackTable.phone + "=?", new String[]{phone});
        database.close();
    }

    /**  向黑名单数据库添加 BlackBean对象 的黑名单号码 */
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

    /**
     * 根据每页要显示多少条数据 来返回总共有多少页
     * @param perPage  指定每页要显示多少条数据
     * @return 总共有多少页
     */
    public int getTotalPages(int perPage) {
        int totalDatas = getTotalDates();
        int totalPages = (int) Math.ceil(totalDatas * 1.0 / perPage);
        return totalPages;
    }

    /**
     *
     * @param currentPage 想获取数据的页码
     * @param perPage 每页要显示多少条数据
     * @return 返回currentPage页的数据
     */
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

    /**
     * 获取更多数据
     * @param startIndex 开始的数据位置
     * @param moreDates 需要获取多少条数据
     * @return 返回moreDates条数据
     */
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

    /**
     * 获取所有的shuj
     * @return
     */
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

    /**
     * 通过电话号码 得到 此电话号码的拦截模式
     * @param originatingAddress 电话号码
     * @return
     */
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
