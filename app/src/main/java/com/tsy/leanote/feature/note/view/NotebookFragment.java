package com.tsy.leanote.feature.note.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseFragment;
import com.tsy.leanote.eventbus.SyncEvent;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.leanote.feature.note.interactor.NotebookInteractor;
import com.tsy.leanote.feature.note.view.adapter.NotebookAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2016/12/22.
 */

public class NotebookFragment extends BaseFragment implements NotebookAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private View mView;
    private Unbinder mUnbinder;

    private NotebookContract.Interactor mNotebookInteractor;
    private NoteContract.Interactor mNoteInteractor;

    private ArrayList<NotebookAdapter.MyNotebook> mMyNotebooks = new ArrayList<>();

    private NotebookAdapter mNotebookAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notebook, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        mNotebookInteractor = new NotebookInteractor(this);
        mNoteInteractor = new NoteInteractor(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNotebookAdapter = new NotebookAdapter(getActivity(), mMyNotebooks);
        mNotebookAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mNotebookAdapter);

        refreshNotebooks();

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClick(View view, int position) {
        NotebookAdapter.MyNotebook myNotebook = mMyNotebooks.get(position);

        if(myNotebook.getNote() != null) {
            startActivity(NoteViewActivity.createIntent(getActivity(), myNotebook.getNote().getNoteid()));
            return;
        }

        if(myNotebook.getChildNotebookNum() == 0 && myNotebook.getChildNoteNum() == 0) {
            return;
        }

        if(!myNotebook.isOpen()) {
            showChildNotebook(myNotebook.getNotebook().getNotebookid(), position, myNotebook.getDepth());
            myNotebook.setOpen(true);
        } else {
            hideChildNotebook(myNotebook.getNotebook().getNotebookid());
            Iterator<NotebookAdapter.MyNotebook> iter = mMyNotebooks.iterator();
            while(iter.hasNext()){
                if(!iter.next().isShow()) {
                    iter.remove();
                }
            }
            myNotebook.setOpen(false);
        }

        mNotebookAdapter.notifyDataSetChanged();
    }

    //显示子notebook和子note
    private void showChildNotebook(String notebookId, int position, int depth) {
        //查找子notebook
        ArrayList<Notebook> notebooks = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(), notebookId);
        for(int i = 0; i < notebooks.size(); i ++) {
            NotebookAdapter.MyNotebook myNotebook = new NotebookAdapter.MyNotebook();
            myNotebook.setNotebook(notebooks.get(i));

            //子notebook数量
            int childNotebookNum = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(),
                    notebooks.get(i).getNotebookid()).size();
            myNotebook.setChildNotebookNum(childNotebookNum);

            //子note数量
            int childNoteNum = mNoteInteractor.getNotesByNotebookId(MyApplication.getInstance().getUserInfo(),
                    notebooks.get(i).getNotebookid()).size();
            myNotebook.setChildNoteNum(childNoteNum);

            //设置深度
            myNotebook.setDepth(depth + 1);
            position++;
            mMyNotebooks.add(position, myNotebook);
        }

        //查找子note
        ArrayList<Note> notes = mNoteInteractor.getNotesByNotebookId(MyApplication.getInstance().getUserInfo(), notebookId);
        for(int i = 0; i < notes.size(); i ++) {
            NotebookAdapter.MyNotebook myNotebook = new NotebookAdapter.MyNotebook();
            myNotebook.setNote(notes.get(i));
            myNotebook.setDepth(depth + 1);
            position++;
            mMyNotebooks.add(position, myNotebook);
        }
    }

    //隐藏子notebook和note
    private void hideChildNotebook(String notebookId) {
        Iterator<NotebookAdapter.MyNotebook> iter = mMyNotebooks.iterator();
        while(iter.hasNext()){
            NotebookAdapter.MyNotebook myNotebook = iter.next();
            if(myNotebook.getNotebook() != null) {
                if(myNotebook.getNotebook().getParent_notebookid().equals(notebookId)) {
                    hideChildNotebook(myNotebook.getNotebook().getNotebookid());
                    myNotebook.setShow(false);
                }
            } else {
                if(myNotebook.getNote().getNotebookid().equals(notebookId)) {
                    myNotebook.setShow(false);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        switch (event.getMsg()) {
            case SyncEvent.MSG_REFRESH:
                refreshNotebooks();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void refreshNotebooks() {
        mMyNotebooks.clear();
        //获取第一级目录
        ArrayList<Notebook> notebooks = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(), "");
        for(int i = 0; i < notebooks.size(); i ++) {
            NotebookAdapter.MyNotebook myNotebook = new NotebookAdapter.MyNotebook();
            myNotebook.setNotebook(notebooks.get(i));

            //子notebook数量
            int childNotebookNum = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(),
                    notebooks.get(i).getNotebookid()).size();
            myNotebook.setChildNotebookNum(childNotebookNum);

            //子note数量
            int childNoteNum = mNoteInteractor.getNotesByNotebookId(MyApplication.getInstance().getUserInfo(),
                    notebooks.get(i).getNotebookid()).size();
            myNotebook.setChildNoteNum(childNoteNum);

            //设置深度
            myNotebook.setDepth(0);
            mMyNotebooks.add(myNotebook);
        }
        mNotebookAdapter.notifyDataSetChanged();
    }
}
