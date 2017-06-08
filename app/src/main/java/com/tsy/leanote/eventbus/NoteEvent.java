package com.tsy.leanote.eventbus;

/**
 * Created by tsy on 2017/1/20.
 */

public class NoteEvent {

    public static final String MSG_INIT = "init";       //数据初始化
    public static final String MSG_EDITOR = "editor";       //编辑

    private final String mMsg;

    public NoteEvent(String msg) {
        this.mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }
}