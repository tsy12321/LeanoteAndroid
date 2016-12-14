package com.tsy.leanote.feature.user.view;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    EditText edit_username;

    @BindView(R.id.edit_password)
    EditText edit_password;

    @BindView(R.id.img_pwd_visible)
    ImageView img_pwd_visible;

    private boolean mPwdVisible = false;
    private UserContract.Interactor mUserInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mUserInteractor = new UserInteractor();
    }

    @OnClick(R.id.img_pwd_visible)
    public void pwdVisible() {
        if(mPwdVisible) {
            mPwdVisible = false;
            edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            img_pwd_visible.setImageResource(R.drawable.ic_login_not_show_pwd);
        } else {
            mPwdVisible = true;
            edit_password.setTransformationMethod(null);
            img_pwd_visible.setImageResource(R.drawable.ic_login_show_pwd);
        }
        edit_password.setSelection(edit_password.length());
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String email = edit_username.getText().toString();
        String pwd = edit_password.getText().toString();

        if(StringUtils.isEmpty(email)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_email_empty);
            return;
        }

        if(StringUtils.isEmpty(pwd)) {
            ToastUtils.showShort(getApplicationContext(), R.string.user_pwd_empty);
            return;
        }

        mUserInteractor.login(email, pwd, new UserContract.UserCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                Log.d("tsy", "login in onSuccess " + userInfo);
            }

            @Override
            public void onFailure(String msg) {
                Log.d("tsy", "onFailure " + msg);
            }
        });
    }

    @OnClick(R.id.txt_forget_pwd)
    public void forgetPwd() {

    }

    @OnClick(R.id.txt_register)
    public void register() {

    }
}
