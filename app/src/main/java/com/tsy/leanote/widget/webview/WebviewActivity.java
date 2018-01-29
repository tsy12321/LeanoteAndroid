package com.tsy.leanote.widget.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.sdk.myutil.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private final static String EXTRA_URL_KEY = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra(EXTRA_URL_KEY);
        if(StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("empty url");
        }

        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        WebviewFragment webviewFragment = new WebviewFragment();
        webviewFragment.setArguments(WebviewFragment.createArguments(url));
        fragmentTransaction.add(R.id.fl_content, webviewFragment, "webview");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onExit();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        onExit();
    }

    private void onExit() {
        finish();
    }

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra(EXTRA_URL_KEY, url);
        return intent;
    }
}
