package com.weibo.sdk.android;

import android.os.Bundle;

public abstract interface WeiboAuthListener {

    public abstract void onComplete(Bundle paramBundle);

    public abstract void onWeiboException(WeiboException paramWeiboException);

    public abstract void onError(WeiboDialogError paramWeiboDialogError);

    public abstract void onCancel();

    public abstract void onThirdPartyAuthorize();

}
