package com.weibo.sdk.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.weibo.sdk.android.Oauth2AccessToken;

public class AccessTokenKeeper {

    //    private static final String PREFERENCES_NAME = "com_weibo_sdk_android";

    public static void keepAccessToken(Context context, Oauth2AccessToken token) {
        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", 32768);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token.getToken());
        editor.putLong("expiresTime", token.getExpiresTime());
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", 32768);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public static Oauth2AccessToken readAccessToken(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", 32768);
        token.setToken(pref.getString("token", ""));
        token.setExpiresTime(pref.getLong("expiresTime", 0L));
        return token;
    }
}
