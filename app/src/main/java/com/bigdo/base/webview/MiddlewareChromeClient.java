package com.bigdo.base.webview;

import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;
import com.just.agentweb.MiddlewareWebChromeBase;


/**
 * WebChrome（WebChromeClient主要辅助WebView处理JavaScript的对话框、网站图片、网站title、加载进度等）中间件
 * 【浏览器】
 */
public class MiddlewareChromeClient extends MiddlewareWebChromeBase {

    public MiddlewareChromeClient() {

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.i("Info", "onJsAlert:" + url);
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        Log.i("Info", "onProgressChanged:");
    }
}
