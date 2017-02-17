package com.tsy.leanote.feature.note.interactor;

import android.content.Context;
import android.os.Environment;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.feature.note.bean.NoteFile;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.greendao.NoteFileDao;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tsy on 2017/2/15.
 */

public class NoteFileInteractor implements NoteFileContract.Interactor {

    private Object mTag;
    private Context mContext;
    private MyOkHttp mMyOkHttp;
    private NoteFileDao mNoteFileDao;

    private final String API_GET_IMAGE = "/api/file/getImage";       //获取图片

    private final String DOWNLOAD_PIC_DIR = Environment.getExternalStorageDirectory() + "/com.tsy.leanote/files/";  //图片保存路径

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

    /**
     * 加载某个笔记下的所有pics（下载下来）
     * @param noteid
     */
    @Override
    public void loadAllPics(final UserInfo userInfo, String noteid, final NoteFileContract.LoadAllPicsCallback callback) {
        ArrayList<NoteFile> noteFiles = getFilesByNoteid(noteid);

        //先检查一共需要加载多少图片 多少已经加载
        int total = 0;
        int loaded = 0;
        for(final NoteFile noteFile : noteFiles) {
            if (!noteFile.getIsAttach()) {
                total ++;
                if(new File(getPicPath(noteFile.getFileId())).exists()
                        && new File(getPicPath(noteFile.getFileId())).length() > 0L) {
                    loaded ++;
                }
            }
        }
        callback.onStart(total, loaded);
        if(loaded == total) {
            return;
        }

        //开始下载图片
        for(final NoteFile noteFile : noteFiles) {
            if(!noteFile.getIsAttach()) {
                if(!new File(getPicPath(noteFile.getFileId())).exists()
                        || new File(getPicPath(noteFile.getFileId())).length() == 0L) {
                    String downloadUrl = EnvConstant.HOST + API_GET_IMAGE + "?fileId=" + noteFile.getFileId() + "&token=" + userInfo.getToken();
                    mMyOkHttp.download()
                            .tag(mTag)
                            .url(downloadUrl)
                            .filePath(getPicPath(noteFile.getFileId()))
                            .enqueue(new DownloadResponseHandler() {
                                @Override
                                public void onFinish(File downloadFile) {
                                    callback.onFinish(noteFile.getFileId());
                                }

                                @Override
                                public void onProgress(long currentBytes, long totalBytes) {

                                }

                                @Override
                                public void onFailure(String error_msg) {
                                    callback.onFailure(noteFile.getFileId());
                                }
                            });
                } else {
                    callback.onFinish(noteFile.getFileId());
                }
            }
        }
    }

    /**
     * 获取pic在webview时显示的路径
     * @param fileId
     * @return
     */
    @Override
    public String getPicWebviewPath(String fileId) {
        return "file://" + getPicPath(fileId);
    }

    private String getPicPath(String fileId) {
        return DOWNLOAD_PIC_DIR + fileId + ".png";
    }

    private ArrayList<NoteFile> getFilesByNoteid(String noteid) {
        return (ArrayList<NoteFile>) mNoteFileDao.queryBuilder()
                .where(NoteFileDao.Properties.Noteid.eq(noteid))
                .list();
    }
}
