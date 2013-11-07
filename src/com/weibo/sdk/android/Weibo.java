package com.weibo.sdk.android;

import android.content.Context;
import android.text.TextUtils;

public class Weibo {

    public static final String TAG = "WEIBO_SDK_LOGIN";

    //    private static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2";

    private static Weibo mWeiboInstance = null;

    private static String app_key = "";

    private static String redirecturl = "";

    private static String scope = "";

    //    private static String app_secret = "";
    //
    //    private static final String SCOPE_EMAIL = "email";
    //
    //    private static final String SCOPE_DIRECT_MESSAGES_WRITE = "direct_messages_write";
    //
    //    private static final String SCOPE_DIRECT_MESSAGES_READ = "direct_messages_read";
    //
    //    private static final String SCOPE_FRIENDSHIPS_GROUPS_READ = "friendships_groups_read";
    //
    //    private static final String SCOPE_FRIENDSHIPS_GROUPS_WRITE = "friendships_groups_write";
    //
    //    private static final String SCOPE_STATUSES_TO_ME_READ = "statuses_to_me_read";
    //
    //    private static final String SCOPE_FOLLOW_APP_OFFICIAL_MICROBLOG = "follow_app_official_microblog";
    //
    //    private static final int AUTH_CODE = 0;
    //
    //    private static final int AUTH_TOKEN = 1;
    //
    //    private Oauth2AccessToken accessToken = null;
    //
    //    private static final String KEY_TOKEN = "access_token";
    //
    //    private static final String KEY_EXPIRES = "expires_in";
    //
    //    private static final String KEY_REFRESHTOKEN = "refresh_token";

    private static boolean isWifi = false;

    //    private static final int AUTH_ONCMPLT = 1000;
    //
    //    private static final int AUTH_ONRR = 1001;
    //
    //    private WeiboAuthListener mlistener;
    //
    //    private static String mPackagename;
    //
    //    private static String mkeyHash;
    //
    //        private Context ct;

    //    private Handler mWeiboHandler = new Handler() {
    //
    //        public void handleMessage(Message msg) {
    //            switch (msg.what) {
    //                case 1000:
    //                    if (msg.getData() != null) Weibo.this.mlistener.onComplete(msg.getData());
    //                    else {
    //                        Weibo.this.mlistener.onWeiboException(new WeiboException(
    //                                "Failed to receive access token."));
    //                    }
    //                    break;
    //                case 1001:
    //                    if (msg.obj != null) Weibo.this.mlistener
    //                            .onWeiboException((WeiboException) msg.obj);
    //                    if (msg.getData() != null) {
    //                        String error = msg.getData().getString("error");
    //                        String error_code = msg.getData().getString("error_code");
    //                        String error_description = msg.getData().getString("error_description");
    //                        Weibo.this.mlistener.onWeiboException(new WeiboException(error + "-"
    //                                + error_description, Integer.parseInt(error_code)));
    //                    }
    //                    break;
    //            }
    //        }
    //    };

    public static synchronized Weibo getInstance(String appKey, String redirectUrl, String aScope) {
        if ((TextUtils.isEmpty(appKey) | TextUtils.isEmpty(redirectUrl))) {
            throw new RuntimeException(
                    "Parameter is not complete, please fill complete appkey and redirectUrl.");
        }

        if (mWeiboInstance == null) {
            mWeiboInstance = new Weibo();
        }

        app_key = appKey;
        redirecturl = redirectUrl;
        scope = aScope;

        return mWeiboInstance;
    }

    //    /** @deprecated */
    //    public void setupConsumerConfig(String appKey, String redirectUrl) {
    //        app_key = appKey;
    //        redirecturl = redirectUrl;
    //    }

    /*
     * 这个是网页登录的，sso授权失败后默认都走网页登录。 
     * 我把这个方法禁用掉。 
     *   
     * @relex
     */

    //    public void anthorize(Context context, WeiboAuthListener listener) {
    //        this.mlistener = listener;
    //        this.ct = context;
    //        mPackagename = this.ct.getApplicationContext().getPackageName();
    //        isWifi = Utility.isWifi(this.ct);
    //        startAuthDialog(context, listener, 0);
    //    }

    /*
     * 这个也是网页登录的，sso连接失败后默认都走网页登录。 
     * 我把这个方法禁用掉。 
     * @relex
     */
    //    public void startAuthDialog(Context context, final WeiboAuthListener listener, final int type) {
    //        WeiboParameters params = new WeiboParameters();
    //        CookieSyncManager.createInstance(context);
    //        startDialog(context, params, new WeiboAuthListener() {
    //
    //            public void onComplete(Bundle values) {
    //                CookieSyncManager.getInstance().sync();
    //                String code = values.getString("code");
    //                if (type == 1) {
    //                    listener.onThirdPartyAuthorize();
    //                } else if (type == 0) {
    //                    listener.onComplete(values);
    //                };
    //            }
    //
    //            public void onWeiboException(WeiboException error) {
    //                listener.onWeiboException(error);
    //            }
    //
    //            public void onCancel() {
    //                listener.onCancel();
    //            }
    //
    //            public void onError(WeiboDialogError e) {
    //                listener.onError(e);
    //            }
    //
    //            @Override
    //            public void onThirdPartyAuthorize() {
    //                listener.onThirdPartyAuthorize();
    //            }
    //
    //        });
    //    }

    /**
     * SSO授权失败后，都走这个。直接调用第三方授权
     * 
     * @relex
     * 
     * */
    public void doThirdParyAnthorize(Context context, final WeiboAuthListener listener) {
        listener.onThirdPartyAuthorize();
    }

    //    private void KeepAccessToken(Bundle values, WeiboAuthListener listener) {
    //        String code = values.getString("code");
    //        if (code != null) {
    //            listener.onComplete(values);
    //        } else {
    //            String token = values.getString("access_token");
    //            String expires_in = values.getString("expires_in");
    //
    //            if ((TextUtils.isEmpty(token)) || (TextUtils.isEmpty(expires_in))) {
    //                WeiboException e = new WeiboException("授权失败！");
    //                listener.onWeiboException(e);
    //                return;
    //            }
    //
    //            Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
    //            if (accessToken.isSessionValid()) {
    //                AccessTokenKeeper.keepAccessToken(this.ct, accessToken);
    //                listener.onComplete(values);
    //            }
    //        }
    //    }

    //    private void startDialog(Context context, WeiboParameters parameters, WeiboAuthListener listener) {
    //        parameters.add("client_id", app_key);
    //        parameters.add("response_type", "code");
    //        parameters.add("redirect_uri", redirecturl);
    //        parameters.add("display", "mobile");
    //        parameters.add("scope", scope);
    //        parameters.add("packagename", mPackagename);
    //        parameters.add("key_hash", Utility.getSign(context, mPackagename));
    //
    //        String url = URL_OAUTH2_ACCESS_AUTHORIZE + "/authorize?" + Utility.encodeUrl(parameters);
    //        if (context.checkCallingOrSelfPermission("android.permission.INTERNET") != 0) Utility
    //                .showAlert(context, "Error",
    //                        "Application requires permission to access the Internet");
    //        else new WeiboDialog(context, url, listener).show();
    //    }

    //    private void FtchAccessToken(String authorization_code) {
    //        WeiboParameters params = new WeiboParameters();
    //        params.add("client_id", app_key);
    //        params.add("client_secret", app_secret);
    //        params.add("grant_type", "authorization_code");
    //        params.add("code", authorization_code);
    //        params.add("redirect_uri", redirecturl);
    //
    //        AsyncWeiboRunner.request(URL_OAUTH2_ACCESS_AUTHORIZE + "/access_token", params, "POST",
    //                new RequestListener() {
    //
    //                    public void onComplete(String response) {
    //                        if (Weibo.this.accessToken == null) {
    //                            Weibo.this.accessToken = new Oauth2AccessToken(response);
    //                        }
    //                        if (Weibo.this.accessToken.isSessionValid()) {
    //                            Log.d("Weibo-authorize", "Login Success! access_token="
    //                                    + Weibo.this.accessToken.getToken() + " expires="
    //                                    + Weibo.this.accessToken.getExpiresTime() + " refresh_token="
    //                                    + Weibo.this.accessToken.getRefreshToken());
    //                            Bundle b = Utility.formBundle(Weibo.this.accessToken);
    //                            Weibo.this.handleListItemEvent(1000, b, null);
    //                            return;
    //                        }
    //                        Log.d("Weibo-authorize", "Failed to receive access token");
    //                        Weibo.this.handleListItemEvent(1001, null, null);
    //                    }
    //
    //                    public void onComplete4binary(ByteArrayOutputStream responseOS) {
    //                    }
    //
    //                    public void onIOException(IOException e) {
    //                        Weibo.this.handleListItemEvent(1001, null, e);
    //                    }
    //
    //                    public void onError(WeiboException e) {
    //                        Bundle b = Utility.errorSAX(e.getMessage());
    //                        Weibo.this.handleListItemEvent(1001, b, null);
    //                    }
    //                });
    //    }

    //    private void handleListItemEvent(int eventId, Bundle b, Exception e) {
    //        Message msg = Message.obtain();
    //        msg.what = eventId;
    //        switch (eventId) {
    //            case 1000:
    //                if (b != null) msg.setData(b);
    //                break;
    //            case 1001:
    //                if (e != null) msg.obj = e;
    //                if (b != null) {
    //                    msg.setData(b);
    //                }
    //                break;
    //        }
    //        this.mWeiboHandler.sendMessage(msg);
    //    }

    public static boolean isWifi() {
        return isWifi;
    }

    public static String getApp_key() {
        return app_key;
    }

    public static String getRedirecturl() {
        return redirecturl;
    }

    public static String getScope() {
        return scope;
    }

    public static void setWifi(boolean isWifi) {
        Weibo.isWifi = isWifi;
    }
}
