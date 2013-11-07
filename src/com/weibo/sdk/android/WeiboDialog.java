//package com.weibo.sdk.android;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Locale;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.AssetManager;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Rect;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.NinePatchDrawable;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.Xml;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.CookieManager;
//import android.webkit.CookieSyncManager;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.weibo.sdk.android.util.Utility;
//
//public class WeiboDialog extends Dialog implements View.OnClickListener {
//
//    private static final String TAG = "WEIBO_SDK_LOGIN";
//
//    private Context mContext;
//
//    private RelativeLayout mContent;
//
//    private RelativeLayout mWebViewContainer;
//
//    private ProgressDialog mSpinner;
//
//    private WebView mWebView;
//
//    private String mAuthUrl;
//
//    private WeiboAuthListener mListener;
//
//    private static int theme = 16973840;
//
//    private static int left_margin = 0;
//
//    private static int top_margin = 0;
//
//    private static int right_margin = 0;
//
//    private static int bottom_margin = 0;
//
//    public WeiboDialog(Context context, String url, WeiboAuthListener listener) {
//        super(context, theme);
//        this.mAuthUrl = url;
//        this.mListener = listener;
//        this.mContext = context;
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == 4) {
//            this.mListener.onCancel();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        initWindow();
//
//        initLoadingDlg();
//
//        initWebview();
//
//        initCloseButton();
//
//        Utility.isNetworkAvailable(this.mContext);
//    }
//
//    private void initWindow() {
//        requestWindowFeature(1);
//        getWindow().setFeatureDrawableAlpha(0, 0);
//        getWindow().setSoftInputMode(16);
//        this.mContent = new RelativeLayout(getContext());
//        addContentView(this.mContent, new ViewGroup.LayoutParams(-1, -1));
//    }
//
//    private void initLoadingDlg() {
//        this.mSpinner = new ProgressDialog(getContext());
//
//        this.mSpinner.requestWindowFeature(1);
//
//        Locale locale = Locale.getDefault();
//        String language = locale.getLanguage();
//        String loading = "loading……";
//        if (("zh".equalsIgnoreCase(language)) || ("zh_CN".equalsIgnoreCase(language))) {
//            loading = "加载中……";
//            if ("TW".equalsIgnoreCase(locale.getCountry())) {
//                loading = "加載中……";
//            }
//        }
//        this.mSpinner.setMessage(loading);
//    }
//
//    private void initWebview() {
//        this.mWebViewContainer = new RelativeLayout(getContext());
//        this.mWebView = new WebView(getContext());
//        this.mWebView.getSettings().setJavaScriptEnabled(true);
//
//        this.mWebView.getSettings().setSavePassword(false);
//        this.mWebView.setWebViewClient(new WeiboWebViewClient());
//        synCookies(this.mContext, this.mAuthUrl);
//        this.mWebView.loadUrl(this.mAuthUrl);
//        this.mWebView.requestFocus();
//        this.mWebView.setScrollBarStyle(0);
//
//        this.mWebView.setVisibility(4);
//
//        RelativeLayout.LayoutParams webViewContainerLayout = new RelativeLayout.LayoutParams(-1, -1);
//
//        RelativeLayout.LayoutParams webviewLayout = new RelativeLayout.LayoutParams(-1, -1);
//
//        AssetManager asseets = getContext().getAssets();
//        InputStream is = null;
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//        float density = dm.density;
//        try {
//            try {
//                is = asseets.open("weibosdk_dialog_bg.9.png");
//                webviewLayout.leftMargin = ((int) (10.0F * density));
//                webviewLayout.topMargin = ((int) (10.0F * density));
//                webviewLayout.rightMargin = ((int) (10.0F * density));
//                webviewLayout.bottomMargin = ((int) (10.0F * density));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (is == null) {
//                this.mWebViewContainer.setBackgroundResource(2130837540);
//            } else {
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                NinePatchDrawable npd = new NinePatchDrawable(bitmap, bitmap.getNinePatchChunk(),
//                        new Rect(0, 0, 0, 0), null);
//                this.mWebViewContainer.setBackgroundDrawable(npd);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        this.mWebViewContainer.addView(this.mWebView, webviewLayout);
//        this.mWebViewContainer.setGravity(17);
//        if (parseDimens()) {
//            webViewContainerLayout.leftMargin = left_margin;
//            if (density == 1.0D) webViewContainerLayout.topMargin = 15;
//            else if (density == 1.5D) webViewContainerLayout.topMargin = 25;
//            else {
//                webViewContainerLayout.topMargin = (dm.heightPixels / 15);
//            }
//            webViewContainerLayout.rightMargin = right_margin;
//            int space = (int) (1.5D * (dm.widthPixels - 2 * left_margin));
//            webViewContainerLayout.bottomMargin = (dm.heightPixels
//                    - webViewContainerLayout.topMargin - space);
//
//            webViewContainerLayout.bottomMargin = (webViewContainerLayout.bottomMargin <= 0 ? bottom_margin
//                    : webViewContainerLayout.bottomMargin);
//        } else {
//            Resources resources = getContext().getResources();
//            webViewContainerLayout.leftMargin = resources.getDimensionPixelSize(2131099651);
//            webViewContainerLayout.rightMargin = resources.getDimensionPixelSize(2131099653);
//            webViewContainerLayout.topMargin = resources.getDimensionPixelSize(2131099652);
//            webViewContainerLayout.bottomMargin = resources.getDimensionPixelSize(2131099654);
//        }
//
//        this.mContent.setBackgroundColor(0);
//        this.mContent.addView(this.mWebViewContainer, webViewContainerLayout);
//    }
//
//    private void initCloseButton() {
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//
//        String imageName = dm.densityDpi >= 240 ? "weibosdk_close_hdpi.png"
//                : "weibosdk_close_mdpi.png";
//        Drawable drawable = getDrawableFromAssert(imageName);
//
//        ImageView closeImage = new ImageView(this.mContext);
//
//        closeImage.setImageDrawable(drawable);
//        closeImage.setOnClickListener(this);
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mWebViewContainer
//                .getLayoutParams();
//        layoutParams.leftMargin = (params.leftMargin - drawable.getIntrinsicWidth() / 2 + 5);
//        layoutParams.topMargin = (params.topMargin - drawable.getIntrinsicHeight() / 2 + 5);
//        this.mContent.addView(closeImage, layoutParams);
//    }
//
//    protected void onBack() {
//        try {
//            this.mSpinner.dismiss();
//            if (this.mWebView != null) {
//                this.mWebView.stopLoading();
//                this.mWebView.destroy();
//            }
//        } catch (Exception localException) {}
//        dismiss();
//    }
//
//    public static void synCookies(Context context, String url) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();
//        cookieManager.removeAllCookie();
//
//        CookieSyncManager.getInstance().sync();
//    }
//
//    private void handleRedirectUrl(WebView view, String url) {
//        Bundle values = Utility.parseUrl(url);
//
//        String error = values.getString("error");
//        String error_code = values.getString("error_code");
//
//        if ((error == null) && (error_code == null)) {
//            this.mListener.onComplete(values);
//        } else if (error_code == null) this.mListener
//                .onWeiboException(new WeiboException(error, 0));
//        else this.mListener
//                .onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
//    }
//
//    private boolean parseDimens() {
//        boolean ret = false;
//        AssetManager asseets = getContext().getAssets();
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//        float density = dm.density;
//        InputStream is = null;
//        try {
//            is = asseets.open("values/dimens.xml");
//            XmlPullParser xmlpull = Xml.newPullParser();
//            try {
//                xmlpull.setInput(is, "utf-8");
//                int eventCode = xmlpull.getEventType();
//                ret = true;
//                while (eventCode != 1) {
//                    switch (eventCode) {
//                        case 2:
//                            if (xmlpull.getName().equals("dimen")) {
//                                String name = xmlpull.getAttributeValue(null, "name");
//                                if ("weibosdk_dialog_left_margin".equals(name)) {
//                                    String value = xmlpull.nextText();
//                                    left_margin = (int) (Integer.parseInt(value) * density);
//                                } else if ("weibosdk_dialog_top_margin".equals(name)) {
//                                    String value = xmlpull.nextText();
//                                    top_margin = (int) (Integer.parseInt(value) * density);
//                                } else if ("weibosdk_dialog_right_margin".equals(name)) {
//                                    String value = xmlpull.nextText();
//                                    right_margin = (int) (Integer.parseInt(value) * density);
//                                } else if ("weibosdk_dialog_bottom_margin".equals(name)) {
//                                    String value = xmlpull.nextText();
//                                    bottom_margin = (int) (Integer.parseInt(value) * density);
//                                }
//                            }
//                            break;
//                    }
//                    eventCode = xmlpull.next();
//                }
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return ret;
//    }
//
//    private Drawable getDrawableFromAssert(String resourceRelativePath) {
//        Drawable rtDrawable = null;
//        AssetManager asseets = getContext().getAssets();
//        InputStream is = null;
//        try {
//            is = asseets.open(resourceRelativePath);
//            if (is != null) {
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//                bitmap.setDensity(dm.densityDpi);
//
//                rtDrawable = new BitmapDrawable(getContext().getResources(), bitmap);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                is = null;
//            }
//        }
//
//        return rtDrawable;
//    }
//
//    public void onClick(View view) {
//        if ((view instanceof ImageView)) {
//            onBack();
//            this.mListener.onCancel();
//        }
//    }
//
//    private class WeiboWebViewClient extends WebViewClient {
//
//        private boolean isCallBacked = false;
//
//        private WeiboWebViewClient() {
//        }
//
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.startsWith("sms:")) {
//                Intent sendIntent = new Intent("android.intent.action.VIEW");
//                sendIntent.putExtra("address", url.replace("sms:", ""));
//                sendIntent.setType("vnd.android-dir/mms-sms");
//                WeiboDialog.this.getContext().startActivity(sendIntent);
//                return true;
//            }
//            return super.shouldOverrideUrlLoading(view, url);
//        }
//
//        public void onReceivedError(WebView view, int errorCode, String description,
//                String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            WeiboDialog.this.dismiss();
//            WeiboDialog.this.mListener.onError(new WeiboDialogError(description, errorCode,
//                    failingUrl));
//        }
//
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            Log.d("WEIBO_SDK_LOGIN", "onPageStarted URL: " + url);
//            if ((url.startsWith(Weibo.getRedirecturl())) && (!this.isCallBacked)) {
//                this.isCallBacked = true;
//                WeiboDialog.this.dismiss();
//                WeiboDialog.this.handleRedirectUrl(view, url);
//                view.stopLoading();
//
//                return;
//            }
//
//            super.onPageStarted(view, url, favicon);
//            WeiboDialog.this.mSpinner.show();
//        }
//
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            Log.d("WEIBO_SDK_LOGIN", "onPageFinished URL: " + url);
//            if (WeiboDialog.this.mSpinner.isShowing()) {
//                WeiboDialog.this.mSpinner.dismiss();
//            }
//            WeiboDialog.this.mWebView.setVisibility(0);
//        }
//    }
//}
