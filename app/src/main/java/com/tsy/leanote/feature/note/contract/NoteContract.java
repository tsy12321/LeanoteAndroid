package com.tsy.leanote.feature.note.contract;

import com.tsy.leanote.base.BaseInteractorCallback;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        /**
         * 按照更新顺序获取所有note
         * @param userInfo 当前登录用户
         * @return
         */
        ArrayList<Note> getNotesOrderNewest(UserInfo userInfo);

        /**
         * 获取Note内容
         * @param userInfo 用户
         * @param noteId note id
         * @param callback
         */
        void getNoteContent(UserInfo userInfo, String noteId, GetNoteContentCallback callback);

        /**
         * 获取Note信息和内容
         * @param userInfo 用户
         * @param noteId note id
         * @param callback
         */
        void getNoteAndContent(UserInfo userInfo, String noteId, GetNoteContentCallback callback);

        /**
         * 获取Note信息
         * @param noteId note id
         */
        Note getNote(String noteId);

        /**
         * 更新Note信息
         * @param userInfo 用户
         * @param noteId noteid
         * @param updateArgvs 更新参数
         * @param callback
         */
        void updateNote(UserInfo userInfo, String noteId, Map<String, String> updateArgvs, UpdateNoteCallback callback);
    }

    interface GetNotesCallback extends BaseInteractorCallback {
        void onSuccess(List<Note> notes);
    }

    interface GetNoteContentCallback extends BaseInteractorCallback {
        void onSuccess(Note note);
    }

    interface UpdateNoteCallback extends BaseInteractorCallback {
        void onSuccess(Note note);
    }
}
