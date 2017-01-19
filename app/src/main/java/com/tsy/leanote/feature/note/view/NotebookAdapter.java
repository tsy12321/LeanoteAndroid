package com.tsy.leanote.feature.note.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsy.leanote.R;
import com.tsy.leanote.feature.note.bean.Notebook;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tsy on 2017/1/18.
 */

public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.MyViewHolder> implements View.OnClickListener {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<MyNotebook> mMyNotebooks;
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

    public NotebookAdapter(Context context, ArrayList<MyNotebook> myNotebooks) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMyNotebooks = myNotebooks;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public NotebookAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_notebook, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(NotebookAdapter.MyViewHolder holder, int position) {
        holder.mName.setText(mMyNotebooks.get(position).getNotebook().getTitle());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mMyNotebooks == null ? 0 : mMyNotebooks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_name)
        TextView mName;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class MyNotebook {
        Notebook mNotebook;
        boolean mIsOpen = false;
        boolean mIsShow = true;

        public Notebook getNotebook() {
            return mNotebook;
        }

        public void setNotebook(Notebook notebook) {
            mNotebook = notebook;
        }

        public boolean isOpen() {
            return mIsOpen;
        }

        public void setOpen(boolean open) {
            mIsOpen = open;
        }

        public boolean isShow() {
            return mIsShow;
        }

        public void setShow(boolean show) {
            mIsShow = show;
        }
    }
}
