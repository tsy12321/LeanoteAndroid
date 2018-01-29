package com.tsy.leanote.constant;

/**
 * Created by tsy on 2016/12/13.
 */

public class EnvConstant {
    public static final String HOST_LEANOTE = "https://leanote.com";

    private static String HOST = HOST_LEANOTE;

    public static void setHOST(String HOST) {
        EnvConstant.HOST = HOST;
    }

    public static String getHOST() {
        return HOST;
    }
}
