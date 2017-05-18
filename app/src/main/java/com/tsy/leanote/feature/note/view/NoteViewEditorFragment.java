package com.tsy.leanote.feature.note.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tsy.leanote.MyApplication;
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

        mTxtTitle.addTextChangedListener(textWatcher);
        mTxtContent.addTextChangedListener(textWatcher);

        return mView;
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mNoteViewActivity.setEdit();
            mNoteViewActivity.setCurNoteTitle(mTxtTitle.getText().toString());
            mNoteViewActivity.setCurNoteContent(mTxtContent.getText().toString());
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && !isVisibleToUser) {
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        }
    }
}
