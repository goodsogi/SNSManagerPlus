package world.plus.manager.sns4.manage_account;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewLoginActivity extends Activity {
	private Intent mIntent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mIntent = getIntent();
		String url = (String) mIntent.getExtras().get(SMConstants.KEY_URL);
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains(SMConstants.TWITTER_CALLBACK_URL)) {
					Uri uri = Uri.parse(url);
					String oauthVerifier = uri
							.getQueryParameter(SMConstants.URL_TWITTER_OAUTH_VERIFIER);
					mIntent.putExtra(
							SMConstants.URL_TWITTER_OAUTH_VERIFIER,
							oauthVerifier);
					setResult(RESULT_OK, mIntent);
					finish();
					return true;
				}

				if (url.startsWith(SMConstants.FOURSQUARE_CALLBACK_URL)) {
					String parts[] = url.split("=");
					String request_token = parts[1]; // This is your request
														// token.
					mIntent.putExtra(SMConstants.FOURSQUARE_REQUEST_TOKEN,
							request_token);
					setResult(RESULT_OK, mIntent);
					finish();

					return true;
				}

				if (url.startsWith(SMConstants.APPNET_CALLBACK_URL)) {
					String parts[] = url.split("=");
					String access_token = parts[1]; // This is your request
													// token.
					mIntent.putExtra(SMConstants.APPNET_ACCESS_TOKEN,
							access_token);
					setResult(RESULT_OK, mIntent);
					finish();

					return true;
				}

				if (url.startsWith(SMConstants.LINKEDIN_CALLBACK_URL)) {
					String parts[] = url.split("=");
					String authCode = parts[1]; // This is your request
												// token.
					mIntent.putExtra(SMConstants.LINKEDIN_AUTH_CODE,
							authCode);
					setResult(RESULT_OK, mIntent);
					finish();

					return true;
				}

				return false;
			}
		});
		webView.loadUrl(url);
	}
}
