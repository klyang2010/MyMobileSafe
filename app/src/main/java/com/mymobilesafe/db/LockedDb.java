package com.mymobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mrka on.
 */

public class LockedDb extends SQLiteOpenHelper {
    public LockedDb(Context context) {
        super(context, "locked.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table locked(_id integer primary key autoincrement, packname text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table locked");
        onCreate(db);
    }
}
