package com.weibo.sdk.android.sso;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sina.sso.RemoteSSO;
import com.sina.weibo.sdk.api.ApiUtils;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.util.AccessTokenKeeper;
import com.weibo.sdk.android.util.Utility;

public class SsoHandler {

    private ServiceConnection conn = null;

    private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32973;

    //
    private static final String WEIBO_SIGNATURE = "30820295308201fea00302010202044b4ef1bf300d06092a864886f70d010105050030818d310b300906035504061302434e3110300e060355040813074265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c74643020170d3130303131343130323831355a180f32303630303130323130323831355a30818d310b300906035504061302434e3110300e060355040813074265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c746430819f300d06092a864886f70d010101050003818d00308189028181009d367115bc206c86c237bb56c8e9033111889b5691f051b28d1aa8e42b66b7413657635b44786ea7e85d451a12a82a331fced99c48717922170b7fc9bc1040753c0d38b4cf2b22094b1df7c55705b0989441e75913a1a8bd2bc591aa729a1013c277c01c98cbec7da5ad7778b2fad62b85ac29ca28ced588638c98d6b7df5a130203010001300d06092a864886f70d0101050500038181000ad4b4c4dec800bd8fd2991adfd70676fce8ba9692ae50475f60ec468d1b758a665e961a3aedbece9fd4d7ce9295cd83f5f19dc441a065689d9820faedbb7c4a4c4635f5ba1293f6da4b72ed32fb8795f736a20c95cda776402099054fccefb4a1a558664ab8d637288feceba9508aa907fc1fe2b1ae5a0dec954ed831c0bea4";

    private int mAuthActivityCode;

    private static String ssoPackageName = "";

    private static String ssoActivityName = "";

    private WeiboAuthListener mAuthDialogListener;

    private Oauth2AccessToken mAccessToken = null;

    private Activity mAuthActivity;

    private Weibo mWeibo;

    //    private static final int AUTH_CODE = 0;
    //
    //    private static final int AUTH_TOKEN = 1;
    //
    //    private static final String KEY_TOKEN = "access_token";
    //
    //    private static final String KEY_EXPIRES = "expires_in";
    //
    //    private static final String KEY_REFRESHTOKEN = "refresh_token";

    public SsoHandler(Activity activity, Weibo weibo) {
        this.mAuthActivity = activity;
        this.mWeibo = weibo;
        Weibo.setWifi(Utility.isWifi(activity));
        this.conn = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
                SsoHandler.this.mWeibo.doThirdParyAnthorize(SsoHandler.this.mAuthActivity,
                        SsoHandler.this.mAuthDialogListener);
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                RemoteSSO remoteSSOservice = RemoteSSO.Stub.asInterface(service);
                try {
                    SsoHandler.ssoPackageName = remoteSSOservice.getPackageName();
                    SsoHandler.ssoActivityName = remoteSSOservice.getActivityName();
                    boolean singleSignOnStarted = SsoHandler.this.startSingleSignOn(
                            SsoHandler.this.mAuthActivity, Weibo.getApp_key(), Weibo.getScope(),
                            SsoHandler.this.mAuthActivityCode);
                    if (!singleSignOnStarted) {
                        SsoHandler.this.mWeibo.doThirdParyAnthorize(SsoHandler.this.mAuthActivity,
                                SsoHandler.this.mAuthDialogListener);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void authorize(WeiboAuthListener listener) {
        authorize(DEFAULT_AUTH_ACTIVITY_CODE, listener, null);
    }

    public void authorize(WeiboAuthListener listener, String packageName) {
        authorize(DEFAULT_AUTH_ACTIVITY_CODE, listener, packageName);
    }

    public void authorize(int activityCode, WeiboAuthListener listener, String packageName) {
        this.mAuthActivityCode = activityCode;

        boolean bindSucced = false;
        this.mAuthDialogListener = listener;

        bindSucced = bindRemoteSSOService(this.mAuthActivity, packageName);

        //  绑定不成功 ，会打开微博自带的webview登录，
        //        if ((!bindSucced) && (this.mWeibo != null)) this.mWeibo.anthorize(this.mAuthActivity,
        //                this.mAuthDialogListener);
        if ((!bindSucced) && (this.mWeibo != null)) this.mWeibo.doThirdParyAnthorize(
                this.mAuthActivity, this.mAuthDialogListener);

    }

    private boolean bindRemoteSSOService(Activity activity, String packageName) {
        Context context = activity.getApplicationContext();

        if ((packageName != null) && (!packageName.trim().equals(""))) {
            Intent intent = new Intent("com.sina.weibo.remotessoservice");
            intent.setPackage(packageName);
            boolean binded = context.bindService(intent, this.conn, 1);
            if (binded) {
                return true;
            }

            intent = new Intent("com.sina.weibo.remotessoservice");

            return context.bindService(intent, this.conn, 1);
        }

        Intent intent = new Intent("com.sina.weibo.remotessoservice");
        intent.setPackage("com.sina.weibo");
        boolean binded = context.bindService(intent, this.conn, 1);
        if (binded) {
            return true;
        }

        intent = new Intent("com.sina.weibo.remotessoservice");

        return context.bindService(intent, this.conn, 1);
    }

    public static ComponentName isServiceExisted(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List<RunningServiceInfo> serviceList = activityManager.getRunningServices(2147483647);

        if (serviceList.size() <= 0) {
            return null;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = (ActivityManager.RunningServiceInfo) serviceList
                    .get(i);
            ComponentName serviceName = serviceInfo.service;

            if ((serviceName.getPackageName().equals(packageName))
                    && (serviceName.getClassName().equals(packageName
                            + ".business.RemoteSSOService"))) {
                return serviceName;
            }
        }

        return null;
    }

    private boolean startSingleSignOn(Activity activity, String applicationId, String permissions,
            int activityCode) {
        boolean didSucceed = true;
        String paName = activity.getApplicationContext().getPackageName();
        Intent intent = new Intent();
        intent.setClassName(ssoPackageName, ssoActivityName);
        intent.putExtra("appKey", applicationId);
        intent.putExtra("redirectUri", Weibo.getRedirecturl());
        intent.putExtra("packagename", paName);
        intent.putExtra("key_hash", Utility.getSign(activity.getApplicationContext(), paName));

        Bundle data = new Bundle();
        data.putInt("_weibo_command_type", 3);
        data.putString("_weibo_transaction", String.valueOf(System.currentTimeMillis()));
        intent.putExtras(data);

        if (permissions != null) {
            intent.putExtra("scope", permissions);
        }

        if (!validateAppSignatureForIntent(activity, intent)) {
            return false;
        }
        try {
            activity.startActivityForResult(intent, activityCode);
        } catch (ActivityNotFoundException e) {
            didSucceed = false;
        }

        activity.getApplication().unbindService(this.conn);
        return didSucceed;
    }

    private boolean validateAppSignatureForIntent(Activity activity, Intent intent) {
        ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(intent, 0);
        if (resolveInfo == null) {
            return false;
        }

        String packageName = resolveInfo.activityInfo.packageName;
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(packageName, 64);
            for (Signature signature : packageInfo.signatures)
                if (WEIBO_SIGNATURE.equals(signature.toCharsString())) return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return false;
    }

    private boolean checkResponse(Intent intent) {
        ApiUtils.WeiboInfo winfo = ApiUtils.queryWeiboInfo(this.mAuthActivity);
        if ((winfo != null) && (winfo.supportApi <= 10352)) {
            return true;
        }
        if (winfo == null) {
            return true;
        }

        String appPackage = intent.getStringExtra("_weibo_appPackage");

        if (appPackage == null) {
            return false;
        }

        if (intent.getStringExtra("_weibo_transaction") == null) {
            return false;
        }

        if (!ApiUtils.validateSign(this.mAuthActivity, appPackage)) {
            return false;
        }

        return true;
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.mAuthActivityCode) {
            if (resultCode == -1) {
                if (!checkResponse(data)) {
                    return;
                }

                String error = data.getStringExtra("error");
                if (error == null) {
                    error = data.getStringExtra("error_type");
                }

                if (error != null) {
                    if ((error.equals("access_denied"))
                            || (error.equals("OAuthAccessDeniedException"))) {
                        Log.d("Weibo-authorize", "Login canceled by user.");
                        this.mAuthDialogListener.onCancel();
                    } else {
                        String description = data.getStringExtra("error_description");
                        if (description != null) {
                            error = error + ":" + description;
                        }
                        Log.d("Weibo-authorize", "Login failed: " + error);
                        this.mAuthDialogListener.onError(new WeiboDialogError(error, resultCode,
                                description));
                    }
                } else {
                    if (this.mAccessToken == null) {
                        this.mAccessToken = new Oauth2AccessToken();
                    }
                    this.mAccessToken.setToken(data.getStringExtra("access_token"));
                    this.mAccessToken.setExpiresIn(data.getStringExtra("expires_in"));
                    this.mAccessToken.setRefreshToken(data.getStringExtra("refresh_token"));
                    if (this.mAccessToken.isSessionValid()) {
                        Log.d("Weibo-authorize",
                                "Login Success! access_token=" + this.mAccessToken.getToken()
                                        + " expires=" + this.mAccessToken.getExpiresTime()
                                        + "refresh_token=" + this.mAccessToken.getRefreshToken());
                        KeepAccessToken(data.getExtras(), this.mAuthDialogListener);
                    } else {
                        Log.d("Weibo-authorize", "Failed to receive access token by SSO");

                        this.mWeibo.doThirdParyAnthorize(this.mAuthActivity,
                                this.mAuthDialogListener);
                    }
                }

            } else if (resultCode == 0) {
                if (data != null) {
                    Log.d("Weibo-authorize", "Login failed: " + data.getStringExtra("error"));
                    this.mAuthDialogListener.onError(new WeiboDialogError(data
                            .getStringExtra("error"), data.getIntExtra("error_code", -1), data
                            .getStringExtra("failing_url")));
                } else {
                    Log.d("Weibo-authorize", "Login canceled by user.");
                    this.mAuthDialogListener.onCancel();
                }
            }
        }
    }

    private void KeepAccessToken(Bundle values, WeiboAuthListener listener) {
        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");

        Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
        if (accessToken.isSessionValid()) {
            AccessTokenKeeper.keepAccessToken(this.mAuthActivity, accessToken);
            listener.onComplete(values);
        }
    }
}
