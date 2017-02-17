package com.tsy.leanote;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.tsy.leanote.feature.user.bean.UserInfo;
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
    private UserInfo mUserInfo;         //当前用户

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        mMyApplication = this;
        mContext = getApplicationContext();

        DBHelper devOpenHelper = new DBHelper(getApplicationContext(), "leanote.db");
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();
    }

    private void initMyOkHttp() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        //持久化存储cookie
//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
//
//        //自定义OkHttp
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)       //设置开启cookie
//                .addInterceptor(logging)            //设置开启log
//                .build();
//        mMyOkHttp = new MyOkHttp(okHttpClient);

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

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }
}
