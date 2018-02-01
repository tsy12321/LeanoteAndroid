package com.tsy.leanote;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.kingja.loadsir.core.LoadSir;
import com.squareup.leakcanary.LeakCanary;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.greendao.DBHelper;
import com.tsy.leanote.greendao.DaoMaster;
import com.tsy.leanote.greendao.DaoSession;
import com.tsy.leanote.widget.loadsir.EmptyCallback;
import com.tsy.leanote.widget.loadsir.ErrorCallback;
import com.tsy.leanote.widget.loadsir.LoadingCallback;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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

        //内存泄露分析初始化
        if(BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }

        //umeng统计初始化
        if(!TextUtils.isEmpty(BuildConfig.UMENG_APPKEY)) {
            MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
            UMConfigure.init(this, BuildConfig.UMENG_APPKEY, "github", UMConfigure.DEVICE_TYPE_PHONE, null);
        }

        mMyApplication = this;
        mContext = getApplicationContext();

        //数据库初始化
        DBHelper devOpenHelper = new DBHelper(getApplicationContext(), "leanote.db");
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();

        //loading初始化
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
    }

    private void initMyOkHttp() {
        if(BuildConfig.DEBUG) {
            //自定义OkHttp
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)            //设置开启log
                    .build();
            mMyOkHttp = new MyOkHttp(okHttpClient);
        } else {
            mMyOkHttp = new MyOkHttp();
        }
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
