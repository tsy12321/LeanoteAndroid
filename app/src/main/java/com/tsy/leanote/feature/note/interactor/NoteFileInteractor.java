package com.tsy.leanote.feature.note.interactor;

import android.content.Context;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.feature.note.bean.NoteFile;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.greendao.NoteFileDao;
import com.tsy.sdk.myokhttp.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tsy on 2017/2/15.
 */

public class NoteFileInteractor implements NoteFileContract.Interactor {

    private Object mTag;
    private Context mContext;
    private MyOkHttp mMyOkHttp;
    private NoteFileDao mNoteFileDao;

    public NoteFileInteractor() {
        this(null);
    }

    public NoteFileInteractor(Object tag) {
        mTag = tag;
        mContext = MyApplication.getInstance().getContext();
        mMyOkHttp = MyApplication.getInstance().getMyOkHttp();
        mNoteFileDao = MyApplication.getInstance().getDaoSession().getNoteFileDao();
    }

    /**
     * 添加某篇note里所有file
     * @param noteid
     * @param noteFiles
     */
    @Override
    public void addNoteFiles(String noteid, JSONArray noteFiles) {
        //删除旧数据
        mNoteFileDao.deleteInTx(getFilesByNoteid(noteid));

        //添加新数据
        if(noteFiles != null && noteFiles.length() > 0) {
            for(int i = 0; i < noteFiles.length(); i ++) {
                JSONObject noteFileJson = noteFiles.optJSONObject(i);
                NoteFile noteFile = new NoteFile();
                noteFile.setNoteid(noteid);
                noteFile.setFileId(noteFileJson.optString("FileId"));
                noteFile.setLocalFileId(noteFileJson.optString("LocalFileId"));
                noteFile.setType(noteFileJson.optString("Type"));
                noteFile.setTitle(noteFileJson.optString("Title"));
                noteFile.setHasBody(noteFileJson.optBoolean("HasBody"));
                noteFile.setIsAttach(noteFileJson.optBoolean("IsAttach"));
                mNoteFileDao.insert(noteFile);
            }
        }
    }

    private ArrayList<NoteFile> getFilesByNoteid(String noteid) {
        return (ArrayList<NoteFile>) mNoteFileDao.queryBuilder()
                .where(NoteFileDao.Properties.Noteid.eq(noteid))
                .list();
    }
}
