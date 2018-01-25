package com.tsy.leanote.widget.webview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseFragment;
import com.tsy.leanote.widget.webview.MyWebView;
import com.tsy.leanote.widget.webview.MyWebViewClient;
import com.tsy.sdk.myutil.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2016/12/22.
 */

public class WebviewFragment extends BaseFragment {

    @BindView(R.id.mywebview)
    MyWebView myWebView;

    private View mView;
    private Unbinder mUnbinder;

    private String mUrl;
    private LoadService mLoadService;

    private final static String ARGS_URL_KEY = "url";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args == null || StringUtils.isEmpty(args.getString(ARGS_URL_KEY))) {
            throw new IllegalArgumentException("empty url");
        }
        mUrl = args.getString(ARGS_URL_KEY);

        mView = inflater.inflate(R.layout.fragment_webview, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        mLoadService = LoadSir.getDefault().register(mView, new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                // 重新加载
                myWebView.reload();
            }
        });

        myWebView.initSetting();
        myWebView.setWebViewClient(new MyWebViewClient(mLoadService));

        myWebView.loadUrl(mUrl);

        return mLoadService.getLoadLayout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        if(myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return false;
    }

    /**
     * 创建参数Bundle
     * @param url
     * @return
     */
    public static Bundle createArguments(String url) {
        Bundle args = new Bundle();
        args.putString(ARGS_URL_KEY, url);
        return args;
    }
}
