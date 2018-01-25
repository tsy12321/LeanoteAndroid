package com.tsy.leanote.feature.note.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;
import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.constant.EnvConstant;
import com.tsy.leanote.eventbus.NoteEvent;
import com.tsy.leanote.eventbus.SyncEvent;
import com.tsy.leanote.feature.note.bean.Note;
import com.tsy.leanote.feature.note.bean.NoteFile;
import com.tsy.leanote.feature.note.contract.NoteContract;
import com.tsy.leanote.feature.note.contract.NoteFileContract;
import com.tsy.leanote.feature.note.contract.NotebookContract;
import com.tsy.leanote.feature.note.interactor.NoteFileInteractor;
import com.tsy.leanote.feature.note.interactor.NoteInteractor;
import com.tsy.leanote.feature.note.interactor.NotebookInteractor;
import com.tsy.leanote.widget.TabIconView;
import com.tsy.sdk.myutil.StringUtils;
import com.tsy.sdk.myutil.ToastUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.security.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteViewActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    @BindView(R.id.action_other_operate)
    protected ExpandableLayout mExpandLayout;

    private TabIconView mTabIconView;

    private static final String INTENT_NOTE_ID = "note_id";

    private NoteContract.Interactor mNoteInteractor;
    private NoteFileContract.Interactor mNoteFileInteractor;
    private NotebookContract.Interactor mNotebookInteractor;

    private NoteViewPreviewFragment mNoteViewPreviewFragment;
    private NoteViewEditorFragment mNoteViewEditorFragment;

    private String mNoteId = "";
    private Note mNote;

    private String mCurNoteTitle = "";      //当前编辑区title
    private String mCurNoteContent = "";    //当前编辑区content
    private String mCurNotebookid = "";    //当前编辑区笔记本id
    private String mCurNotebookpath = "";    //当前编辑区笔记本path

    //初始加载多张图片
    private int mTotalPics = 0;
    private int mLoadedPics = 0;

    private ProgressDialog mLoadProgressDialog;

    private boolean mHasEdit = false;   //是否编辑

    private final int RC_SYSTEM_GALLERY = 1;
    private final int RC_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ButterKnife.bind(this);

        mNoteInteractor = new NoteInteractor(this);
        mNoteFileInteractor = new NoteFileInteractor(this);
        mNotebookInteractor = new NotebookInteractor(this);

        mLoadProgressDialog = new ProgressDialog(this);
        mLoadProgressDialog.setCancelable(false);

        initToolbar();
        initViewPager();
        initTab();

        //获取笔记数据
        mNoteId = getIntent().getStringExtra(INTENT_NOTE_ID);

        if(!StringUtils.isEmpty(mNoteId)) {     //编辑
            mNote = mNoteInteractor.getNote(mNoteId);

            //获取note内容
            if(StringUtils.isEmpty(mNote.getContent())) {
                mLoadProgressDialog.show();
                mNoteInteractor.getNoteAndContent(MyApplication.getInstance().getUserInfo(), mNoteId,
                        new NoteContract.GetNoteContentCallback() {
                            @Override
                            public void onSuccess(Note note) {
                                mNote = note;
                                loadPics();
                            }

                            @Override
                            public void onFailure(String msg) {
                                ToastUtils.showShort(getApplicationContext(), msg);
                                mLoadProgressDialog.dismiss();
                            }
                        });
            } else {
                loadPics();
            }
        } else {        //新增
            mViewpager.setCurrentItem(1, true);

            mToolbar.setTitle("新增笔记");
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        performCodeWithPermission(R.string.permission_rc_storage, RC_PERMISSION, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {

            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {

            }
        });
    }

    //初始化toolbar
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化viewpager
    private void initViewPager() {
        mNoteViewPreviewFragment = new NoteViewPreviewFragment();
        mNoteViewEditorFragment = new NoteViewEditorFragment();

        mViewpager.setAdapter(new NoteViewAdapter(getSupportFragmentManager(), mNoteViewPreviewFragment, mNoteViewEditorFragment));
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                MenuItem otherMenuItem = mToolbar.getMenu().findItem(R.id.action_other_operate);
                if(otherMenuItem == null) {
                    return;
                }
                if(position == 0) {
                    otherMenuItem.setVisible(false);
                    if(mExpandLayout.isExpanded()) {
                        mExpandLayout.collapse(false);
                        otherMenuItem.setIcon(R.drawable.ic_add_white_24dp);
                    }
                } else {
                    otherMenuItem.setVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewpager.setCurrentItem(0, true);
    }

    //加载tab
    private void initTab() {
        mTabIconView = (TabIconView) findViewById(R.id.tabIconView);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_list_bulleted, R.id.id_shortcut_list_bulleted, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_list_numbers, R.id.id_shortcut_format_numbers, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_insert_link, R.id.id_shortcut_insert_link, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_insert_photo, R.id.id_shortcut_insert_photo, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_console, R.id.id_shortcut_console, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_bold, R.id.id_shortcut_format_bold, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_italic, R.id.id_shortcut_format_italic, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_1, R.id.id_shortcut_format_header_1, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_2, R.id.id_shortcut_format_header_2, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_3, R.id.id_shortcut_format_header_3, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_quote, R.id.id_shortcut_format_quote, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_xml, R.id.id_shortcut_xml, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_minus, R.id.id_shortcut_minus, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_strikethrough, R.id.id_shortcut_format_strikethrough, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_grid, R.id.id_shortcut_grid, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_4, R.id.id_shortcut_format_header_4, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_5, R.id.id_shortcut_format_header_5, this);
        mTabIconView.addTab(R.drawable.ic_shortcut_format_header_6, R.id.id_shortcut_format_header_6, this);
    }

    //加载图片 并转换markdown
    private void loadPics() {
        mNoteFileInteractor.loadAllPics(MyApplication.getInstance().getUserInfo(), mNoteId, new NoteFileContract.LoadAllPicsCallback() {
            @Override
            public void onStart(int totalPics, int loadedPics) {
                if(totalPics == 0 || totalPics == loadedPics) {     //不需要加载图片
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                } else {        //开始loading 下载中
                    mTotalPics = totalPics;
                    mLoadProgressDialog.show();
                }
            }

            @Override
            public void onFinish(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                }
            }

            @Override
            public void onFailure(String fileid) {
                mLoadedPics ++;
                if(mLoadedPics >= mTotalPics) {
                    mLoadProgressDialog.dismiss();
                    loadFinish();
                }
            }
        });
    }

    //图片加载完成
    private void loadFinish() {
        mCurNoteTitle = mNote.getTitle();
        mCurNoteContent = mNote.getContent();
        mCurNotebookid = mNote.getNotebookid();
        mCurNotebookpath = mNotebookInteractor.getNotebookPath(mNote.getNotebookid());

        EventBus.getDefault().post(new NoteEvent(NoteEvent.MSG_INIT));
    }

    //发生变化
    public void setEdit(Boolean edit) {
        if(edit && !mHasEdit) {
            mHasEdit = true;
            MenuItem saveMenuItem = mToolbar.getMenu().findItem(R.id.action_save);
            saveMenuItem.setIcon(R.drawable.ic_action_unsave);
        }

        if(!edit && mHasEdit) {
            mHasEdit = false;
            MenuItem saveMenuItem = mToolbar.getMenu().findItem(R.id.action_save);
            saveMenuItem.setIcon(R.drawable.ic_action_save);
        }
    }

    public String getCurNoteTitle() {
        return mCurNoteTitle;
    }

    public void setCurNoteTitle(String curNoteTitle) {
        mCurNoteTitle = curNoteTitle;
    }

    public String getCurNoteContent() {
        return mCurNoteContent;
    }

    public void setCurNoteContent(String curNoteContent) {
        mCurNoteContent = curNoteContent;
    }

    public String getCurNotebookid() {
        return mCurNotebookid;
    }

    public void setCurNotebookid(String curNotebookid) {
        mCurNotebookid = curNotebookid;
    }

    public String getCurNotebookpath() {
        return mCurNotebookpath;
    }

    public void setCurNotebookpath(String curNotebookpath) {
        mCurNotebookpath = curNotebookpath;
    }

    public NoteFileContract.Interactor getNoteFileInteractor() {
        return mNoteFileInteractor;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, NoteViewActivity.class);
    }

    public static Intent createIntent(Context context, String noteId) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra(INTENT_NOTE_ID, noteId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_view, menu);

        if(StringUtils.isEmpty(mNoteId)) {     //编辑
            MenuItem saveMenuItem = mToolbar.getMenu().findItem(R.id.action_other_operate);
            saveMenuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_other_operate://展开和收缩
                if (!mExpandLayout.isExpanded()) {
                    //没有展开，但是接下来就是展开，设置向上箭头
                    item.setIcon(R.drawable.ic_arrow_up);
                }
                else {
                    item.setIcon(R.drawable.ic_add_white_24dp);
                }
                mExpandLayout.toggle();
                break;

            case android.R.id.home:
                onExit();
                break;

            case R.id.action_switch:    //切换编辑和预览状态
                mViewpager.setCurrentItem(1-mViewpager.getCurrentItem(), true);
                break;

            case R.id.action_save:    //保存
                save(false);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        onExit();
    }

    private void onExit() {
        if(mHasEdit) {
            onNoSave();
        } else {
            finish();
        }
    }

    private void onNoSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.note_nosave_tips);
        builder.setNegativeButton(R.string.note_nosave_unsave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.note_nosave_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save(true);
            }
        });
        builder.show();
    }

    private void save(final Boolean exit) {
        if(!mHasEdit) {
            return;
        }
        Map<String, String> updateArgvs = new HashMap<>();
        updateArgvs.put("Title", mCurNoteTitle);
        updateArgvs.put("Content", mCurNoteContent);

        mLoadProgressDialog.show();
        mNoteInteractor.updateNote(MyApplication.getInstance().getUserInfo(),
                mNoteId, updateArgvs, mNoteFileInteractor.getAddNoteFiles(),
                new NoteContract.UpdateNoteCallback() {
            @Override
            public void onSuccess(Note note) {
                mLoadProgressDialog.dismiss();
                ToastUtils.showShort(getApplicationContext(), R.string.note_save_success);
                setEdit(false);
                mNote = note;
                loadFinish();
                mViewpager.setCurrentItem(0, true);

                EventBus.getDefault().post(new SyncEvent(SyncEvent.MSG_SYNC));

                if(exit) {
                    finish();
                }
            }

            @Override
            public void onFailure(String msg) {
                mLoadProgressDialog.dismiss();
                if(msg.equals("conflict")) {
                    ToastUtils.showShort(getApplicationContext(), R.string.note_save_conflict);
                } else {
                    ToastUtils.showShort(getApplicationContext(), msg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (R.id.id_shortcut_insert_photo == v.getId()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);// Pick an item fromthe
            intent.setType("image/*");// 从所有图片中进行选择
            startActivityForResult(intent, RC_SYSTEM_GALLERY);
            return;
        } else if (R.id.id_shortcut_insert_link == v.getId()) {
            //插入链接
            insertLink();
            return;
        } else if (R.id.id_shortcut_grid == v.getId()) {
            //插入表格
            insertTable();
            return;
        }
        //点击事件分发
        mNoteViewEditorFragment.getPerformEditable().onClick(v);
    }

    private static class NoteViewAdapter extends FragmentPagerAdapter {

        private NoteViewPreviewFragment mNoteViewPreviewFragment;
        private NoteViewEditorFragment mNoteViewEditorFragment;

        public NoteViewAdapter(FragmentManager fm, NoteViewPreviewFragment noteViewPreviewFragment,
                               NoteViewEditorFragment noteViewEditorFragment) {
            super(fm);
            mNoteViewPreviewFragment = noteViewPreviewFragment;
            mNoteViewEditorFragment = noteViewEditorFragment;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mNoteViewPreviewFragment;
            }
            return mNoteViewEditorFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * 插入表格
     */
    private void insertTable() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_common_input_table_view, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("插入表格")
                .setView(rootView)
                .show();

        TextInputLayout rowNumberHint = (TextInputLayout) rootView.findViewById(R.id.rowNumberHint);
        TextInputLayout columnNumberHint = (TextInputLayout) rootView.findViewById(R.id.columnNumberHint);
        EditText rowNumber = (EditText) rootView.findViewById(R.id.rowNumber);
        EditText columnNumber = (EditText) rootView.findViewById(R.id.columnNumber);


        rootView.findViewById(R.id.sure).setOnClickListener(v -> {
            String rowNumberStr = rowNumber.getText().toString().trim();
            String columnNumberStr = columnNumber.getText().toString().trim();

            if (StringUtils.isEmpty(rowNumberStr)) {
                rowNumberHint.setError("不能为空");
                return;
            }
            if (StringUtils.isEmpty(columnNumberStr)) {
                columnNumberHint.setError("不能为空");
                return;
            }


            if (rowNumberHint.isErrorEnabled())
                rowNumberHint.setErrorEnabled(false);
            if (columnNumberHint.isErrorEnabled())
                columnNumberHint.setErrorEnabled(false);

            mNoteViewEditorFragment.getPerformEditable().perform(R.id.id_shortcut_grid, Integer.parseInt(rowNumberStr), Integer.parseInt(columnNumberStr));
            dialog.dismiss();
        });
        rootView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * 插入链接
     */
    private void insertLink() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_common_input_link_view, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("插入链接")
                .setView(rootView)
                .show();

        TextInputLayout titleHint = (TextInputLayout) rootView.findViewById(R.id.inputNameHint);
        TextInputLayout linkHint = (TextInputLayout) rootView.findViewById(R.id.inputHint);
        EditText title = (EditText) rootView.findViewById(R.id.name);
        EditText link = (EditText) rootView.findViewById(R.id.text);


        rootView.findViewById(R.id.sure).setOnClickListener(v -> {
            String titleStr = title.getText().toString().trim();
            String linkStr = link.getText().toString().trim();

            if (StringUtils.isEmpty(titleStr)) {
                titleHint.setError("不能为空");
                return;
            }
            if (StringUtils.isEmpty(linkStr)) {
                linkHint.setError("不能为空");
                return;
            }

            if (titleHint.isErrorEnabled())
                titleHint.setErrorEnabled(false);
            if (linkHint.isErrorEnabled())
                linkHint.setErrorEnabled(false);

            mNoteViewEditorFragment.getPerformEditable().perform(R.id.id_shortcut_insert_link, titleStr, linkStr);
            dialog.dismiss();
        });

        rootView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == RC_SYSTEM_GALLERY) {
            Uri uri = data.getData();
            String[] pojo = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.managedQuery(uri, pojo, null, null, null);
            if (cursor != null) {
                int colunmIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(colunmIndex);

                String fileUrl = mNoteFileInteractor.createNoteFile(mNoteId, path);

                mNoteViewEditorFragment.getPerformEditable().perform(R.id.id_shortcut_insert_photo, fileUrl);
            } else {
                ToastUtils.showShort(this, "图片处理失败");
            }
        }

    }
}
