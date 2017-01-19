package com.tsy.leanote.feature.note.contract;

import com.tsy.leanote.base.BaseInteractorCallback;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsy on 2016/12/23.
 */

public interface NoteContract {
    interface Interactor {

        /**
         * 获取notebookid下的notes
         * @param userInfo 当前登录用户
         * @param notebookid notebook id
         * @param callback
         */
        void getNotesByNotebookId(UserInfo userInfo, String notebookid, GetNotesCallback callback);

        /**
         * 同步
         * @param userInfo 当前登录用户
         * @param callback
         */
        void sync(UserInfo userInfo, GetNotesCallback callback);

        /**
         * 根据notebookid获取本地所有notes
         * @param userInfo 当前登录用户
         * @param notebookid notebook id
         * @return
         */
        ArrayList<Note> getNotesByNotebookId(UserInfo userInfo, String notebookid);
    }

    interface GetNotesCallback extends BaseInteractorCallback {
        void onSuccess(List<Note> notes);
    }
}
