package com.tsy.leanote.feature.splash.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.HomeActivity;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.leanote.feature.user.view.LoginActivity;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity {

    private MyHandler mMyHandler;
    private UserContract.Interactor mUserInteractor;

    static class MyHandler extends Handler {
        private final WeakReference<SplashActivity> mWeakSplashActivity;
        private SplashActivity mSplashActivity;

        MyHandler(SplashActivity splashActivity) {
            mWeakSplashActivity = new WeakReference<SplashActivity>(splashActivity);
            mSplashActivity = mWeakSplashActivity.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mSplashActivity.splashTimeOut();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mUserInteractor = new UserInteractor(this);

        mMyHandler = new MyHandler(this);
        mMyHandler.sendEmptyMessageDelayed(0, 2000);
    }

    private void splashTimeOut() {
        if(mUserInteractor.getCurUser() != null) {
            startActivity(HomeActivity.createIntent(this));
        } else {
            startActivity(LoginActivity.createIntent(this));
        }
    }
}
