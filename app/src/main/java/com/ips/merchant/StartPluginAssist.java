package com.ips.merchant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author llzhang@ips.com
 * @project name ips_upmp_assist
 * @create time 2015-8-28 下午4:34:25
 */
public class StartPluginAssist {
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    private static String IPS_UPMP_PLUGIN_PACKAGE = "com.ips.upmp";
    private static String IPS_UPMP_PLUGIN_START_INIT = "com.ips.upmp.activity.StartInitActivity";

    public StartPluginAssist() {
        super();
    }

    /**
     * 检测插件是否已安装
     *
     * @param context
     * @return
     */
    private static boolean isPluginInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(IPS_UPMP_PLUGIN_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * @param startActivity 起始Activity
     * @param bundle        数据Bundle
     * @return 插件状态
     */
    public static int start_ips_plugin(Activity startActivity, Bundle bundle) {
        int statusCode = -1;
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        ComponentName componentName = startActivity.getComponentName();
        bundle.putParcelable("startComponent", componentName);
        intent.putExtras(bundle);
        intent.setComponent(new ComponentName(IPS_UPMP_PLUGIN_PACKAGE, IPS_UPMP_PLUGIN_START_INIT));
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isPluginInstalled(startActivity)) {
            try {
                System.out.println("current ips plugin version is V" + getVersionName(startActivity));
            }catch(Exception e){
                e.printStackTrace();
            }
            //startActivity.startActivityForResult(intent,requestCode);
            startActivity.startActivity(intent);
            statusCode = 0;
        } else {
            statusCode = -1;
            installPluginDialog(startActivity);
        }
        return statusCode;
    }

    public static int back2Platform(int businessFlag, Activity activity, String packageName, String activityName,
                                    Bundle bundle) {
        int statusCode = -1;
        try {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClassName(packageName, activityName);
            activity.setResult(businessFlag, intent);
            statusCode = 0;
            activity.finish();
        } catch (Exception ex) {
            statusCode = -1;
        }
        return statusCode;
    }

    private static String getVersionName(Context context) throws Exception {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    public static void installPluginDialog(final Context context) {
        // 需要重新安装控件
        System.out.println("plugin not found or need upgrade!!!");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("完成本操作需要环迅插件，是否安装？");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                StartPluginAssist.installApk(context);
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void installApk(Context activity) {
        String apkFile = Environment.getExternalStorageDirectory().getPath()+"/ips_plugin.apk";
        AssetManager assetManager = activity.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("ips_plugin.apk");
            System.out.println(apkFile);
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