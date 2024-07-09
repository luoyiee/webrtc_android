package com.dds.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.dds.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author luo
 */
public class PermissionUtil {
    /**
     * 请求联系人
     */
    public static int REQUEST_CODE_SERVICE_SMS = 100;

    /**
     * @param permission 判断某个权限是否打开
     * @return
     */
    public static boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(App.getInstance(), permission) == PERMISSION_GRANTED;
    }

    /**
     * @return 判断蓝牙权限是否打开----
     */
    public static boolean hasLocationPermission() {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static void goPermissionSettings(Activity activity) {
        if (miuiSystem()) {
            Intent intent;
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());

            if (isNotIntentAvailable(intent, activity)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
                if (isNotIntentAvailable(intent, activity)) {
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                }
            }
            activity.startActivityForResult(intent, REQUEST_CODE_SERVICE_SMS);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivity(intent);
        }
    }

    /**
     * 判断是否是小米机型
     *
     * @return
     */
    public static boolean miuiSystem() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e("", "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e("", "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 判断是否存在可用的跳转意图
     */
    public static boolean isNotIntentAvailable(Intent intent, Context context) {
        if (intent == null || context == null || context.getPackageManager() == null) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)).size() <= 0;
        } else {
            return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() <= 0;
        }
    }


    public static boolean isLocationEnabled(Context context) {
        // This is new method provided in API 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

}
