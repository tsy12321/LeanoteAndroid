package com.tsy.leanote.feature.user.interactor;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.greendao.UserInfoDao;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by tsy on 2016/12/13.
 */

public class UserInteractor implements UserContract.Interactor {

    private final String API_LOGIN = "/api/auth/login";       //登录
    private final String API_GET_USER_IFNO = "/api/user/info";       //获取用户信息

    private Object mTag;
    private MyOkHttp mMyOkHttp;
    private UserInfoDao mUserInfoDao;

    public UserInteractor() {
        this(null);
    }

    public UserInteractor(Object tag) {
        mTag = tag;
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

                        mUserInfoDao.deleteAll();
                        mUserInfoDao.insert(userInfo);

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

                        mUserInfoDao.update(userInfo);

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

}
