package com.tsy.leanote.feature.user.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.tsy.leanote.HomeActivity;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.constant.SharePreConstant;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
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

    private boolean mPwdVisible = false;
    private UserContract.Interactor mUserInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mUserInteractor = new UserInteractor();

        String lastLoginEmail = SharePreferenceUtils.getString(getApplicationContext(), SharePreConstant.KEY_LAST_LOGIN_EMAIL);
        if (!StringUtils.isEmpty(lastLoginEmail)) {
            mEditUsername.setText(lastLoginEmail);
            mEditUsername.setSelection(mEditUsername.length());
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

        mUserInteractor.login(email, pwd, new UserContract.UserCallback() {
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
        String forget_url = EnvConstant.HOST + "/findPassword";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(forget_url));
        startActivity(intent);
    }

    @OnClick(R.id.txt_register)
    public void register() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
