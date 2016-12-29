package site.hanschen.runwithyou.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import site.hanschen.runwithyou.R;
import site.hanschen.runwithyou.base.RunnerBaseActivity;

/**
 * @author HansChen
 */
public class WebViewActivity extends RunnerBaseActivity {

    private static final String KEY_URL   = "KEY_URL";
    private static final String KEY_TITLE = "KEY_TITLE";

    public static void startup(@NonNull Context context, @NonNull String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    private WebView mWebView;
    private String  mUrl;
    private String  mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);
        parseData();
        initViews();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.web_toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(mUrl);
    }

    private void parseData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || (mUrl = bundle.getString(KEY_URL)) == null || (mTitle = bundle.getString(KEY_TITLE)) == null) {
            throw new IllegalStateException("bundle must contain url and title info");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}