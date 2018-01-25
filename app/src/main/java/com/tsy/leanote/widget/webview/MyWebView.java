package com.tsy.leanote.widget.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tsy.leanote.MyApplication;
import com.tsy.sdk.myutil.ManifestUtils;
import com.tsy.sdk.myutil.NetworkUtils;

import java.util.Locale;

/**
 * Created by tangsiyuan on 2018/1/25.
 */

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置setting
    public void initSetting() {
        WebSettings websettings = getSettings();
        websettings.setUserAgentString(websettings.getUserAgentString());
        websettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        websettings.setDomStorageEnabled(true);
        websettings.setJavaScriptEnabled(true);
        websettings.setDefaultTextEncodingName("UTF-8");
        websettings.setUseWideViewPort(true);
        websettings.setLoadWithOverviewMode(true);

        //设置agent规则 webview UA+空格+MamidianMerchant/版本号+空格+网络+空格+地区
        String angent = getSettings().getUserAgentString() +
                " Leanote/" + ManifestUtils.getVersionName(MyApplication.getInstance().getContext()) +
                " NetType/" + NetworkUtils.getAPNType(MyApplication.getInstance().getContext()) +
                " Language/" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        websettings.setUserAgentString(angent);

        String dir = MyApplication.getInstance().getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        websettings.setDatabaseEnabled(true);
        websettings.setDatabasePath(dir);// 设置数据库路径
        websettings.setDomStorageEnabled(true);// 使用LocalStorage则必须打开
        websettings.setGeolocationDatabasePath(dir); // 设置定位的数据库路径

        websettings.setJavaScriptCanOpenWindowsAutomatically(true);
        websettings.setGeolocationEnabled(true);

        requestFocus();
    }
}
