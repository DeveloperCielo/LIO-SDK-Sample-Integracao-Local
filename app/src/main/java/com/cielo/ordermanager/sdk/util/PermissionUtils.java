package com.cielo.ordermanager.sdk.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    private static boolean isPermissionGranted(final Context context, final String permissionName) {
        return ActivityCompat.checkSelfPermission(context, permissionName) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasNotGrantedPermissions(Context context, String[] requiredPermissions) {
        for (final String permissionName : requiredPermissions) {
            final boolean isNotGranted = !isPermissionGranted(context, permissionName);
            if (isNotGranted)
                return true;
        }
        return false;
    }

    private static String[] getNotGrantedPermissions(Context context, final String[] requiredPermissions) {
        final List<String> permissionsNotGranted = new ArrayList<>();
        for (final String permissionName : requiredPermissions) {
            if (isPermissionGranted(context, permissionName))
                continue;
            permissionsNotGranted.add(permissionName);
        }
        return permissionsNotGranted.toArray(new String[0]);
    }

    public static void checkPermission(Context context, String[] requiredPermissions, CheckPermissionListener listener) {
        if (hasNotGrantedPermissions(context, requiredPermissions)) {
            listener.onNeedRequestPermissions(getNotGrantedPermissions(context, requiredPermissions));
        } else {
            listener.onAllPermissionsGranted();
        }
    }

    public interface CheckPermissionListener {
        void onNeedRequestPermissions(String[] permissionNotGranted);

        void onAllPermissionsGranted();
    }
}
