package com.tsy.leanote.feature.note.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by tsy on 2017/2/3.
 */

@Entity
public class NoteFile {
    @Id
    private Long id;

    private String noteid;
    private String fileId;
    private String localFileId;
    private String type;
    private String title;
    private boolean hasBody;
    private boolean isAttach;
    @Generated(hash = 2026444157)
    public NoteFile(Long id, String noteid, String fileId, String localFileId,
            String type, String title, boolean hasBody, boolean isAttach) {
        this.id = id;
        this.noteid = noteid;
        this.fileId = fileId;
        this.localFileId = localFileId;
        this.type = type;
        this.title = title;
        this.hasBody = hasBody;
        this.isAttach = isAttach;
    }
    @Generated(hash = 1417941967)
    public NoteFile() {
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
    public String getFileId() {
        return this.fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getLocalFileId() {
        return this.localFileId;
    }
    public void setLocalFileId(String localFileId) {
        this.localFileId = localFileId;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean getHasBody() {
        return this.hasBody;
    }
    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }
    public boolean getIsAttach() {
        return this.isAttach;
    }
    public void setIsAttach(boolean isAttach) {
        this.isAttach = isAttach;
    }
}
