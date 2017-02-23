package com.tsy.leanote.feature.note.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.feature.note.interactor.NoteFileInteractor;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.leanote.widget.MarkdownPreviewView;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txtTitle)
    TextView mTxtTitle;

    @BindView(R.id.markdownPreviewView)
    MarkdownPreviewView mMarkdownPreviewView;

    private static final String INTENT_NOTE_ID = "note_id";

    private NoteContract.Interactor mNoteInteractor;
    private NoteFileContract.Interactor mNoteFileInteractor;

    private String mNoteId;
    private String mNoteContent = "";
    private Note mNote;
    private int mTotalPics = 0;
    private int mLoadedPics = 0;
    private boolean mContentloadFinished = false;
    private boolean mWebloadFinished = false;

    private ProgressDialog mLoadProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ButterKnife.bind(this);

        mNoteInteractor = new NoteInteractor(this);
        mNoteFileInteractor = new NoteFileInteractor(this);

        initToolbar();

        mLoadProgressDialog = new ProgressDialog(this);
        mLoadProgressDialog.setCancelable(false);

        //获取笔记数据
        mNoteId = getIntent().getStringExtra(INTENT_NOTE_ID);
        mNote = mNoteInteractor.getNote(mNoteId);

        mTxtTitle.setText(mNote.getTitle());

        //获取note内容
        if(StringUtils.isEmpty(mNote.getContent())) {
            mLoadProgressDialog.show();
            mNoteInteractor.getNoteContent(MyApplication.getInstance().getUserInfo(), mNoteId,
                    new NoteContract.GetNoteContentCallback() {
                @Override
                public void onSuccess(Note note) {
                    mNote = note;
                    parseContent();
                    loadPics();
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtils.showShort(getApplicationContext(), msg);
                    mLoadProgressDialog.dismiss();
                }
            });
        } else {
            parseContent();
            loadPics();
        }

        mMarkdownPreviewView.setOnLoadingFinishListener(new MarkdownPreviewView.OnLoadingFinishListener() {
            @Override
            public void onLoadingFinish() {
                mWebloadFinished = true;
                refreshView();
            }
        });
    }

    private void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //加载图片 并转换markdown
    private void loadPics() {
        mNoteFileInteractor.loadAllPics(MyApplication.getInstance().getUserInfo(), mNoteId, new NoteFileContract.LoadAllPicsCallback() {
            @Override
            public void onStart(int totalPics, int loadedPics) {
                if(totalPics == 0 || totalPics == loadedPics) {     //不需要加载图片
                    mLoadProgressDialog.dismiss();
                    mContentloadFinished = true;
                    refreshView();
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
                    mContentloadFinished = true;
                    refreshView();
                }
            }

            @Override
            public void onFailure(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
                    mContentloadFinished = true;
                    refreshView();
                }
            }
        });
    }

    //把文章内容图片格式转化为本地图片格式
    private void parseContent() {
        mNoteContent = mNote.getContent();
        mNoteContent = mNoteContent.replaceAll("!\\[(.*)\\]\\(http:\\/\\/leanote.com\\/file\\/outputImage\\?fileId=(.*)\\)",
                "![$1](" + mNoteFileInteractor.getPicWebviewPath("$2") + ")");
        mNoteContent = mNoteContent.replaceAll("!\\[(.*)\\]\\(http:\\/\\/leanote.com\\/api\\/file\\/getImage\\?fileId=(.*)\\)",
                "![$1](" + mNoteFileInteractor.getPicWebviewPath("$2") + ")");
        mNoteContent = mNoteContent.replaceAll("!\\[(.*)\\]\\(https:\\/\\/leanote.com\\/file\\/outputImage\\?fileId=(.*)\\)",
                "![$1](" + mNoteFileInteractor.getPicWebviewPath("$2") + ")");
        mNoteContent = mNoteContent.replaceAll("!\\[(.*)\\]\\(https:\\/\\/leanote.com\\/api\\/file\\/getImage\\?fileId=(.*)\\)",
                "![$1](" + mNoteFileInteractor.getPicWebviewPath("$2") + ")");
    }

    private void refreshView() {
        if(mContentloadFinished && mWebloadFinished) {
            mMarkdownPreviewView.parseMarkdown(mNoteContent, true);
        }
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
                finish();
                break;

            case R.id.action_switch:    //切换编辑和预览状态
                break;

            case R.id.action_save:    //保存
                break;
        }

        return true;
    }
}
