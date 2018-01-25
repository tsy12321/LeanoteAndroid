package com.tsy.leanote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.base.NormalInteractorCallback;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.eventbus.SyncEvent;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.leanote.feature.note.interactor.NotebookInteractor;
import com.tsy.leanote.feature.note.view.NoteIndexFragment;
import com.tsy.leanote.feature.note.view.NoteViewActivity;
import com.tsy.leanote.feature.user.bean.UserInfo;
import com.tsy.leanote.feature.user.contract.UserContract;
import com.tsy.leanote.feature.user.interactor.UserInteractor;
import com.tsy.leanote.feature.user.view.LoginActivity;
import com.tsy.leanote.widget.webview.WebviewFragment;
import com.tsy.leanote.glide.CropCircleTransformation;
import com.tsy.sdk.myutil.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.nav_view)
    NavigationView nav_view;

    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;

    ImageView img_avatar;
    TextView txt_username;
    TextView txt_email;

    private UserInfo mUserInfo;

    private UserContract.Interactor mUserInteractor;
    private NotebookContract.Interactor mNotebookInteractor;
    private NoteContract.Interactor mNoteInteractor;

    private NoteIndexFragment mNoteIndexFragment;
    private WebviewFragment mBlogWebviewFragment;
    private WebviewFragment mLeeWebviewFragment;

    private ProgressDialog mSyncProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mSyncProgressDialog = new ProgressDialog(this);
        mSyncProgressDialog.setCancelable(false);
        mSyncProgressDialog.setMessage(getString(R.string.sync_ing));

        mUserInteractor = new UserInteractor(this);
        mUserInfo = mUserInteractor.getCurUser();
        MyApplication.getInstance().setUserInfo(mUserInfo);

        mNotebookInteractor = new NotebookInteractor(this);
        mNoteInteractor = new NoteInteractor(this);

        //init toolbar
        toolbar.setTitle(R.string.toolbar_title_note);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
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
                .placeholder(R.drawable.default_avatar)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(img_avatar);
        txt_username.setText(mUserInfo.getUsername());
        txt_email.setText(mUserInfo.getEmail());

        //Default Switch To Note
        switchNote();

        //每次进入同步一次
        doSync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick(R.id.fab_add)
    public void addNote() {
        startActivity(NoteViewActivity.createIntent(this));
    }

    /**
     * 个人笔记
     */
    private void switchNote() {
        toolbar.setTitle(R.string.toolbar_title_note);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mNoteIndexFragment == null) {
            mNoteIndexFragment = new NoteIndexFragment();
            transaction.add(R.id.fl_content, mNoteIndexFragment, "NoteIndexFragment");
        }

        if(mBlogWebviewFragment != null) {
            transaction.hide(mBlogWebviewFragment);
        }
        if(mLeeWebviewFragment != null) {
            transaction.hide(mLeeWebviewFragment);
        }

        transaction.show(mNoteIndexFragment);
        transaction.commit();
    }

    /**
     * 博客
     */
    private void switchBlog() {
        toolbar.setTitle(R.string.toolbar_title_blog);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mBlogWebviewFragment == null) {
            mBlogWebviewFragment = new WebviewFragment();
            mBlogWebviewFragment.setArguments(WebviewFragment.createArguments(EnvConstant.HOST + "/blog/" + mUserInfo.getEmail()));

            transaction.add(R.id.fl_content, mBlogWebviewFragment, "BlogWebviewFragment");
        }

        if(mNoteIndexFragment != null) {
            transaction.hide(mNoteIndexFragment);
        }
        if(mLeeWebviewFragment != null) {
            transaction.hide(mLeeWebviewFragment);
        }

        transaction.show(mBlogWebviewFragment);
        transaction.commit();
    }

    /**
     * 探索
     */
    private void switchLee() {
        toolbar.setTitle(R.string.toolbar_title_lee);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mLeeWebviewFragment == null) {
            mLeeWebviewFragment = new WebviewFragment();
            mLeeWebviewFragment.setArguments(WebviewFragment.createArguments("http://lea.leanote.com/index"));

            transaction.add(R.id.fl_content, mLeeWebviewFragment, "LeeWebviewFragment");
        }

        if(mNoteIndexFragment != null) {
            transaction.hide(mNoteIndexFragment);
        }
        if(mBlogWebviewFragment != null) {
            transaction.hide(mBlogWebviewFragment);
        }

        transaction.show(mLeeWebviewFragment);
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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                doSync();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        switch (event.getMsg()) {
            case SyncEvent.MSG_SYNC:
                doSync();
                break;
        }
    }

    //同步
    private void doSync() {
        mSyncProgressDialog.show();

        mUserInteractor.getSyncState(mUserInfo, new UserContract.GetSyncStateCallback() {
            @Override
            public void onSuccess(final int lastSyncUsn) {
                //判断是否需要更新
                if(mUserInfo.getLast_usn() >= lastSyncUsn) {
                    mSyncProgressDialog.dismiss();
                    return;
                }

                //开始同步
                mNotebookInteractor.sync(mUserInfo, new NotebookContract.GetNotebooksCallback() {
                    @Override
                    public void onSuccess(List<Notebook> notebooks) {
                        mNoteInteractor.sync(mUserInfo, new NoteContract.GetNotesCallback() {
                            @Override
                            public void onSuccess(List<Note> notes) {
                                //同步成功 更新lastSyncUsn
                                mUserInteractor.updateLastSyncUsn(mUserInfo, lastSyncUsn);
                                mSyncProgressDialog.dismiss();

                                Logger.i("Sync Usn %s", lastSyncUsn);

                                EventBus.getDefault().post(new SyncEvent(SyncEvent.MSG_REFRESH));
                            }

                            @Override
                            public void onFailure(String msg) {
                                mSyncProgressDialog.dismiss();
                                ToastUtils.showShort(getApplicationContext(), msg);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String msg) {
                        mSyncProgressDialog.dismiss();
                        ToastUtils.showShort(getApplicationContext(), msg);
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                mSyncProgressDialog.dismiss();
                ToastUtils.showShort(getApplicationContext(), msg);
            }
        });

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
