package com.tsy.leanote.feature.note.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2017/2/23.
 */

public class NoteViewEditorFragment extends BaseFragment {

    @BindView(R.id.txt_title)
    EditText mTxtTitle;

    @BindView(R.id.txt_content)
    EditText mTxtContent;

    private View mView;
    private Unbinder mUnbinder;
    private NoteViewActivity mNoteViewActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note_editor, container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        mNoteViewActivity = (NoteViewActivity) getActivity();

        mTxtTitle.setText(mNoteViewActivity.getCurNoteTitle());
        mTxtTitle.setSelection(mTxtTitle.getText().length());
        mTxtContent.setText(mNoteViewActivity.getCurNoteContent());
        
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
