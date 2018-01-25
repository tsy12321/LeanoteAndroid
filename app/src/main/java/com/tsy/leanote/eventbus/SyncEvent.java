package com.tsy.leanote.eventbus;

/**
 * Created by tsy on 2017/1/20.
 */

public class SyncEvent {

    public static final String MSG_SYNC = "sync";       //同步
    public static final String MSG_REFRESH = "refresh";       //同步

    private final String mMsg;

    public SyncEvent(String msg) {
        this.mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }
}