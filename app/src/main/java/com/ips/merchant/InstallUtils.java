package com.ips.merchant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IH847 on 2016/7/13.
 * contact with email:llzhang@ips.com
 */
public class InstallUtils {
    public static final String tag = "InstallUtils";

    @Deprecated
    public static void installApk1(Context activity) {
        String apkPath = "file:///android_asset/";
        String destApkName = "ips_plugin.apk";
        File file = null;
        file = new File(apkPath, destApkName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream os = activity.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            InputStream is = null;
            byte[] bytes = new byte[512];
            int i = -1;
            while ((i = is.read(bytes)) > 0) {
                os.write(bytes);
            }
            os.close();
            is.close();

            String permission = "666";
            try {
                String command = "chmod " + permission + " " + apkPath + "/"
                        + destApkName;
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            Intent intentInstall = new Intent();
            intentInstall.setAction(android.content.Intent.ACTION_VIEW);
            intentInstall.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intentInstall);
        }
    }

    @Deprecated
    public static void installApk2(Activity activity) {
        Intent intentInstall = new Intent();
        intentInstall.setAction(android.content.Intent.ACTION_VIEW);
        intentInstall.setDataAndType(Uri.fromFile(new File(
                        "file:///android_asset/ips_plugin.apk")),
                "application/vnd.android.package-archive");
        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intentInstall);
    }

    public static void installApk(Context activity) {
        String apkFile = Environment.getExternalStorageDirectory().getPath()+"/ips_plugin.apk";
        AssetManager assetManager = activity.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("ips_plugin.apk");
            Log.i(tag, apkFile);
            out = new FileOutputStream(apkFile);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.flush();
            out.close();
            out = null;

            in.close();
            in = null;

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.fromFile(new File(apkFile)),
                    "application/vnd.android.package-archive");

            activity.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
