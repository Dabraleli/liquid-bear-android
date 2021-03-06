package com.pillowapps.liqear.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.connection.ApiException;
import com.pillowapps.liqear.connection.QueryManager;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;

/**
 * Vk authorization activity with webview.
 */
public class LoginActivity extends TrackedActivity {
    public static final String OAUTH_REQUEST_FORMAT = "http://oauth.vk.com/authorize?"
            + "client_id=%s&"
            + "scope=%s&"
            + "redirect_uri=%s&"
            + "display=%s&" + "response_type=%s";
    public static final String allPermission = "friends,photos,audio,status,wall,groups,offline";
    public static final String OAUT_BLANK_URL = "http://api.vkontakte.ru/blank.html";
    public static final String DISPLAY = "touch";
    public static final String RESPONSE = "token";
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vk_login);
        WebView webview = (WebView) findViewById(R.id.login_webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webview.clearCache(true);

        //Чтобы получать уведомления об окончании загрузки страницы
        webview.setWebViewClient(new LoginWebViewClient());

        CookieSyncManager.createInstance(this);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        String url = String.format(OAUTH_REQUEST_FORMAT, QueryManager.appId,
                allPermission, OAUT_BLANK_URL, DISPLAY, RESPONSE);
        webview.loadUrl(url);
    }

	/*
     * 06-05 08:00:14.005: D/Access_token(351): http://oauth.vk.com/blank.html
	 * #access_token
	 * =ec563076ecbc0aa7ecbc0aa729ec912958fecbdeca3b239c5e5475adc0e3e53
	 * &expires_in=0 &user_id=15350481 &secret=d377128abcc69a486d
	 */

    private void parseUrl(String url) {
        try {
            if (url.startsWith(OAUT_BLANK_URL)) {
                setUserConfiguration(url.split("[#&]"));
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        } catch (ApiException ignored) {
        }
    }

    private void setUserConfiguration(String[] strings) throws ApiException {
        if (strings.length < 4) {
            return;
        }
        boolean hasAccessToken = false;
        boolean hasUserId = false;
        String accessToken = null;
        Long userId = -1l;
        String secret = null;
        for (String param : strings) {
            if (param.startsWith("access_token")) {
                accessToken = param.substring(param.indexOf("=") + 1);
                hasAccessToken = true;
            }
            if (param.startsWith("user_id")) {
                userId = Long
                        .valueOf(param.substring(param.indexOf("=") + 1));
                hasUserId = true;
            }
            if (param.startsWith("secret")) {
                secret = param.substring(param.indexOf("=") + 1);
            }
        }
        if (hasAccessToken && hasUserId) {
            SharedPreferences preferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(AuthorizationInfoManager.VK_PREFERENCES,
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("access_token", accessToken);
            editor.putLong("uid", userId);
            editor.putString("secret", secret);
            editor.commit();
        } else {
            throw new ApiException("Invalid authorization request");
        }

    }

    class LoginWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.GONE);
            parseUrl(url);
        }
    }
}
