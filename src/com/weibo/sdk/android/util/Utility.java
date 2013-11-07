package com.weibo.sdk.android.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;

public class Utility {

    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

    private static byte[] decodes = new byte[256];

    public static Bundle parseUrl(String url) {
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {}
        return new Bundle();
    }

    public static void showToast(String content, Context ct) {
        Toast.makeText(ct, content, Toast.LENGTH_LONG).show();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String[] array = s.split("&");
            for (String parameter : array) {
                String[] v = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }

    public static String encodeUrl(WeiboParameters parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int loc = 0; loc < parameters.size(); loc++) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String _key = parameters.getKey(loc);
            String _value = parameters.getValue(_key);
            if (_value == null) {
                Log.i("encodeUrl", "key:" + _key + " 's value is null");
            } else {
                try {
                    sb.append(URLEncoder.encode(parameters.getKey(loc), "UTF-8") + "="
                            + URLEncoder.encode(parameters.getValue(loc), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            Log.i("encodeUrl", sb.toString());
        }
        return sb.toString();
    }

    public static String encodeParameters(WeiboParameters httpParams) {
        if ((httpParams == null) || (isBundleEmpty(httpParams))) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        int j = 0;
        for (int loc = 0; loc < httpParams.size(); loc++) {
            String key = httpParams.getKey(loc);
            if (j != 0) buf.append("&");
            try {
                buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
            j++;
        }
        return buf.toString();
    }

    public static Bundle formBundle(Oauth2AccessToken oat) {
        Bundle params = new Bundle();
        params.putString("access_token", oat.getToken());
        params.putString("refresh_token", oat.getRefreshToken());
        params.putString("expires_in", String.valueOf(oat.getExpiresTime()));
        return params;
    }

    public static Bundle formErrorBundle(Exception e) {
        Bundle params = new Bundle();
        params.putString("error", e.getMessage());
        return params;
    }

    public static void showAlert(Context context, String title, String text) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.create().show();
    }

    private static boolean isBundleEmpty(WeiboParameters bundle) {
        if ((bundle == null) || (bundle.size() == 0)) {
            return true;
        }
        return false;
    }

    public static String encodeBase62(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0;
        int val = 0;
        for (int i = 0; i < data.length; i++) {
            val = val << 8 | data[i] & 0xFF;
            pos += 8;
            while (pos > 5) {
                pos -= 6;
                char c = encodes[(val >> pos)];
                sb.append(c == '/' ? "ic" : c == '+' ? "ib" : c == 'i' ? "ia" : Character
                        .valueOf(c));
                val &= (1 << pos) - 1;
            }
        }
        if (pos > 0) {
            char c = encodes[(val << 6 - pos)];
            sb.append(c == '/' ? "ic" : c == '+' ? "ib" : c == 'i' ? "ia" : Character.valueOf(c));
        }
        return sb.toString();
    }

    public static byte[] decodeBase62(String string) {
        if (string == null) {
            return null;
        }
        char[] data = string.toCharArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(string.toCharArray().length);
        int pos = 0;
        int val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[(++i)];
                c = c == 'c' ? '/' : c == 'b' ? '+' : c == 'a' ? 'i' : data[(--i)];
            }
            val = val << 6 | decodes[c];
            pos += 6;
            while (pos > 7) {
                pos -= 8;
                baos.write(val >> pos);
                val &= (1 << pos) - 1;
            }
        }
        return baos.toByteArray();
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetInfo != null) && (activeNetInfo.getType() == 1)) {
            return true;
        }
        return false;
    }

    public static Bundle errorSAX(String responsetext) {
        Bundle mErrorBun = new Bundle();
        if ((responsetext != null) && (responsetext.indexOf("{") >= 0)) {
            try {
                JSONObject json = new JSONObject(responsetext);
                mErrorBun.putString("error", json.optString("error"));
                mErrorBun.putString("error_code", json.optString("error_code"));
                mErrorBun.putString("error_description", json.optString("error_description"));
            } catch (JSONException e) {
                mErrorBun.putString("error", "JSONExceptionerror");
            }
        }

        return mErrorBun;
    }

    public static boolean isNetworkAvailable(Context ct) {
        ConnectivityManager connectivity = (ConnectivityManager) ct
                .getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo name : info) {
                if (NetworkInfo.State.CONNECTED == name.getState()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getSign(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 64);
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return null;
        }

        for (int j = 0; j < packageInfo.signatures.length; j++) {
            byte[] str = packageInfo.signatures[j].toByteArray();

            if (str != null) {
                return MD5.hexdigest(str);
            }
        }
        return null;
    }

}
