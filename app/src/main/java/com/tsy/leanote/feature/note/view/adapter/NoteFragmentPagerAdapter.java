package com.tsy.leanote.feature.note.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tsy.leanote.R;
import com.tsy.leanote.feature.note.view.NoteFragment;
import com.tsy.leanote.feature.note.view.NotebookFragment;

/**
 * Created by tsy on 2016/12/22.
 */

public class NoteFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private int[] mTabTitles = new int[]{R.string.tab_title_note, R.string.tab_title_notebooke};
    private Fragment[] mTabFragments = new Fragment[]{new NoteFragment(), new NotebookFragment()};
    private Context mContext;

    public NoteFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabFragments[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mTabTitles[position]);
    }
}
