package com.tsy.leanote.feature.note.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseFragment;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.widget.MarkdownPreviewView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2017/2/23.
 */

public class NoteViewPreviewFragment extends BaseFragment {

    @BindView(R.id.txtTitle)
    TextView mTxtTitle;

    @BindView(R.id.markdownPreviewView)
    MarkdownPreviewView mMarkdownPreviewView;

    private View mView;
    private Unbinder mUnbinder;
    private NoteViewActivity mNoteViewActivity;

    private boolean mWebLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note_preview, container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        mNoteViewActivity = (NoteViewActivity) getActivity();
        mMarkdownPreviewView.setOnLoadingFinishListener(new MarkdownPreviewView.OnLoadingFinishListener() {
            @Override
            public void onLoadingFinish() {
                mWebLoaded = true;
                refreshView();
            }
        });

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    //刷新preview页面
    public void refreshView() {
        if(!mWebLoaded) {
            return;
        }
        mTxtTitle.setText(mNoteViewActivity.getCurNoteTitle());
        String content = parseContent(mNoteViewActivity.getCurNoteContent());
        mMarkdownPreviewView.parseMarkdown(content, true);
    }

    //把文章内容图片格式转化为本地图片格式
    private String parseContent(String content) {
        NoteFileContract.Interactor noteFileInteractor = mNoteViewActivity.getNoteFileInteractor();
        content = content.replaceAll("!\\[(.*)\\]\\(http:\\/\\/leanote.com\\/file\\/outputImage\\?fileId=(.*)\\)",
                "![$1](" + noteFileInteractor.getPicWebviewPath("$2") + ")");
        content = content.replaceAll("!\\[(.*)\\]\\(http:\\/\\/leanote.com\\/api\\/file\\/getImage\\?fileId=(.*)\\)",
                "![$1](" + noteFileInteractor.getPicWebviewPath("$2") + ")");
        content = content.replaceAll("!\\[(.*)\\]\\(https:\\/\\/leanote.com\\/file\\/outputImage\\?fileId=(.*)\\)",
                "![$1](" + noteFileInteractor.getPicWebviewPath("$2") + ")");
        content = content.replaceAll("!\\[(.*)\\]\\(https:\\/\\/leanote.com\\/api\\/file\\/getImage\\?fileId=(.*)\\)",
                "![$1](" + noteFileInteractor.getPicWebviewPath("$2") + ")");

        return content;
    }
}
