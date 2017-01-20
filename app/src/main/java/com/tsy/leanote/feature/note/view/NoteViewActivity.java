package com.tsy.leanote.feature.note.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.contract.NoteContract;
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

    private String mNoteId;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ButterKnife.bind(this);

        mNoteId = getIntent().getStringExtra(INTENT_NOTE_ID);

        mNoteInteractor = new NoteInteractor();
        mNote = mNoteInteractor.getNote(mNoteId);

        //获取note内容
        if(StringUtils.isEmpty(mNote.getContent())) {
            mNoteInteractor.getNoteContent(MyApplication.getInstance().getUserInfo(), mNoteId,
                    new NoteContract.GetNoteContentCallback() {
                @Override
                public void onSuccess(Note note) {
                    mNote = note;
                    refreshView();
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtils.showShort(getApplicationContext(), msg);
                }
            });
        } else {
            refreshView();
        }
    }

    private void refreshView() {
        mMarkdownView.loadMarkdown(mNote.getContent());
    }

    public static Intent createIntent(Context context, String noteId) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra(INTENT_NOTE_ID, noteId);
        return intent;
    }
}
