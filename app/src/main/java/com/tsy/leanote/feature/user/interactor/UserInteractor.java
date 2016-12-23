package com.tsy.leanote.feature.user.interactor;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.NormalInteractorCallback;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.constant.SharePreConstant;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.greendao.UserInfoDao;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myutil.NetworkUtils;
import com.tsy.sdk.myutil.SharePreferenceUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by tsy on 2016/12/13.
 */

public class UserInteractor implements UserContract.Interactor {

    private final String API_LOGIN = "/api/auth/login";       //登录
    private final String API_GET_USER_IFNO = "/api/user/info";       //获取用户信息
    private final String API_REGISTER = "/api/auth/register";       //注册
    private final String API_LOGOUT = "/api/auth/logout";       //退出注销
    private final String API_SYNC = "/api/user/getSyncState";       //获取最新同步状态

    private Object mTag;
    private Context mContext;
    private MyOkHttp mMyOkHttp;
    private UserInfoDao mUserInfoDao;

    public UserInteractor() {
        this(null);
    }

    public UserInteractor(Object tag) {
        mTag = tag;
        mContext = MyApplication.getInstance().getContext();
        mMyOkHttp = MyApplication.getInstance().getMyOkHttp();
        mUserInfoDao = MyApplication.getInstance().getDaoSession().getUserInfoDao();
    }

    /**
     * 登录
     * @param email
     * @param pwd
     * @param callback
     */
    @Override
    public void login(String email, String pwd, final UserContract.UserCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_LOGIN;
        mMyOkHttp.get()
                .url(url)
                .addParam("email", email)
                .addParam("pwd", pwd)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(!response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        UserInfo userInfo = new UserInfo();
                        userInfo.setUid(response.optString("UserId"));
                        userInfo.setUsername(response.optString("Username"));
                        userInfo.setEmail(response.optString("Email"));
                        userInfo.setToken(response.optString("Token"));

                        mUserInfoDao.insert(userInfo);

                        //保存最近登录email
                        SharePreferenceUtils.putString(mContext, SharePreConstant.KEY_LAST_LOGIN_EMAIL, response.optString("Email"));

                        //获取一次用户信息
                        getUserInfo(userInfo.getUid(), userInfo.getToken(), callback);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 获取用户信息
     * @param uid
     * @param token
     * @param callback
     */
    @Override
    public void getUserInfo(final String uid, String token, final UserContract.UserCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_GET_USER_IFNO;
        mMyOkHttp.get()
                .url(url)
                .addParam("userId", uid)
                .addParam("token", token)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        UserInfo userInfo = getCurUser();
                        if(userInfo == null || !userInfo.getUid().equals(uid)) {
                            callback.onFailure("not login");
                            return;
                        }

                        userInfo.setUsername(response.optString("Username"));
                        userInfo.setEmail(response.optString("Email"));
                        userInfo.setLogo(response.optString("Logo"));
                        userInfo.setVerified(response.optBoolean("Verified"));
                        userInfo.setLast_usn(0);

                        mUserInfoDao.update(userInfo);

                        Logger.i("Login success %s", userInfo);

                        callback.onSuccess(userInfo);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 获取当前用户
     * @return
     */
    @Override
    public UserInfo getCurUser() {
        List<UserInfo> userInfoList = mUserInfoDao.queryBuilder()
                .list();
        if(userInfoList != null && userInfoList.size() > 0) {
            return userInfoList.get(0);
        }

        return null;
    }

    /**
     * 注册
     * @param email
     * @param pwd
     * @param callback
     */
    @Override
    public void register(final String email, final String pwd, final UserContract.UserCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_REGISTER;
        mMyOkHttp.post()
                .url(url)
                .addParam("email", email)
                .addParam("pwd", pwd)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(!response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        //自动调用登录
                        login(email, pwd, callback);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 退出
     * @param userInfo 当前登录用户
     * @param callback
     */
    @Override
    public void logout(final UserInfo userInfo, final NormalInteractorCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_LOGOUT;
        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(!response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        //退出
                        mUserInfoDao.deleteAll();
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 同步
     * @param userInfo 当前登录用户
     * @param callback
     */
    @Override
    public void sync(final UserInfo userInfo, final NormalInteractorCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_SYNC;
        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        userInfo.setLast_usn(response.optInt("LastSyncUsn"));

                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

}
