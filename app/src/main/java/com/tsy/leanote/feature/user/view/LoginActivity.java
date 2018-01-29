package com.tsy.leanote.feature.user.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsy.leanote.HomeActivity;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.constant.SharePreConstant;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.leanote.widget.webview.WebviewActivity;
import com.tsy.sdk.myutil.SharePreferenceUtils;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    EditText mEditUsername;
    @BindView(R.id.edit_password)
    EditText mEditPassword;
    @BindView(R.id.img_pwd_visible)
    ImageView mImgPwdVisible;
    @BindView(R.id.txt_server)
    TextView mTxtServer;
    @BindView(R.id.edit_server)
    EditText mEditServer;
    @BindView(R.id.rl_server)
    RelativeLayout mRlServer;

    private boolean mPwdVisible = false;
    private boolean mSelfServer = false;
    private UserContract.Interactor mUserInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserInteractor = new UserInteractor(this);
        if(mUserInteractor.getCurUser() != null) {
            //设置当前host
            String host = SharePreferenceUtils.getString(getApplicationContext(), SharePreConstant.KEY_LAST_LOGIN_HOST);
            if(!StringUtils.isEmpty(host)) {
                EnvConstant.setHOST(host);
            }

            startActivity(HomeActivity.createIntent(this));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mUserInteractor = new UserInteractor();

        String lastLoginEmail = SharePreferenceUtils.getString(getApplicationContext(), SharePreConstant.KEY_LAST_LOGIN_EMAIL);
        if (!StringUtils.isEmpty(lastLoginEmail)) {
            mEditUsername.setText(lastLoginEmail);
            mEditUsername.setSelection(mEditUsername.length());
        }
        String host = SharePreferenceUtils.getString(getApplicationContext(), SharePreConstant.KEY_LAST_LOGIN_HOST);
        if (!StringUtils.isEmpty(host) && !host.equals(EnvConstant.HOST_LEANOTE)) {
            switchServer();
            mEditServer.setText(host);
        }
    }

    @OnClick(R.id.img_pwd_visible)
    public void pwdVisible() {
        if (mPwdVisible) {
            mPwdVisible = false;
            mEditPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mImgPwdVisible.setImageResource(R.drawable.ic_login_not_show_pwd);
        } else {
            mPwdVisible = true;
            mEditPassword.setTransformationMethod(null);
            mImgPwdVisible.setImageResource(R.drawable.ic_login_show_pwd);
        }
        mEditPassword.setSelection(mEditPassword.length());
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String email = mEditUsername.getText().toString();
        String pwd = mEditPassword.getText().toString();

        if (StringUtils.isEmpty(email)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_email_empty);
            return;
        }

        if (StringUtils.isEmpty(pwd)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_pwd_empty);
            return;
        }

        if(mSelfServer && StringUtils.isEmpty(mEditServer.getText().toString())) {
            ToastUtils.showShort(getApplicationContext(), R.string.address_empty);
            return;
        }

        String host = EnvConstant.HOST_LEANOTE;
        if(mSelfServer) {
            host = mEditServer.getText().toString();
        }

        //登录
        mUserInteractor.login(host, email, pwd, new UserContract.UserCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                startActivity(HomeActivity.createIntent(LoginActivity.this));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showShort(getApplicationContext(), msg);
            }
        });
    }

    @OnClick(R.id.txt_forget_pwd)
    public void forgetPwd() {
        if(mSelfServer && StringUtils.isEmpty(mEditServer.getText().toString())) {
            ToastUtils.showShort(getApplicationContext(), R.string.address_empty);
            return;
        }

        String host = EnvConstant.HOST_LEANOTE;
        if(mSelfServer) {
            host = mEditServer.getText().toString();
        }

        String forgetUrl = host + "/findPassword";
        startActivity(WebviewActivity.createIntent(this, forgetUrl));
    }

    @OnClick(R.id.txt_register)
    public void register() {
        if(mSelfServer && StringUtils.isEmpty(mEditServer.getText().toString())) {
            ToastUtils.showShort(getApplicationContext(), R.string.address_empty);
            return;
        }

        if(mSelfServer) {
            startActivity(RegisterActivity.createIntent(this, mEditServer.getText().toString()));
        } else {
            startActivity(RegisterActivity.createIntent(this));
        }
    }

    @OnClick(R.id.txt_server)
    public void switchServer() {
        if(!mSelfServer) {       //自建服务器
            mTxtServer.setText(R.string.leanote_server);
            mRlServer.setVisibility(View.VISIBLE);
            mSelfServer = true;
        } else {
            mTxtServer.setText(R.string.self_server);
            mRlServer.setVisibility(View.GONE);
            mSelfServer = false;
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
