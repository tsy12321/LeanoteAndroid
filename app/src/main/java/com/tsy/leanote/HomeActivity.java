package com.tsy.leanote;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.base.NormalInteractorCallback;
import com.tsy.leanote.feature.note.view.NoteIndexFragment;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.leanote.feature.user.view.LoginActivity;
import com.tsy.leanote.feature.webview.WebviewFragment;
import com.tsy.leanote.glide.CropCircleTransformation;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.nav_view)
    NavigationView nav_view;

    ImageView img_avatar;
    TextView txt_username;
    TextView txt_email;

    private UserContract.Interactor mUserInteractor;
    private UserInfo mUserInfo;

    private NoteIndexFragment mNoteIndexFragment;
    private WebviewFragment mWebviewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mUserInteractor = new UserInteractor(this);
        mUserInfo = mUserInteractor.getCurUser();

        //init toolbar
        toolbar.setTitle(R.string.toolbar_title_note);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //init navigationview
        nav_view.setNavigationItemSelectedListener(this);
        View headerView = nav_view.getHeaderView(0);
        img_avatar = (ImageView) headerView.findViewById(R.id.img_avatar);
        txt_username = (TextView) headerView.findViewById(R.id.txt_username);
        txt_email = (TextView) headerView.findViewById(R.id.txt_email);
        nav_view.getMenu().getItem(0).setChecked(true);   //设置第一个menu为选中

        Glide.with(this)
                .load(mUserInfo.getLogo())
                .bitmapTransform(new CropCircleTransformation(this))
                .into(img_avatar);
        txt_username.setText(mUserInfo.getUsername());
        txt_email.setText(mUserInfo.getEmail());

        //Default Switch To Note
        switchNote();
    }

    /**
     * 个人笔记
     */
    private void switchNote() {
        toolbar.setTitle(R.string.toolbar_title_note);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mNoteIndexFragment == null) {
            mNoteIndexFragment = new NoteIndexFragment();
            transaction.add(R.id.fl_content, mNoteIndexFragment, "NoteIndexFragment");
        }

        if(mWebviewFragment != null) {
            transaction.hide(mWebviewFragment);
        }

        transaction.show(mNoteIndexFragment);
        transaction.commit();
    }

    /**
     * 博客
     */
    private void switchBlog() {
        toolbar.setTitle(R.string.toolbar_title_blog);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mWebviewFragment == null) {
            mWebviewFragment = new WebviewFragment();
            transaction.add(R.id.fl_content, mWebviewFragment, "WebviewFragment");
        }

        if(mNoteIndexFragment != null) {
            transaction.hide(mNoteIndexFragment);
        }

        transaction.show(mWebviewFragment);
        transaction.commit();
    }

    /**
     * 探索
     */
    private void switchLee() {
        toolbar.setTitle(R.string.toolbar_title_lee);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mWebviewFragment == null) {
            mWebviewFragment = new WebviewFragment();
            transaction.add(R.id.fl_content, mWebviewFragment, "WebviewFragment");
        }

        if(mNoteIndexFragment != null) {
            transaction.hide(mNoteIndexFragment);
        }

        transaction.show(mWebviewFragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_note:
                drawer_layout.closeDrawer(GravityCompat.START);
                switchNote();
                break;

            case R.id.nav_blog:
                drawer_layout.closeDrawer(GravityCompat.START);
                switchBlog();
                break;

            case R.id.nav_lee:
                drawer_layout.closeDrawer(GravityCompat.START);
                switchLee();
                break;

            case R.id.nav_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.user_exit_dialog_title);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExit();
                    }
                });
                builder.show();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void doExit() {
        mUserInteractor.logout(mUserInfo, new NormalInteractorCallback() {
            @Override
            public void onSuccess() {
                startActivity(LoginActivity.createIntent(HomeActivity.this));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showShort(getApplicationContext(), msg);
            }
        });
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
