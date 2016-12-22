package com.tsy.leanote.feature.user.contract;

import com.tsy.leanote.base.BaseInteractorCallback;
import com.tsy.leanote.base.NormalInteractorCallback;
import com.tsy.leanote.feature.user.bean.UserInfo;

/**
 * Created by tsy on 2016/12/13.
 */

public interface UserContract {

    interface Interactor {
        /**
         * 登录
         * @param email
         * @param pwd
         * @param callback
         */
        void login(String email, String pwd, UserCallback callback);

        /**
         * 获取用户信息
         * @param uid
         * @param token
         * @param callback
         */
        void getUserInfo(String uid, String token, UserContract.UserCallback callback);

        /**
         * 获取当前用户
         * @return
         */
        UserInfo getCurUser();

        /**
         * 注册
         * @param email
         * @param pwd
         * @param callback
         */
        void register(String email, String pwd, UserCallback callback);

        /**
         * 退出
         * @param userInfo 当前登录用户
         * @param callback
         */
        void logout(UserInfo userInfo, NormalInteractorCallback callback);
    }

    interface UserCallback extends BaseInteractorCallback {
        void onSuccess(UserInfo userInfo);
    }
}
