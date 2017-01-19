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
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.note.interactor.NotebookInteractor;

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

    private ArrayList<NotebookAdapter.MyNotebook> mMyNotebooks = new ArrayList<>();

    private NotebookAdapter mNotebookAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notebook, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        mNotebookInteractor = new NotebookInteractor(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNotebookAdapter = new NotebookAdapter(getActivity(), mMyNotebooks);
        mNotebookAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mNotebookAdapter);

        //获取第一级目录
        ArrayList<Notebook> notebooks = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(), "");
        for(int i = 0; i < notebooks.size(); i ++) {
            NotebookAdapter.MyNotebook myNotebook = addMyNotebook(notebooks.get(i));
            myNotebook.setDepth(0);
            mMyNotebooks.add(myNotebook);
        }
        mNotebookAdapter.notifyDataSetChanged();

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

        if(myNotebook.getChildNotebookNum() == 0) {
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

    //显示子notebook
    private void showChildNotebook(String notebookId, int position, int depth) {
        ArrayList<Notebook> notebooks = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(), notebookId);
        for(int i = 0; i < notebooks.size(); i ++) {
            NotebookAdapter.MyNotebook myNotebook = addMyNotebook(notebooks.get(i));
            myNotebook.setDepth(depth + 1);
            mMyNotebooks.add(position + i + 1, myNotebook);
        }
    }

    //隐藏子notebook
    private void hideChildNotebook(String notebookId) {
        Iterator<NotebookAdapter.MyNotebook> iter = mMyNotebooks.iterator();
        while(iter.hasNext()){
            NotebookAdapter.MyNotebook myNotebook = iter.next();
            if(myNotebook.getNotebook().getParent_notebookid().equals(notebookId)) {
                hideChildNotebook(myNotebook.getNotebook().getNotebookid());
                myNotebook.setShow(false);
            }
        }
    }

    //根据notebook返回mynotebook
    private NotebookAdapter.MyNotebook addMyNotebook(Notebook notebook) {
        NotebookAdapter.MyNotebook myNotebook = new NotebookAdapter.MyNotebook();
        myNotebook.setNotebook(notebook);
        int childNotebookNum = mNotebookInteractor.getNotebooks(MyApplication.getInstance().getUserInfo(), notebook.getNotebookid()).size();
        myNotebook.setChildNotebookNum(childNotebookNum);

        return myNotebook;
    }
}
