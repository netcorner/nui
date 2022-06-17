package com.ingwill.listener;

/**
 * Created by netcorner on 17/3/25.
 * 权限申请接口
 */

public interface PermissionsResultListener {
    void onPermissionGranted();

    void onPermissionDenied();
}
