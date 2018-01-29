package com.tsy.leanote.feature.user.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsy.leanote.HomeActivity;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    EditText mEditUsername;
    @BindView(R.id.edit_password)
    EditText mEditPassword;
    @BindView(R.id.img_pwd_visible)
    ImageView mImgPwdVisible;
    @BindView(R.id.edit_password_confim)
    EditText mEditPasswordConfim;
    @BindView(R.id.rl_server)
    RelativeLayout mRlServer;
    @BindView(R.id.edit_server)
    EditText mEditServer;

    private boolean mPwdVisible = false;
    private UserContract.Interactor mUserInteractor;
    private String mSelfServer;
    private static final String EXTRA_SELFSERVER = "selfserver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mSelfServer = getIntent().getStringExtra(EXTRA_SELFSERVER);
        if(!StringUtils.isEmpty(mSelfServer)) {
            mRlServer.setVisibility(View.VISIBLE);
            mEditServer.setText(mSelfServer);
        }

        mUserInteractor = new UserInteractor();
    }

    @OnClick(R.id.img_pwd_visible)
    public void pwdVisible() {
        if (mPwdVisible) {
            mPwdVisible = false;
            mEditUsername.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEditPasswordConfim.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mImgPwdVisible.setImageResource(R.drawable.ic_login_not_show_pwd);
        } else {
            mPwdVisible = true;
            mEditUsername.setTransformationMethod(null);
            mEditPasswordConfim.setTransformationMethod(null);
            mImgPwdVisible.setImageResource(R.drawable.ic_login_show_pwd);
        }
        mEditPassword.setSelection(mEditPassword.length());
        mEditPasswordConfim.setSelection(mEditPasswordConfim.length());
    }

    @OnClick(R.id.btn_register)
    public void register() {
        String email = mEditUsername.getText().toString();
        String pwd = mEditPassword.getText().toString();
        String pwd_confirm = mEditPasswordConfim.getText().toString();

        if (StringUtils.isEmpty(email)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_email_empty);
            return;
        }

        if (StringUtils.isEmpty(pwd)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_pwd_empty);
            return;
        }

        if (pwd.length() < 6) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_pwd_length_error);
            return;
        }

        if (!pwd.equals(pwd_confirm)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_confirm_pwd_error);
            return;
        }

        String host = EnvConstant.HOST_LEANOTE;
        if(!StringUtils.isEmpty(mSelfServer)) {
            host = mSelfServer;
        }

        mUserInteractor.register(host, email, pwd, new UserContract.UserCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                startActivity(HomeActivity.createIntent(RegisterActivity.this));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showShort(getApplicationContext(), msg);
            }
        });
    }

    @OnClick(R.id.txt_back_login)
    public void backLogin() {
        finish();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    public static Intent createIntent(Context context, String selfServer) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(EXTRA_SELFSERVER, selfServer);
        return intent;
    }
}
