package com.sina.weibo.sdk.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.weibo.sdk.android.util.MD5;

public class ApiUtils {

    //    private static final String TAG = "ApiUtils";

    public static final int BUILD_INT = 10350;

    public static final int BUILD_INT_VER_2_2 = 10351;

    public static final int BUILD_INT_VER_2_3 = 10352;

    //    private static final String WEIBO_IDENTITY_ACTION = "com.sina.weibo.action.sdkidentity";

    private static final Uri WEIBO_NAME_URI = Uri
            .parse("content://com.sina.weibo.sdkProvider/query/package");

    //    private static final String WEIBO_SIGN = "18da2bf10352443a00a5e046d9fca6bd";

    public static WeiboInfo queryWeiboInfoByPackage(Context context, String packageName) {
        WeiboInfo winfo = null;
        winfo = getAssetWeiboInfo(context, packageName);
        if (winfo != null) {
            return winfo;
        }

        winfo = queryWeiboInfoByProvider(context);
        if ((winfo != null) && (packageName.equals(winfo.packageName))) {
            return winfo;
        }

        return null;
    }

    public static WeiboInfo queryWeiboInfo(Context context) {
        WeiboInfo winfo = null;
        winfo = queryWeiboInfoByProvider(context);
        if (winfo != null) {
            return winfo;
        }
        winfo = queryWeiboInfoByFile(context);
        return winfo;
    }

    private static WeiboInfo queryWeiboInfoByProvider(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(WEIBO_NAME_URI, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            int supportApiIndex = cursor.getColumnIndex("support_api");
            int packageIndex = cursor.getColumnIndex("package");
            if (cursor.moveToFirst()) {
                int supportApiInt = -1;
                String supportApi = cursor.getString(supportApiIndex);
                try {
                    supportApiInt = Integer.parseInt(supportApi);
                } catch (NumberFormatException localNumberFormatException) {}
                String packageName = cursor.getString(packageIndex);
                if ((!TextUtils.isEmpty(packageName)) && (validateSign(context, packageName))) {
                    WeiboInfo winfo = new WeiboInfo();
                    winfo.packageName = packageName;
                    winfo.supportApi = supportApiInt;
                    return winfo;
                }
            }
        } catch (Exception e) {
            Log.e("ApiUtils", e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    private static WeiboInfo queryWeiboInfoByFile(Context context) {
        Intent intent = new Intent("com.sina.weibo.action.sdkidentity");
        intent.addCategory("android.intent.category.DEFAULT");
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(intent, 0);
        if ((list == null) || (list.isEmpty())) {
            return null;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            ResolveInfo ri = (ResolveInfo) list.get(i);

            if ((ri.serviceInfo != null) && (ri.serviceInfo.applicationInfo != null)
                    && (ri.serviceInfo.applicationInfo.packageName != null)
                    && (ri.serviceInfo.applicationInfo.packageName.length() != 0)) {
                String packageName = ri.serviceInfo.applicationInfo.packageName;
                WeiboInfo winfo = getAssetWeiboInfo(context, packageName);
                if (winfo != null) {
                    return winfo;
                }
            }
        }
        return null;
    }

    public static WeiboInfo getAssetWeiboInfo(Context context, String packageName) {
        try {
            Context weiboContext = context.createPackageContext(packageName, 2);
            InputStream is = null;
            try {
                //                int len = 1024;
                byte[] buf = new byte[1024];

                is = weiboContext.getAssets().open("weibo_for_sdk.json");
                StringBuilder sbContent = new StringBuilder();
                int readNum;
                while ((readNum = is.read(buf, 0, 1024)) != -1) {
                    sbContent.append(new String(buf, 0, readNum));
                }

                if (TextUtils.isEmpty(sbContent.toString())) ;
                while (!validateSign(context, packageName)) {
                    return null;
                }

                int supportApi = parseSupportApi(sbContent.toString());
                WeiboInfo winfo = new WeiboInfo();
                winfo.packageName = packageName;
                winfo.supportApi = supportApi;
                return winfo;
            } catch (IOException e) {
                Log.e("ApiUtils", e.getMessage(), e);
            } catch (Exception e) {
                Log.e("ApiUtils", e.getMessage(), e);
            } finally {
                if (is != null) try {
                    is.close();
                } catch (IOException localIOException5) {}
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ApiUtils", e.getMessage(), e);
        } catch (Exception e) {
            Log.e("ApiUtils", e.getMessage(), e);
        }

        return null;
    }

    private static int parseSupportApi(String weiboInfo) {
        if (TextUtils.isEmpty(weiboInfo)) return -1;
        try {
            JSONObject json = new JSONObject(weiboInfo);
            return json.optInt("support_api", -1);
        } catch (JSONException localJSONException) {}
        return -1;
    }

    public static boolean isWeiboAppSupportAPI(int supportApi) {
        return supportApi >= 10350;
    }

    public static boolean validateSign(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 64);
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {

            return false;
        }
        return compareSign(packageInfo.signatures);
    }

    public static boolean compareSign(Signature[] sign) {
        for (int j = 0; j < sign.length; j++) {
            String s = MD5.hexdigest(sign[0].toByteArray());
            if ("18da2bf10352443a00a5e046d9fca6bd".equals(s)) {
                Log.d("Weibo", "check pass");
                return true;
            }
        }
        return false;
    }

    public static class WeiboInfo {

        public String packageName;

        public int supportApi;
    }
}
