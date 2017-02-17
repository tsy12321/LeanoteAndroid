package com.tsy.leanote.feature.note.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.feature.note.interactor.NoteFileInteractor;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.feras.mdv.MarkdownView;

public class NoteViewActivity extends BaseActivity {

    @BindView(R.id.markdownView)
    MarkdownView mMarkdownView;

    private static final String INTENT_NOTE_ID = "note_id";

    private NoteContract.Interactor mNoteInteractor;
    private NoteFileContract.Interactor mNoteFileInteractor;

    private String mNoteId;
    private String mNoteContent;
    private Note mNote;
    private int mTotalPics = 0;
    private int mLoadedPics = 0;

    private ProgressDialog mLoadProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ButterKnife.bind(this);

        mLoadProgressDialog = new ProgressDialog(this);
        mLoadProgressDialog.setCancelable(false);

        mNoteId = getIntent().getStringExtra(INTENT_NOTE_ID);

        mNoteInteractor = new NoteInteractor(this);
        mNoteFileInteractor = new NoteFileInteractor(this);

        mNote = mNoteInteractor.getNote(mNoteId);

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
    }

    //加载图片 并转换markdown
    private void loadPics() {
        mNoteFileInteractor.loadAllPics(MyApplication.getInstance().getUserInfo(), mNoteId, new NoteFileContract.LoadAllPicsCallback() {
            @Override
            public void onStart(int totalPics, int loadedPics) {
                if(totalPics == 0 || totalPics == loadedPics) {     //不需要加载图片
                    mLoadProgressDialog.dismiss();
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
                    refreshView();
                }
            }

            @Override
            public void onFailure(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
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
        mMarkdownView.loadMarkdown(mNoteContent);
    }

    public static Intent createIntent(Context context, String noteId) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra(INTENT_NOTE_ID, noteId);
        return intent;
    }
}
