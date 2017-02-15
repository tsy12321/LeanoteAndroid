package com.tsy.leanote.feature.note.contract;

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
    }
}
