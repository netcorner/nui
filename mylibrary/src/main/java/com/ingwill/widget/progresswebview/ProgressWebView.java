package com.ingwill.widget.progresswebview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ingwill.activity.BaseActivity;
import com.ingwill.mylibrary.R;
import com.ingwill.utils.ImageTools;
import com.ingwill.widget.jsbridge.BridgeHandler;
import com.ingwill.widget.jsbridge.BridgeUtil;
import com.ingwill.widget.jsbridge.BridgeWebView;
import com.ingwill.widget.jsbridge.CallBackFunction;
import com.ingwill.widget.jsbridge.DefaultHandler;
import com.ingwill.widget.jsbridge.Message;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * Created by netcorner on 15/10/14.
 */
public class ProgressWebView extends BridgeWebView {

    private ProgressBar progressbar;

    private boolean linkEnable=true;

    public void setLinkEnable(boolean linkEnable) {
        this.linkEnable = linkEnable;
    }
    private boolean  isNewTarget=false;

    public void setNewTarget(boolean newTarget) {
        isNewTarget = newTarget;
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources resources = context.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.progressbar_drawable);

        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 10, 0, 0));
        progressbar.setProgressDrawable(drawable);
        progressbar.setVisibility(GONE);

        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        addView(progressbar);

        WebSettings webSettings = getSettings();
        //允许webview对文件的操作
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);

        webSettings.setBlockNetworkImage(false);




        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua+";android-mark");
        //        setWebViewClient(new WebViewClient(){});







        setDefaultHandler(new DefaultHandler());
        setWebChromeClient(new WebChromeClient());



        setWebViewClient(new BridgeWebViewClient());
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && ProgressWebView.this.canGoBack()) {  //表示按返回键
                        ProgressWebView.this.goBack();   //后退

                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

    }


    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if(loadURLCallback!=null) loadURLCallback.complete();
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(GONE);
                progressbar.setProgress(newProgress);
                if(loadURLCallback!=null) loadURLCallback.loading(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if(loadInnerURLCallback!=null) loadInnerURLCallback.getTitle(title);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            //选文件的处理方式
            return true;
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
    private   LoadInnerURLCallback loadInnerURLCallback;
    private LoadURLCallback loadURLCallback;

    public void setLoadURLCallback(LoadURLCallback loadURLCallback) {
        this.loadURLCallback = loadURLCallback;
    }

    @Override
    public void goBack(){
        super.goBack();
        if(loadInnerURLCallback!=null) loadInnerURLCallback.back();
    }


    public void setLoadInnerURLCallback(ProgressWebView.LoadInnerURLCallback loadInnerURLCallback) {
        this.loadInnerURLCallback = loadInnerURLCallback;
    }

    public interface LoadInnerURLCallback{
        void loaded();
        void back();
        void error(int errorCode, String description, String failingUrl);
        void getTitle(String title);
        void newTarget(String url);
    }
    public interface LoadURLCallback{
        void complete();
        void loading(int progress);
    }



    public class BridgeWebViewClient extends WebViewClient {
        private BridgeWebView webView;

        public BridgeWebViewClient() {
            this.webView = ProgressWebView.this;
        }

        @Override
        public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl)
        {
            if(loadInnerURLCallback!=null) loadInnerURLCallback.error( errorCode,  description,  failingUrl);
            else super.onReceivedError(view, errorCode, description, failingUrl);

            // 断网或者网络连接超时
            if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                view.loadUrl("about:blank"); // 避免出现默认的错误界面
                view.loadUrl("file:///android_asset/error_handle.html");

                ProgressWebView.this.registerHandler("closeWindow", new BridgeHandler() {
                    @Override
                    public void handler(String data, CallBackFunction function) {
                        BaseActivity.closeWindow((Activity) ProgressWebView.this.getContext(),0,0);
                    }
                });

            }
        }

        /**
         * 此方法是为了解决本地图片显示不出来的情况
         * @param webView
         * @param webResourceRequest
         * @return
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            InputStream input;
            String url = webResourceRequest.getUrl().toString();
            String key = "http://ingwill";

            /*如果请求包含约定的字段 说明是要拿本地的图片*/
            if (url.startsWith(key)) {
                String imgPath = url.replace(key, "");
                if(!imgPath.startsWith(".")) {
                    //Log.d("ProgressWebView","本地图片路径：" + imgPath.trim());
                    if (imgPath.contains("%")) imgPath = URLDecoder.decode(imgPath.trim());


                    //ImageTools.bitmap2Bytes()

                    Bitmap bitmap = ImageTools.decodeFile(imgPath.trim());


                    byte[] bytes = ImageTools.bitmap2Bytes(bitmap);
                    input = new ByteArrayInputStream(bytes);

                    /*重新构造WebResourceResponse  将数据已流的方式传入*/
                    //input = new FileInputStream(new File(imgPath.trim()));

                    WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                    /*返回WebResourceResponse*/
                    return response;
                }
            }
            return super.shouldInterceptRequest(webView,webResourceRequest);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
                webView.handlerReturnData(url);
                return true;
            } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
                webView.flushMessageQueue();
                return true;
            } else {
                if(linkEnable) {
                    view.loadUrl(url);
                    if (loadInnerURLCallback != null) loadInnerURLCallback.loaded();
                    return true;
                }else{
                    if(isNewTarget){
                        if (loadInnerURLCallback != null)  loadInnerURLCallback.newTarget(url);
                    }
                    return true;
                }
            }
        }

        // 增加shouldOverrideUrlLoading在api》=24时
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String url = request.getUrl().toString();
                try {
                    url = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
                    webView.handlerReturnData(url);
                    return true;
                } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
                    webView.flushMessageQueue();
                    return true;
                } else {

                    if(linkEnable) {
                        view.loadUrl(url);
                        if (loadInnerURLCallback != null) loadInnerURLCallback.loaded();
                        return true;
                    }else{
                        if(isNewTarget){
                            if (loadInnerURLCallback != null)  loadInnerURLCallback.newTarget(url);
                        }
                        return true;
                    }
                    //return this.onCustomShouldOverrideUrlLoading(url)?true:super.shouldOverrideUrlLoading(view, request);
                }
            }else {
                return super.shouldOverrideUrlLoading(view, request);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (BridgeWebView.toLoadJs != null) {
                BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
            }

            //
            if (webView.getStartupMessage() != null) {
                for (Message m : webView.getStartupMessage()) {
                    webView.dispatchMessage(m);
                }
                webView.setStartupMessage(null);
            }

            //
            onCustomPageFinishd(view,url);

        }


        protected boolean onCustomShouldOverrideUrlLoading(String url) {
            return false;
        }


        protected void onCustomPageFinishd(WebView view, String url){

        }




    }
}