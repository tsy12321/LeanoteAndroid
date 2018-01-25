package com.tsy.leanote.widget.webview;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kingja.loadsir.core.LoadService;
import com.tsy.leanote.widget.loadsir.ErrorCallback;
import com.tsy.leanote.widget.loadsir.LoadingCallback;

/**
 * custom webviewcilent
 * Created by tsy on 16/8/3.
 */
public class MyWebViewClient extends WebViewClient {

    private LoadService mLoadService;

    public MyWebViewClient(LoadService loadService) {
        mLoadService = loadService;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.getSettings().setBlockNetworkImage(false);
        if(mLoadService.getCurrentCallback().equals(LoadingCallback.class)) {
            mLoadService.showSuccess();
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        view.getSettings().setBlockNetworkImage(true);
        mLoadService.showCallback(LoadingCallback.class);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        mLoadService.showCallback(ErrorCallback.class);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
