package com.mymobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*黑名单数据库的创建*/

public class BlackDb extends SQLiteOpenHelper {
    public BlackDb(Context context) {
        super(context, "black.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table blacktb(_id integer primary key autoincrement, phone text, mode integer, time long)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table blacktb");
    }
}
