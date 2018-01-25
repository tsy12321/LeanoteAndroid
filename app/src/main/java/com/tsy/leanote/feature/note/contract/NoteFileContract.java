package com.tsy.leanote.feature.note.contract;

import com.tsy.leanote.feature.note.bean.NoteFile;
import com.tsy.leanote.feature.user.bean.UserInfo;

import org.json.JSONArray;

import java.util.ArrayList;

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
         * 更新localFile
         * @param noteId
         * @param noteFiles
         */
        void updateLocalFile(String noteId, JSONArray noteFiles);

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

        /**
         * 获取pic存取路径
         * @param fileId
         * @return
         */
        String getPicPath(String fileId);

        /**
         * 插入新notefile
         * @param noteId noteid
         * @param path 文件路径
         * @return
         */
        String createNoteFile(String noteId, String path);

        /**
         * 获取新插入的noteFile
         * @return
         */
        ArrayList<NoteFile> getAddNoteFiles();
    }

    interface LoadAllPicsCallback {
        void onStart(int totalPics, int loadedPics);
        void onFinish(String fileid);
        void onFailure(String fileid);
    }
}
