package com.tsy.leanote.feature.note.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 笔记本
 * Created by tsy on 2016/12/22.
 */

@Entity
public class Notebook {
    @Id
    private Long id;
    @Unique
    private String notebookid;
    private String uid;
    private String parent_notebookid;       //上级
    private int seq;    //排序
    private String title;
    private boolean is_blog;
    private boolean is_deleted;
    private String created_time;
    private String updated_time;
    private int usn;    //更新同步号
    @Generated(hash = 1743649552)
    public Notebook(Long id, String notebookid, String uid,
            String parent_notebookid, int seq, String title, boolean is_blog,
            boolean is_deleted, String created_time, String updated_time, int usn) {
        this.id = id;
        this.notebookid = notebookid;
        this.uid = uid;
        this.parent_notebookid = parent_notebookid;
        this.seq = seq;
        this.title = title;
        this.is_blog = is_blog;
        this.is_deleted = is_deleted;
        this.created_time = created_time;
        this.updated_time = updated_time;
        this.usn = usn;
    }
    @Generated(hash = 1348176405)
    public Notebook() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNotebookid() {
        return this.notebookid;
    }
    public void setNotebookid(String notebookid) {
        this.notebookid = notebookid;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public int getSeq() {
        return this.seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean getIs_blog() {
        return this.is_blog;
    }
    public void setIs_blog(boolean is_blog) {
        this.is_blog = is_blog;
    }
    public boolean getIs_deleted() {
        return this.is_deleted;
    }
    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    public String getCreated_time() {
        return this.created_time;
    }
    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }
    public String getUpdated_time() {
        return this.updated_time;
    }
    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }
    public int getUsn() {
        return this.usn;
    }
    public void setUsn(int usn) {
        this.usn = usn;
    }
    public String getParent_notebookid() {
        return this.parent_notebookid;
    }
    public void setParent_notebookid(String parent_notebookid) {
        this.parent_notebookid = parent_notebookid;
    }
}
