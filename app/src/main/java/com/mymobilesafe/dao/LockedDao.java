package com.mymobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mymobilesafe.db.LockedDb;
import com.mymobilesafe.domain.LockedTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrka on.
 */

public class LockedDao {
    LockedDb lockedDb;

    public LockedDao(Context context) {
        this.lockedDb = new LockedDb(context);
    }

    public void add(String packname){
        SQLiteDatabase writableDatabase = lockedDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LockedTable.PACKNAME, packname);
        writableDatabase.insert(LockedTable.TABLENAME, null, values);
        writableDatabase.close();
    }

    public void delete(String packname){
        SQLiteDatabase writableDatabase = lockedDb.getWritableDatabase();
        writableDatabase.delete(LockedTable.TABLENAME, LockedTable.PACKNAME + "=?", new String[]{packname});
        writableDatabase.close();
    }

    public List<String> getAllLockedDatas(){
        List<String> stringList = new ArrayList<String>();
        SQLiteDatabase readableDatabase = lockedDb.getReadableDatabase();
        Cursor cursor = readableDatabase.query(LockedTable.TABLENAME, null, null, null, null, null, null);
        while(cursor.moveToNext()){
            String packname = cursor.getString(0);
            stringList.add(packname);
        }
        cursor.close();
        readableDatabase.close();
        return stringList;
    }
}
