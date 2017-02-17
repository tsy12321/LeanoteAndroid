package com.tsy.leanote.feature.note.contract;

import com.tsy.leanote.feature.user.bean.UserInfo;

import org.json.JSONArray;

/**
 * Created by tsy on 2017/2/15.
 */

public interface NoteFileContract {
    interface Interactor {

        /**
         * 添加某篇note里所有file
         * @param noteid
         * @param noteFiles
         */
        void addNoteFiles(String noteid, JSONArray noteFiles);

        /**
         * 加载某个笔记下的所有pics（下载下来）
         * @param noteid
         */
        void loadAllPics(UserInfo userInfo, String noteid, LoadAllPicsCallback callback);

        /**
         * 获取pic在webview时显示的路径
         * @param fileId
         * @return
         */
        String getPicWebviewPath(String fileId);
    }

    interface LoadAllPicsCallback {
        void onStart(int totalPics, int loadedPics);
        void onFinish(String fileid);
        void onFailure(String fileid);
    }
}
