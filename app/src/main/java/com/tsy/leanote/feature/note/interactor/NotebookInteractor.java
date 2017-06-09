package com.tsy.leanote.feature.note.interactor;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.greendao.NotebookDao;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myutil.NetworkUtils;
import com.tsy.sdk.myutil.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsy on 2016/12/22.
 */

public class NotebookInteractor implements NotebookContract.Interactor {

    private final String API_SYNC = "/api/notebook/getSyncNotebooks";       //得到需要同步的笔记本
    private final String API_GET_ALL = "/api/notebook/getNotebooks";       //得到所有笔记本

    private Object mTag;
    private Context mContext;
    private MyOkHttp mMyOkHttp;
    private NotebookDao mNotebookDao;

    public NotebookInteractor() {
        this(null);
    }

    public NotebookInteractor(Object tag) {
        mTag = tag;
        mContext = MyApplication.getInstance().getContext();
        mMyOkHttp = MyApplication.getInstance().getMyOkHttp();
        mNotebookDao = MyApplication.getInstance().getDaoSession().getNotebookDao();
    }

    /**
     * 获取所有笔记本
     * @param userInfo 当前登录用户
     * @param callback
     */
    @Override
    public void getAllNotebooks(final UserInfo userInfo, final NotebookContract.GetNotebooksCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_GET_ALL;

        mMyOkHttp.get()
                .url(url)
                .addParam("token", userInfo.getToken())
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
                        List<Notebook> notebooks = new ArrayList<>();
                        Logger.json(response.toString());
                        for (int i = 0; i < response.length(); i ++) {
                            JSONObject notebook_json = response.optJSONObject(i);
                            Notebook notebook = new Notebook();
                            notebook.setNotebookid(notebook_json.optString("NotebookId"));
                            notebook.setUid(notebook_json.optString("UserId"));
                            notebook.setParent_notebookid(notebook_json.optString("ParentNotebookId"));
                            notebook.setSeq(notebook_json.optInt("Seq"));
                            notebook.setTitle(notebook_json.optString("Title"));
                            notebook.setIs_blog(notebook_json.optBoolean("IsBlog"));
                            notebook.setIs_deleted(notebook_json.optBoolean("IsDeleted"));
                            notebook.setCreated_time(notebook_json.optString("CreatedTime"));
                            notebook.setUpdated_time(notebook_json.optString("UpdatedTime"));
                            notebook.setUsn(notebook_json.optInt("Usn"));

                            notebooks.add(notebook);
                        }
                        mNotebookDao.deleteAll();
                        mNotebookDao.insertInTx(notebooks);
                        callback.onSuccess(notebooks);
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
    public void sync(final UserInfo userInfo, final NotebookContract.GetNotebooksCallback callback) {
        if(!NetworkUtils.checkNetworkConnect(mContext)) {
            callback.onFailure(mContext.getString(R.string.app_no_network));
            return;
        }

        String url = EnvConstant.HOST + API_SYNC;

//        Log.d("tsy", "notebook do sync " + userInfo.getLast_usn());
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
                        List<Notebook> notebooks = new ArrayList<>();

                        for (int i = 0; i < response.length(); i ++) {
                            JSONObject notebook_json = response.optJSONObject(i);
                            Notebook notebook = new Notebook();
                            notebook.setNotebookid(notebook_json.optString("NotebookId"));
                            notebook.setUid(notebook_json.optString("UserId"));
                            notebook.setParent_notebookid(notebook_json.optString("ParentNotebookId"));
                            notebook.setSeq(notebook_json.optInt("Seq"));
                            notebook.setTitle(notebook_json.optString("Title"));
                            notebook.setIs_blog(notebook_json.optBoolean("IsBlog"));
                            notebook.setIs_deleted(notebook_json.optBoolean("IsDeleted"));
                            notebook.setCreated_time(notebook_json.optString("CreatedTime"));
                            notebook.setUpdated_time(notebook_json.optString("UpdatedTime"));
                            notebook.setUsn(notebook_json.optInt("Usn"));

                            if(userInfo.getLast_usn() > 0) {        //如果已经有则更新数据
                                Notebook cur_notebook = getNotebook(notebook_json.optString("NotebookId"));
                                if(cur_notebook != null) {
                                    notebook.setId(cur_notebook.getId());
                                }
                            }
                            notebooks.add(notebook);
                        }
                        mNotebookDao.insertOrReplaceInTx(notebooks);
                        callback.onSuccess(notebooks);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        callback.onFailure(error_msg);
                    }
                });
    }

    /**
     * 本地数据库获取所有笔记本
     * @param userInfo 用户
     * @param parentNotebook 父notebook
     * @return
     */
    @Override
    public ArrayList<Notebook> getNotebooks(UserInfo userInfo, String parentNotebook) {
        List<Notebook> notebooks = mNotebookDao.queryBuilder()
                .where(NotebookDao.Properties.Uid.eq(userInfo.getUid()),
                        NotebookDao.Properties.Is_deleted.eq(false),
                        NotebookDao.Properties.Parent_notebookid.eq(parentNotebook))
                .orderAsc(NotebookDao.Properties.Seq)
                .list();

        return (ArrayList<Notebook>) notebooks;
    }

    /**
     * 获取笔记本目录
     * @param notebookid
     * @return
     */
    @Override
    public String getNotebookPath(String notebookid) {
        if(StringUtils.isEmpty(notebookid)) {
            return "/";
        }

        Notebook notebook = getNotebook(notebookid);
        if(notebook == null) {
            return "/";
        }

        String path = "";

        while(notebook != null) {
            path =  "/" + notebook.getTitle() + path;

            if(StringUtils.isEmpty(notebook.getParent_notebookid())) {
                break;
            }

            notebook = getNotebook(notebook.getParent_notebookid());
        }

        return path;
    }

    private Notebook getNotebook(String notebookid) {
        List<Notebook> notebooks = mNotebookDao.queryBuilder()
                .where(NotebookDao.Properties.Notebookid.eq(notebookid))
                .list();
        if(notebooks != null && notebooks.size() > 0) {
            return notebooks.get(0);
        }

        return null;
    }

}
