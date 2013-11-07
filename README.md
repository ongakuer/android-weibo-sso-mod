基于 [Android 微博 SDK](https://github.com/mobileresearch/weibo_android_sdk) v2.3 精简，仅保留客户端SSO授权代码。

未安装微博客户端，会回调到 '''WeiboAuthListener'''的'''onThirdPartyAuthorize()'''。可自行处理网页授权，不使用原sdk自带的webview授权。