package com.tsy.leanote;

import android.app.Application;
import android.content.Context;

import com.tsy.leanote.greendao.DBHelper;
import com.tsy.leanote.greendao.DaoMaster;
import com.tsy.leanote.greendao.DaoSession;
import com.tsy.sdk.myokhttp.MyOkHttp;

/**
 * Created by tsy on 2016/12/13.
 */

public class MyApplication extends Application {

    private static MyApplication mMyApplication;
    private Context mContext;
    protected MyOkHttp mMyOkHttp;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mMyApplication = this;
        mContext = getApplicationContext();

        DBHelper devOpenHelper = new DBHelper(getApplicationContext(), "leanote.db");
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();
    }

    private void initMyOkHttp() {
        mMyOkHttp = new MyOkHttp();
    }

    /**
     * myokhttp
     * @return
     */
    public MyOkHttp getMyOkHttp() {
        if(mMyOkHttp == null) {
            initMyOkHttp();
        }

        return mMyOkHttp;
    }

    /**
     * 获取全局Application
     * @return
     */
    public static synchronized MyApplication getInstance() {
        return mMyApplication;
    }

    /**
     * 获取ApplicationContext
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取dao session
     * @return
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
