package com.tsy.leanote.base;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by tsy on 2016/12/13.
 */

public class BaseFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private Map<Integer, PermissionCallback> mPermissonCallbacks = new HashMap<>();

    /**
     * 权限回调接口
     */
    protected interface PermissionCallback {
        /**
         * 成功获取权限
         */
        void hasPermission();

        /**
         * 没有权限
         * @param hasPermanentlyDenied 是否点击不再询问，被设置为永久拒绝权限
         */
        void noPermission(Boolean hasPermanentlyDenied);
    }

    /**
     * 请求权限操作
     * @param rationale 请求权限提示语
     * @param permissionRequestCode 权限requestCode
     * @param perms 申请的权限列表
     * @param callback 权限结果回调
     */
    protected void performCodeWithPermission(@NonNull String rationale,
                                             final int permissionRequestCode, @NonNull String[] perms, @NonNull PermissionCallback callback) {
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            callback.hasPermission();
        } else {
            mPermissonCallbacks.put(permissionRequestCode, callback);
            EasyPermissions.requestPermissions(this, rationale, permissionRequestCode, perms);
        }
    }

    /**
     * 跳转设置弹框 建议在权限被设置为不在询问时弹出 提示用户前往设置页面打开权限
     * @param tips 提示信息
     */
    protected void alertAppSetPermission(String tips) {
        new AppSettingsDialog.Builder(this, tips)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .build()
                .show();
    }

    /**
     * 跳转设置弹框 建议在权限被设置为不在询问时弹出 提示用户前往设置页面打开权限
     * @param tips 提示信息
     * @param requestCode 页面返回时onActivityResult的requestCode
     */
    protected void alertAppSetPermission(String tips, int requestCode) {
        new AppSettingsDialog.Builder(this, tips)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        PermissionCallback callback = mPermissonCallbacks.get(requestCode);
        if(callback != null) {
            callback.hasPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionCallback callback = mPermissonCallbacks.get(requestCode);
        if(callback != null) {
            if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                callback.noPermission(true);
            } else {
                callback.noPermission(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MyApplication.getInstance().getMyOkHttp().cancel(this);
    }
}
