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
         * @param host
         * @param email
         * @param pwd
         * @param callback
         */
        void login(String host, String email, String pwd, UserCallback callback);

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
         * @param host
         * @param email
         * @param pwd
         * @param callback
         */
        void register(String host, String email, String pwd, UserCallback callback);

        /**
         * 退出
         * @param userInfo 当前登录用户
         * @param callback
         */
        void logout(UserInfo userInfo, NormalInteractorCallback callback);

        /**
         * 获取最新同步状态
         * @param userInfo 当前登录用户
         * @param callback
         */
        void getSyncState(UserInfo userInfo, GetSyncStateCallback callback);

        /**
         * 更新最新的同步信息
         * @param userInfo 当前登录用户
         * @param lastSyncUsn 最新同步usn
         */
        void updateLastSyncUsn(UserInfo userInfo, int lastSyncUsn);
    }

    interface UserCallback extends BaseInteractorCallback {
        void onSuccess(UserInfo userInfo);
    }

    interface GetSyncStateCallback extends BaseInteractorCallback {
        void onSuccess(int lastSyncUsn);
    }
}
