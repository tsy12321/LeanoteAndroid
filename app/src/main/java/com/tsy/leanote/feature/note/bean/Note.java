package com.tsy.leanote.feature.note.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by tsy on 2016/12/23.
 */

@Entity
public class Note {
    @Id
    private Long id;
    @Unique
    private String noteid;
    private String notebookid;
    private String uid;
    private String title;
    private String content;
    private boolean is_markdown;
    private boolean is_blog;
    private boolean is_trash;
    private String created_time;
    private String updated_time;
    private String public_time;
    private int usn;    //更新同步号
    @Generated(hash = 1921932240)
    public Note(Long id, String noteid, String notebookid, String uid, String title,
            String content, boolean is_markdown, boolean is_blog, boolean is_trash,
            String created_time, String updated_time, String public_time, int usn) {
        this.id = id;
        this.noteid = noteid;
        this.notebookid = notebookid;
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.is_markdown = is_markdown;
        this.is_blog = is_blog;
        this.is_trash = is_trash;
        this.created_time = created_time;
        this.updated_time = updated_time;
        this.public_time = public_time;
        this.usn = usn;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNoteid() {
        return this.noteid;
    }
    public void setNoteid(String noteid) {
        this.noteid = noteid;
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public boolean getIs_markdown() {
        return this.is_markdown;
    }
    public void setIs_markdown(boolean is_markdown) {
        this.is_markdown = is_markdown;
    }
    public boolean getIs_trash() {
        return this.is_trash;
    }
    public void setIs_trash(boolean is_trash) {
        this.is_trash = is_trash;
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
    public String getPublic_time() {
        return this.public_time;
    }
    public void setPublic_time(String public_time) {
        this.public_time = public_time;
    }
    public int getUsn() {
        return this.usn;
    }
    public void setUsn(int usn) {
        this.usn = usn;
    }
    public boolean getIs_blog() {
        return this.is_blog;
    }
    public void setIs_blog(boolean is_blog) {
        this.is_blog = is_blog;
    }
}
