package com.tsy.leanote.feature.note.interactor;

import android.content.Context;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.bean.NoteFile;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.greendao.NoteDao;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myutil.NetworkUtils;
import com.tsy.sdk.myutil.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tsy on 2016/12/23.
 */

public class NoteInteractor implements NoteContract.Interactor {

    private final String API_SYNC = "/api/note/getSyncNotes";       //获取需要同步的笔记
    private final String API_GET_NOTE = "/api/note/getNotes";       //获得某笔记本下的笔记
    private final String API_GET_NOTE_CONTENT = "/api/note/getNoteContent";       //获得某笔记内容
    private final String API_GET_NOTE_AND_CONTENT = "/api/note/getNoteAndContent";       //获得笔记与内容
    private final String API_UPDATE_NOTE = "/api/note/updateNote";       //更新笔记
    private final String API_ADD_NOTE = "/api/note/addNote";       //插入笔记

    private Object mTag;
    private Context mContext;
    private MyOkHttp mMyOkHttp;
    private NoteDao mNoteDao;

    public NoteInteractor() {
        this(null);
    }

    public NoteInteractor(Object tag) {
        mTag = tag;
        mContext = MyApplication.getInstance().getContext();
        mMyOkHttp = MyApplication.getInstance().getMyOkHttp();
        mNoteDao = MyApplication.getInstance().getDaoSession().getNoteDao();
    }

    /**
     * 获取notebookid下的notes
     * @param userInfo 当前登录用户
     * @param notebookid notebook id
     * @param callback
     */
    @Override
    public void getNotesByNotebookId(UserInfo userInfo, String notebookid, final NoteContract.GetNotesCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_GET_NOTE;

        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .addParam("notebookId", notebookid)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, JSONArray response) {
                        List<Note> notes = new ArrayList<>();

                        for (int i = 0; i < response.length(); i ++) {
                            JSONObject note_json = response.optJSONObject(i);
                            Note note = new Note();
                            note.setNoteid(note_json.optString("NoteId"));
                            note.setNotebookid(note_json.optString("NotebookId"));
                            note.setUid(note_json.optString("UserId"));
                            note.setTitle(note_json.optString("Title"));
                            note.setContent(note_json.optString("Content"));
                            note.setIs_markdown(note_json.optBoolean("IsMarkdown"));
                            note.setIs_blog(note_json.optBoolean("IsBlog"));
                            note.setIs_trash(note_json.optBoolean("IsTrash"));
                            note.setCreated_time(note_json.optString("CreatedTime"));
                            note.setUpdated_time(note_json.optString("UpdatedTime"));
                            note.setPublic_time(note_json.optString("PublicTime"));
                            note.setUsn(note_json.optInt("Usn"));

                            notes.add(note);
                        }
                        mNoteDao.insertInTx(notes);
                        callback.onSuccess(notes);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 同步
     * @param userInfo 当前登录用户
     * @param callback
     */
    @Override
    public void sync(final UserInfo userInfo, final NoteContract.GetNotesCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_SYNC;

//        Log.d("tsy", "note do sync " + userInfo.getLast_usn());
        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .addParam("afterUsn", String.valueOf(userInfo.getLast_usn()))
                .addParam("maxEntry", "1000")
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, JSONArray response) {
//                        Logger.json(response.toString());
                        List<Note> notes = new ArrayList<>();
                        NoteFileInteractor noteFileInteractor = new NoteFileInteractor();

                        for (int i = 0; i < response.length(); i ++) {
                            JSONObject note_json = response.optJSONObject(i);
                            Note note = new Note();
                            note.setNoteid(note_json.optString("NoteId"));
                            note.setNotebookid(note_json.optString("NotebookId"));
                            note.setUid(note_json.optString("UserId"));
                            note.setTitle(note_json.optString("Title"));
                            note.setContent(note_json.optString("Content"));
                            note.setIs_markdown(note_json.optBoolean("IsMarkdown"));
                            note.setIs_blog(note_json.optBoolean("IsBlog"));
                            note.setIs_trash(note_json.optBoolean("IsTrash"));
                            note.setCreated_time(note_json.optString("CreatedTime"));
                            note.setUpdated_time(note_json.optString("UpdatedTime"));
                            note.setPublic_time(note_json.optString("PublicTime"));
                            note.setUsn(note_json.optInt("Usn"));

                            //更新file
                            noteFileInteractor.addNoteFiles(note_json.optString("NoteId"), note_json.optJSONArray("Files"));

                            if(userInfo.getLast_usn() > 0) {        //如果已经有则更新数据
                                Note cur_note = getNote(note_json.optString("NoteId"));
                                if(cur_note != null) {
                                    note.setId(cur_note.getId());
                                }
                            }
                            notes.add(note);
                        }
                        mNoteDao.insertOrReplaceInTx(notes);
                        callback.onSuccess(notes);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 根据notebookid获取本地所有notes
     * @param userInfo 当前登录用户
     * @param notebookid notebook id
     * @return
     */
    @Override
    public ArrayList<Note> getNotesByNotebookId(UserInfo userInfo, String notebookid) {
        List<Note> notes = mNoteDao.queryBuilder()
                .where(NoteDao.Properties.Notebookid.eq(notebookid),
                        NoteDao.Properties.Is_trash.eq(false),
                        NoteDao.Properties.Title.notEq(""))
                .list();

        return (ArrayList<Note>) notes;
    }

    /**
     * 按照更新顺序获取所有note
     * @param userInfo 当前登录用户
     * @return
     */
    @Override
    public ArrayList<Note> getNotesOrderNewest(UserInfo userInfo) {
        List<Note> notes = mNoteDao.queryBuilder()
                .where(NoteDao.Properties.Is_trash.eq(false),
                        NoteDao.Properties.Title.notEq(""))
                .orderDesc(NoteDao.Properties.Updated_time)
                .list();

        return (ArrayList<Note>) notes;
    }

    /**
     * 获取Note内容
     * @param userInfo 用户
     * @param noteId note id
     * @param callback
     */
    @Override
    public void getNoteContent(UserInfo userInfo, final String noteId, final NoteContract.GetNoteContentCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_GET_NOTE_CONTENT;

        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .addParam("noteId", noteId)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        Note note = getNote(noteId);
                        note.setContent(response.optString("Content"));
                        mNoteDao.update(note);

                        callback.onSuccess(note);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 获取Note信息和内容
     * @param userInfo 用户
     * @param noteId note id
     * @param callback
     */
    @Override
    public void getNoteAndContent(UserInfo userInfo, final String noteId, final NoteContract.GetNoteContentCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_GET_NOTE_AND_CONTENT;

        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
                .addParam("noteId", noteId)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        NoteFileInteractor noteFileInteractor = new NoteFileInteractor();

                        Note note = getNote(noteId);
                        note.setNotebookid(response.optString("NotebookId"));
                        note.setUid(response.optString("UserId"));
                        note.setTitle(response.optString("Title"));
                        note.setContent(response.optString("Content"));
                        note.setIs_markdown(response.optBoolean("IsMarkdown"));
                        note.setIs_blog(response.optBoolean("IsBlog"));
                        note.setIs_trash(response.optBoolean("IsTrash"));
                        note.setCreated_time(response.optString("CreatedTime"));
                        note.setUpdated_time(response.optString("UpdatedTime"));
                        note.setPublic_time(response.optString("PublicTime"));
                        note.setUsn(response.optInt("Usn"));
                        noteFileInteractor.addNoteFiles(noteId, response.optJSONArray("Files"));

                        mNoteDao.update(note);

                        callback.onSuccess(note);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 获取Note信息
     * @param noteId note id
     */
    @Override
    public Note getNote(String noteId) {
        List<Note> notes = mNoteDao.queryBuilder()
                .where(NoteDao.Properties.Noteid.eq(noteId))
                .list();
        if(notes != null && notes.size() > 0) {
            return notes.get(0);
        }

        return null;
    }

    /**
     * 更新Note信息
     * @param userInfo 用户
     * @param noteId noteid
     * @param updateArgvs 更新参数
     * @param noteFiles 所有文件元数据
     * @param callback
     */
    @Override
    public void updateNote(final UserInfo userInfo, final String noteId,
                           Map<String, String> updateArgvs,
                           ArrayList<NoteFile> noteFiles,
                           final NoteContract.UpdateNoteCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }
        Iterator iterator = updateArgvs.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if(entry.getValue().toString().isEmpty()) {
                if(entry.getKey().equals("NotebookId")) {
                    callback.onFailure(mContext.getString(R.string.note_empty_notebook));
                    return;
                } else if(entry.getKey().equals("Title")) {
                    callback.onFailure(mContext.getString(R.string.note_empty_title));
                    return;
                } else if(entry.getKey().equals("Content")) {
                    callback.onFailure(mContext.getString(R.string.note_empty_content));
                    return;
                }
            }
        }

        final Note note = getNote(noteId);

        String url = EnvConstant.HOST + API_UPDATE_NOTE;

        updateArgvs.put("NoteId", noteId);
        updateArgvs.put("token", userInfo.getToken());
        updateArgvs.put("Usn", String.valueOf(note.getUsn()));

        Map<String, String> noteFilesBody = parseNoteFileRequestBody(noteFiles);
        updateArgvs.putAll(noteFilesBody);

        Map<String, File> noteFilesMultipart = parseNoteFileMultipart(noteFiles);

        mMyOkHttp.upload()
                .url(url)
                .params(updateArgvs)
                .files(noteFilesMultipart)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        //更新本地localFile
                        NoteFileInteractor noteFileInteractor = new NoteFileInteractor();
                        noteFileInteractor.updateLocalFile(response.optJSONArray("Files"));

                        //更新Note信息
                        Note note = getNote(noteId);
                        note.setNotebookid(response.optString("NotebookId"));
                        note.setUid(response.optString("UserId"));
                        note.setTitle(response.optString("Title"));
                        note.setIs_markdown(response.optBoolean("IsMarkdown"));
                        note.setIs_blog(response.optBoolean("IsBlog"));
                        note.setIs_trash(response.optBoolean("IsTrash"));
                        note.setCreated_time(response.optString("CreatedTime"));
                        note.setUpdated_time(response.optString("UpdatedTime"));
                        note.setPublic_time(response.optString("PublicTime"));
                        note.setUsn(response.optInt("Usn"));
                        noteFileInteractor.addNoteFiles(noteId, response.optJSONArray("Files"));
                        mNoteDao.update(note);

                        //获取内容信息
                        getNoteContent(userInfo, noteId, new NoteContract.GetNoteContentCallback() {
                            @Override
                            public void onSuccess(Note note) {
                                callback.onSuccess(note);
                            }

                            @Override
                            public void onFailure(String msg) {
                                callback.onFailure(msg);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 添加Note
     * @param userInfo 用户
     * @param noteBookId noteBookId
     * @param title 标题
     * @param content 内容
     * @param noteFiles 所有文件元数据
     * @param callback
     */
    @Override
    public void addNote(final UserInfo userInfo,
                        String noteBookId,
                        final String title,
                        final String content,
                        final ArrayList<NoteFile> noteFiles,
                        final NoteContract.UpdateNoteCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }
        if(StringUtils.isEmpty(title)) {
            callback.onFailure(mContext.getString(R.string.note_empty_title));
            return;
        }
        if(StringUtils.isEmpty(content)) {
            callback.onFailure(mContext.getString(R.string.note_empty_content));
            return;
        }
        if(StringUtils.isEmpty(noteBookId)) {
            //默认放在mobile下面
            NotebookInteractor notebookInteractor = new NotebookInteractor();
            Notebook notebook = notebookInteractor.getNotebookByTitle("mobile");
            if(notebook != null) {
                noteBookId = notebook.getNotebookid();
                doAddNote(userInfo, noteBookId, title, content, noteFiles, callback);
            } else {
                //创建一个notebook
                notebookInteractor.addNotebook(userInfo, "mobile", "", new NotebookContract.NotebookCallback() {
                    @Override
                    public void onSuccess(Notebook notebook) {
                        doAddNote(userInfo, notebook.getNotebookid(), title, content, noteFiles, callback);
                    }

                    @Override
                    public void onFailure(String msg) {
                        callback.onFailure(mContext.getString(R.string.app_no_network));
                    }
                });
            }
//            callback.onFailure(mContext.getString(R.string.note_empty_notebook));
//            return;
        } else {
            doAddNote(userInfo, noteBookId, title, content, noteFiles, callback);
        }
    }

    /**
     * addNote请求
     * @param userInfo
     * @param noteBookId
     * @param title
     * @param content
     * @param noteFiles
     * @param callback
     */
    private void doAddNote(UserInfo userInfo,
                           String noteBookId,
                           String title,
                           final String content,
                           ArrayList<NoteFile> noteFiles,
                           NoteContract.UpdateNoteCallback callback) {
        String url = EnvConstant.HOST + API_ADD_NOTE;

        Map<String, String> param = new HashMap<>();
        param.put("token", userInfo.getToken());
        param.put("NotebookId", noteBookId);
        param.put("Title", title);
        param.put("Content", content);
        param.put("IsMarkdown", "true");

        Map<String, String> noteFilesBody = parseNoteFileRequestBody(noteFiles);
        param.putAll(noteFilesBody);

        Map<String, File> noteFilesMultipart = parseNoteFileMultipart(noteFiles);

        mMyOkHttp.upload()
                .url(url)
                .params(param)
                .files(noteFilesMultipart)
                .tag(mTag)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        if(response.has("Ok") && !response.optBoolean("Ok", false)) {
                            callback.onFailure(response.optString("Msg"));
                            return;
                        }

                        //更新本地localFile
                        NoteFileInteractor noteFileInteractor = new NoteFileInteractor();
                        noteFileInteractor.updateLocalFile(response.optJSONArray("Files"));

                        //插入一条数据
                        Note note = new Note();
                        note.setNoteid(response.optString("NoteId"));
                        note.setNotebookid(response.optString("NotebookId"));
                        note.setUid(response.optString("UserId"));
                        note.setTitle(response.optString("Title"));
                        note.setContent(content);
                        note.setIs_markdown(response.optBoolean("IsMarkdown"));
                        note.setIs_blog(response.optBoolean("IsBlog"));
                        note.setIs_trash(response.optBoolean("IsTrash"));
                        note.setCreated_time(response.optString("CreatedTime"));
                        note.setUpdated_time(response.optString("UpdatedTime"));
                        note.setPublic_time(response.optString("PublicTime"));
                        note.setUsn(response.optInt("Usn"));
                        mNoteDao.insert(note);

                        getNoteContent(userInfo, response.optString("NoteId"), new NoteContract.GetNoteContentCallback() {
                            @Override
                            public void onSuccess(Note note) {
                                callback.onSuccess(note);
                            }

                            @Override
                            public void onFailure(String msg) {
                                callback.onFailure(msg);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }
    /**
     * 将文件数据转为请求元数据
     * @param noteFiles
     * @return
     */
    private Map<String, String> parseNoteFileRequestBody(ArrayList<NoteFile> noteFiles) {
        HashMap<String, String> filesBody = new HashMap<>();

        if(noteFiles != null) {
            for(int i = 0; i < noteFiles.size(); i ++) {
                NoteFile noteFile = noteFiles.get(i);

                filesBody.put(String.format("Files[%s][LocalFileId]", i), noteFile.getLocalFileId());
                filesBody.put(String.format("Files[%s][FileId]", i), noteFile.getFileId());
                filesBody.put(String.format("Files[%s][HasBody]", i), String.valueOf(noteFile.getHasBody()));
                filesBody.put(String.format("Files[%s][IsAttach]", i), String.valueOf(noteFile.getIsAttach()));
            }
        }

        return filesBody;
    }

    /**
     * 将文件数据转为multipart
     * @param noteFiles
     * @return
     */
    private Map<String, File> parseNoteFileMultipart(ArrayList<NoteFile> noteFiles) {
        HashMap<String, File> filesBody = new HashMap<>();

        if(noteFiles != null) {
            NoteFileInteractor noteFileInteractor = new NoteFileInteractor();

            for (int i = 0; i < noteFiles.size(); i++) {
                NoteFile noteFile = noteFiles.get(i);

                if (noteFile.getHasBody()) {
                    filesBody.put(String.format("FileDatas[%s]", noteFile.getLocalFileId()), new File(noteFileInteractor.getPicPath(noteFile.getLocalFileId())));
                }
            }
        }

        return filesBody;
    }
}
