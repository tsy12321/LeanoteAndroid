package com.tsy.leanote.feature.note.view;

import android.content.Intent;
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
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.leanote.feature.note.view.adapter.NoteAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2016/12/22.
 */

public class NoteFragment extends BaseFragment implements NoteAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private View mView;
    private Unbinder mUnbinder;

    private NoteContract.Interactor mNoteInteractor;

    private ArrayList<Note> mNotes = new ArrayList<>();

    private NoteAdapter mNoteAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        mNoteInteractor = new NoteInteractor(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoteAdapter = new NoteAdapter(getActivity(), mNotes);
        mNoteAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mNoteAdapter);

        refreshNote();

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClick(View view, int position) {
        startActivity(new Intent(getActivity(), NoteViewActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        switch (event.getMsg()) {
            case SyncEvent.MSG_SYNC:
                refreshNote();
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

    private void refreshNote() {
        mNotes.clear();
        mNotes.addAll(mNoteInteractor.getNotesOrderNewest(MyApplication.getInstance().getUserInfo()));
        mNoteAdapter.notifyDataSetChanged();
    }
}
