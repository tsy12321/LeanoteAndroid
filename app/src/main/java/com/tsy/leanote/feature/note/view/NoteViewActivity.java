package com.tsy.leanote.feature.note.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.eventbus.SyncEvent;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.feature.note.interactor.NoteFileInteractor;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    private static final String INTENT_NOTE_ID = "note_id";

    private NoteContract.Interactor mNoteInteractor;
    private NoteFileContract.Interactor mNoteFileInteractor;

    private NoteViewPreviewFragment mNoteViewPreviewFragment;
    private NoteViewEditorFragment mNoteViewEditorFragment;

    private String mNoteId;
    private Note mNote;

    private String mCurNoteTitle = "";      //当前编辑区title
    private String mCurNoteContent = "";    //当前编辑区content

    //初始加载多张图片
    private int mTotalPics = 0;
    private int mLoadedPics = 0;

    private ProgressDialog mLoadProgressDialog;

    private boolean mHasEdit = false;   //是否编辑

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ButterKnife.bind(this);

        mNoteInteractor = new NoteInteractor(this);
        mNoteFileInteractor = new NoteFileInteractor(this);

        mLoadProgressDialog = new ProgressDialog(this);
        mLoadProgressDialog.setCancelable(false);

        initToolbar();
        initViewPager();

        //获取笔记数据
        mNoteId = getIntent().getStringExtra(INTENT_NOTE_ID);
        mNote = mNoteInteractor.getNote(mNoteId);

        //获取note内容
        if(StringUtils.isEmpty(mNote.getContent())) {
            mLoadProgressDialog.show();
            mNoteInteractor.getNoteContent(MyApplication.getInstance().getUserInfo(), mNoteId,
                    new NoteContract.GetNoteContentCallback() {
                @Override
                public void onSuccess(Note note) {
                    mNote = note;
                    loadPics();
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtils.showShort(getApplicationContext(), msg);
                    mLoadProgressDialog.dismiss();
                }
            });
        } else {
            loadPics();
        }
    }

    //初始化toolbar
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化viewpager
    private void initViewPager() {
        mNoteViewPreviewFragment = new NoteViewPreviewFragment();
        mNoteViewEditorFragment = new NoteViewEditorFragment();

        mViewpager.setAdapter(new NoteViewAdapter(getSupportFragmentManager(), mNoteViewPreviewFragment, mNoteViewEditorFragment));
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    mNoteViewPreviewFragment.refreshView();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewpager.setCurrentItem(0, true);
    }

    //加载图片 并转换markdown
    private void loadPics() {
        mNoteFileInteractor.loadAllPics(MyApplication.getInstance().getUserInfo(), mNoteId, new NoteFileContract.LoadAllPicsCallback() {
            @Override
            public void onStart(int totalPics, int loadedPics) {
                if(totalPics == 0 || totalPics == loadedPics) {     //不需要加载图片
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                } else {        //开始loading 下载中
                    mTotalPics = totalPics;
                    mLoadProgressDialog.show();
                }
            }

            @Override
            public void onFinish(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                }
            }

            @Override
            public void onFailure(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                }
            }
        });
    }

    //图片加载完成
    private void loadFinish() {
        mCurNoteTitle = mNote.getTitle();
        mCurNoteContent = mNote.getContent();
        mNoteViewPreviewFragment.refreshView();
    }

    //发生变化
    public void setEdit(Boolean edit) {
        if(edit && !mHasEdit) {
            mHasEdit = true;
            MenuItem saveMenuItem = mToolbar.getMenu().findItem(R.id.action_save);
            saveMenuItem.setIcon(R.drawable.ic_action_unsave);
        }

        if(!edit && mHasEdit) {
            mHasEdit = false;
            MenuItem saveMenuItem = mToolbar.getMenu().findItem(R.id.action_save);
            saveMenuItem.setIcon(R.drawable.ic_action_save);
        }
    }

    public String getCurNoteTitle() {
        return mCurNoteTitle;
    }

    public void setCurNoteTitle(String curNoteTitle) {
        mCurNoteTitle = curNoteTitle;
    }

    public String getCurNoteContent() {
        return mCurNoteContent;
    }

    public void setCurNoteContent(String curNoteContent) {
        mCurNoteContent = curNoteContent;
    }

    public NoteFileContract.Interactor getNoteFileInteractor() {
        return mNoteFileInteractor;
    }

    public static Intent createIntent(Context context, String noteId) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra(INTENT_NOTE_ID, noteId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onExit();
                break;

            case R.id.action_switch:    //切换编辑和预览状态
                mViewpager.setCurrentItem(1-mViewpager.getCurrentItem(), true);
                break;

            case R.id.action_save:    //保存
                save(false);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        onExit();
    }

    private void onExit() {
        if(mHasEdit) {
            onNoSave();
        } else {
            finish();
        }
    }

    private void onNoSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.note_nosave_tips);
        builder.setNegativeButton(R.string.note_nosave_unsave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.note_nosave_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save(true);
            }
        });
        builder.show();
    }

    private void save(final Boolean exit) {
        if(!mHasEdit) {
            return;
        }
        Map<String, String> updateArgvs = new HashMap<>();
        updateArgvs.put("Title", mCurNoteTitle);
        updateArgvs.put("Content", mCurNoteContent);

        mLoadProgressDialog.show();
        mNoteInteractor.updateNote(MyApplication.getInstance().getUserInfo(), mNoteId, updateArgvs, new NoteContract.UpdateNoteCallback() {
            @Override
            public void onSuccess(Note note) {
                mLoadProgressDialog.dismiss();
                ToastUtils.showShort(getApplicationContext(), R.string.note_save_success);
                setEdit(false);

                EventBus.getDefault().post(new SyncEvent(SyncEvent.MSG_SYNC));

                if(exit) {
                    finish();
                }
            }

            @Override
            public void onFailure(String msg) {
                mLoadProgressDialog.dismiss();
                if(msg.equals("conflict")) {
                    ToastUtils.showShort(getApplicationContext(), R.string.note_save_conflict);
                } else {
                    ToastUtils.showShort(getApplicationContext(), msg);
                }
            }
        });
    }

    private static class NoteViewAdapter extends FragmentPagerAdapter {

        private NoteViewPreviewFragment mNoteViewPreviewFragment;
        private NoteViewEditorFragment mNoteViewEditorFragment;

        public NoteViewAdapter(FragmentManager fm, NoteViewPreviewFragment noteViewPreviewFragment,
                               NoteViewEditorFragment noteViewEditorFragment) {
            super(fm);
            mNoteViewPreviewFragment = noteViewPreviewFragment;
            mNoteViewEditorFragment = noteViewEditorFragment;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mNoteViewPreviewFragment;
            }
            return mNoteViewEditorFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
