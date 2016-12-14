package com.tsy.leanote.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tsy on 2016/11/9.
 */

public class DBHelper extends DaoMaster.OpenHelper {


    public DBHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("tsy", "onUpgrade old version=" + oldVersion + " new=" + newVersion);
    }
}
