package com.tsy.leanote.feature.note.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tsy on 2016/12/22.
 */

public class NoteIndexFragment extends BaseFragment {

    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    private View mView;
    private Unbinder mUnbinder;
    private NoteFragmentPagerAdapter mPageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_note_index, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        mPageAdapter = new NoteFragmentPagerAdapter(getChildFragmentManager(), getActivity().getApplicationContext());
        mViewpager.setAdapter(mPageAdapter);
        mTabs.setupWithViewPager(mViewpager);
        mTabs.setTabMode(TabLayout.MODE_FIXED);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
