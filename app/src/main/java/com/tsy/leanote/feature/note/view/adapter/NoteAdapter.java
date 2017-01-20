package com.tsy.leanote.feature.note.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsy.leanote.R;
import com.tsy.leanote.feature.note.bean.Note;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tsy on 2017/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> implements View.OnClickListener {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<Note> mMyNotes;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public NoteAdapter(Context context, ArrayList<Note> myNotes) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMyNotes = myNotes;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public NoteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_note, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.MyViewHolder holder, int position) {
        Note myNote = mMyNotes.get(position);

        holder.itemView.setTag(position);
        holder.mTitle.setText(myNote.getTitle());

        String updateTime = myNote.getUpdated_time().replace('T', ' ');
        updateTime = updateTime.substring(0, 19);
        holder.mTime.setText(updateTime);
    }

    @Override
    public int getItemCount() {
        return mMyNotes == null ? 0 : mMyNotes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_title)
        TextView mTitle;

        @BindView(R.id.txt_time)
        TextView mTime;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
