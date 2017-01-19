package com.tsy.leanote.feature.note.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsy.leanote.R;
import com.tsy.leanote.feature.note.bean.Notebook;
import com.tsy.sdk.myutil.DeviceUtils;

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
        MyNotebook myNotebook = mMyNotebooks.get(position);
        holder.mName.setText(myNotebook.getNotebook().getTitle());
        holder.itemView.setTag(position);
        if(myNotebook.isOpen()) {
            holder.mImgArrowDown.setScaleY(-1);
        } else {
            holder.mImgArrowDown.setScaleY(1);
        }
        if(myNotebook.getDepth() == 0) {
            holder.mRlBackground.setBackgroundColor(0xFFFFFF);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mName.getLayoutParams();
            layoutParams.leftMargin = DeviceUtils.dip2px(mContext, 20);
            holder.mName.setLayoutParams(layoutParams);
        } else {
            holder.mRlBackground.setBackgroundColor(0x10000000);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mName.getLayoutParams();
            layoutParams.leftMargin = DeviceUtils.dip2px(mContext, 20 * (myNotebook.getDepth() + 1));
            holder.mName.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return mMyNotebooks == null ? 0 : mMyNotebooks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.img_arrow_down)
        ImageView mImgArrowDown;

        @BindView(R.id.rl_background)
        RelativeLayout mRlBackground;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class MyNotebook {
        Notebook mNotebook;
        boolean mIsOpen = false;        //目录是否打开
        boolean mIsShow = true;     //待关闭删除的item标识
        int depth = 0;      //目录深度

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

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }
    }
}
