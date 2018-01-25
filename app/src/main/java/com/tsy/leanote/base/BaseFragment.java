package com.tsy.leanote.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.tsy.leanote.MyApplication;
import com.tsy.leanote.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by tsy on 2016/12/13.
 */

public class BaseFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private Map<Integer, BaseActivity.PermissionCallback> mPermissonCallbacks = null;
    private Map<Integer, String[]> mPermissions = null;

    protected interface PermissionCallback {
        /**
         * has all permission
         * @param allPerms all permissions
         */
        void hasPermission(List<String> allPerms);

        /**
         * denied some permission
         * @param deniedPerms denied permission
         * @param grantedPerms granted permission
         * @param hasPermanentlyDenied has permission denied permanently
         */
        void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied);
    }

    /**
     * request permission
     * @param resId if denied first, next request rationale
     * @param requestCode requestCode
     * @param perms permissions
     * @param callback callback
     */
    protected void performCodeWithPermission(@NonNull int resId,
                                             final int requestCode, @NonNull String[] perms, @NonNull BaseActivity.PermissionCallback callback) {
        performCodeWithPermission(getResources().getString(resId), requestCode, perms, callback);
    }
    
    /**
     * request permission
     * @param rationale if denied first, next request rationale
     * @param requestCode requestCode
     * @param perms permissions
     * @param callback callback
     */
    protected void performCodeWithPermission(@NonNull String rationale,
                                             final int requestCode, @NonNull String[] perms, @NonNull BaseActivity.PermissionCallback callback) {
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            callback.hasPermission(Arrays.asList(perms));
        } else {
            if(mPermissonCallbacks == null) {
                mPermissonCallbacks = new HashMap<>();
            }
            mPermissonCallbacks.put(requestCode, callback);

            if(mPermissions == null) {
                mPermissions = new HashMap<>();
            }
            mPermissions.put(requestCode, perms);

            EasyPermissions.requestPermissions(this, rationale, requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if(mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        // 100% granted permissions
        if(mPermissions.get(requestCode).length == perms.size()) {
            mPermissonCallbacks.get(requestCode).hasPermission(Arrays.asList(mPermissions.get(requestCode)));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if(mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        //granted permission
        List<String> grantedPerms = new ArrayList<>();
        for(String perm : mPermissions.get(requestCode)) {
            if(!perms.contains(perm)) {
                grantedPerms.add(perm);
            }
        }

        //check has permission denied permanently
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, true);
        } else {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, false);
        }
    }

    /**
     * alert AppSet Permission
     * @param rationale alert setting rationale
     */
    protected void alertAppSetPermission(String rationale) {
        new AppSettingsDialog.Builder(this, rationale)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .build()
                .show();
    }

    /**
     * alert AppSet Permission
     * @param rationale alert setting rationale
     * @param requestCode onActivityResult requestCode
     */
    protected void alertAppSetPermission(String rationale, int requestCode) {
        new AppSettingsDialog.Builder(this, rationale)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MyApplication.getInstance().getMyOkHttp().cancel(this);
    }

    public boolean onBackPressed() {
        return false;
    }
}
