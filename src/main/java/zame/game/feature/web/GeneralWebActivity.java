package zame.game.feature.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import zame.game.App;
import zame.game.R;
import zame.game.core.app.BaseActivity;

// https://stackoverflow.com/questions/2566485/webview-and-cookies-on-android

public class GeneralWebActivity extends BaseActivity {
    public static final String EXTRA_URL = "url";

    protected static final String KEY_WEBVIEW = "web";

    protected String currentUrl;
    protected WebView webView;
    protected ProgressBar progressBar;
    protected GeneralWebChromeClient webChromeClient;
    protected GeneralWebViewClient webViewClient;
    protected Bundle webViewState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        App.self.tracker.onActivityCreate(this);

        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress);

        initWebView();
        Bundle extras = getIntent().getExtras();
        currentUrl = extras.getString(EXTRA_URL);

        if (currentUrl == null) {
            currentUrl = "";
        }

        webView.loadUrl(currentUrl);
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.self.tracker.onActivityStart(this);
    }

    @Override
    protected void onStop() {
        App.self.tracker.onActivityStop(this);
        super.onStop();
    }

    @TargetApi(8)
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings({ "deprecation", "RedundantSuppression" })
    protected void initWebView() {
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        webSettings.setSupportMultipleWindows(true);

        webView.setBackgroundColor(0);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        webChromeClient = new GeneralWebChromeClient(progressBar);
        webView.setWebChromeClient(webChromeClient);

        webViewClient = new GeneralWebViewClient();
        webView.setWebViewClient(webViewClient);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        webViewState = state.getBundle(KEY_WEBVIEW);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        webView.saveState(webViewState);
        state.putBundle(KEY_WEBVIEW, webViewState);
    }

    @Override
    public void onBackPressed() {
        webView.stopLoading();

        WebBackForwardList list = webView.copyBackForwardList();
        int currentIndex = list.getCurrentIndex();
        int index = currentIndex;

        while (index >= 0) {
            WebHistoryItem item = list.getItemAtIndex(index);
            index--;

            if (item == null) {
                continue;
            }

            String url = item.getUrl();

            if (url.startsWith(GeneralWebViewClient.ERROR_PAGE_URL)) {
                continue;
            }

            if (index >= 0) {
                WebHistoryItem prevItem = list.getItemAtIndex(index);

                if (prevItem != null && url.equals(prevItem.getUrl())) {
                    continue;
                }
            }

            break;
        }

        if (index < 0) {
            super.onBackPressed();
        } else {
            webView.goBackOrForward(index - currentIndex);
        }
    }
}
