package com.tsy.leanote.feature.note.contract;

import com.tsy.leanote.base.BaseInteractorCallback;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsy on 2016/12/22.
 */

public interface NotebookContract {
    interface Interactor {

        /**
         * 获取所有笔记本
         * @param userInfo 当前登录用户
         * @param callback
         */
        void getAllNotebooks(UserInfo userInfo, GetNotebooksCallback callback);

        /**
         * 同步
         * @param userInfo 当前登录用户
         * @param callback
         */
        void sync(UserInfo userInfo, GetNotebooksCallback callback);

        /**
         * 本地数据库获取所有笔记本
         * @param userInfo 用户
         * @param parentNotebook 父notebook
         * @return
         */
        ArrayList<Notebook> getNotebooks(UserInfo userInfo, String parentNotebook);

        /**
         * 获取笔记本目录
         * @param notebookid
         * @return
         */
        String getNotebookPath(String notebookid);
    }

    interface GetNotebooksCallback extends BaseInteractorCallback {
        void onSuccess(List<Notebook> notebooks);
    }
}
