package world.plus.manager.sns4.manage_account;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.InternectConnectionDetector;
import world.plus.manager.sns4.util.LoginListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Twitter Login Helper
 * 
 * @author user
 * 
 */
public class TwitterOAuthHelper extends PlusOAuth {

	// Twitter
	private Twitter mTwitter;

	// Catch error that user is null at line 182
	private EasyTracker mTracker;
	private AccessToken mAccessToken;
	private final LoginListener mLoginListener;

	public TwitterOAuthHelper(Activity activity, Fragment fragment) {
		super(activity, fragment);

		// Create twitter instance
		mTwitter = new TwitterFactory().getInstance();
		mTwitter.setOAuthConsumer(SMConstants.TWITTER_CONSUMER_KEY,
				SMConstants.TWITTER_CONSUMER_SECRET);

		

		// Instantiate google analytics
		mTracker = EasyTracker.getInstance(mActivity);

		mLoginListener = (LoginListener) fragment;

	}

	/**
	 * Log in twitter
	 * */
	public void doLogin() {
		// If phone is not connected to internet, show error message
		if (!InternectConnectionDetector.hasConnection(mActivity))
			return;
		// Get Twitter request token
		new GetTwitterRequestTokenTask().execute();

	}

	/**
	 * Log out Twitter
	 */
	public void doLogout() {
		// If phone is not connected to internet, show error message
		if (!InternectConnectionDetector.hasConnection(mActivity))
			return;

		mEditor.remove(SMConstants.KEY_TWITTER_ACCESS_TOKEN);
		mEditor.remove(SMConstants.KEY_TWITTER_ACCESS_TOKEN_SECRET);
		mEditor.remove(SMConstants.KEY_TWITTER_LOGIN);
		mEditor.commit();

	}

	/**
	 * Save Twitter token to sharedpreference
	 * 
	 * @param data
	 */
	public void saveTokenToPreference(Intent data) {

		String oauthVerifier = (String) data.getExtras().get(
				SMConstants.URL_TWITTER_OAUTH_VERIFIER);
		new GetTwitterAccessTokenTask().execute(oauthVerifier);

	}

	/**
	 * Check user already logged in using twitter Login flag is fetched from
	 * Shared Preferences
	 * */
	public boolean isLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreference.getBoolean(SMConstants.KEY_TWITTER_LOGIN,
				false);
	}

	/**
	 * Get Twitter request token
	 * 
	 * @author user
	 * 
	 */
	class GetTwitterRequestTokenTask extends
			AsyncTask<Void, Void, RequestToken> {

		@Override
		protected RequestToken doInBackground(Void... params) {

			RequestToken mRequestToken = null;
			try {
				// Set access token null because of error:
				// java.lang.IllegalStateException: Access token already
				// available
				// not sure it would work
				mTwitter.setOAuthAccessToken(null);
				mRequestToken = mTwitter
						.getOAuthRequestToken(SMConstants.TWITTER_CALLBACK_URL);

			} catch (TwitterException e) {
				e.printStackTrace();
			}

			return mRequestToken;
		}

		@Override
		protected void onPostExecute(RequestToken requestToken) {
			if (requestToken == null)
				return;

			mTracker.send(MapBuilder.createEvent("OAuth", // Event
					// category
					// (required)
					"process_twitter", // Event action (required)
					"got_request_token", // Event label
					null) // Event value
					.build());

			// Just prevent crash, not solve real problem
			if (!mFragment.isAdded())
				return;


			// Start webview to get oauth token
			Intent i = new Intent(mActivity, WebviewLoginActivity.class);
			i.putExtra(SMConstants.KEY_URL,
					requestToken.getAuthenticationURL());
			mFragment.startActivityForResult(i, SMConstants.TWITTER_OAUTH);

		}

	}

	/**
	 * Get Twitter access token
	 * 
	 * @author user
	 * 
	 */
	class GetTwitterAccessTokenTask extends
			AsyncTask<String, Void, AccessToken> {

		@Override
		protected AccessToken doInBackground(String... params) {

			AccessToken accessToken = null;
			String oauthVerifier = params[0];
			try {

				accessToken = mTwitter.getOAuthAccessToken(oauthVerifier);

			} catch (TwitterException e) {
				e.printStackTrace();
			}

			return accessToken;
		}

		@Override
		protected void onPostExecute(AccessToken accessToken) {
			if (accessToken == null)
				return;
			mTracker.send(MapBuilder.createEvent("OAuth", // Event
					// category
					// (required)
					"process_twitter", // Event action (required)
					"got_access_token", // Event label
					null) // Event value
					.build());


			mAccessToken = accessToken;
			// Set Twitter access token
			mTwitter.setOAuthAccessToken(accessToken);


           saveLogInStatePreference(true);
			mLoginListener.onLoginSuccess(SMConstants.TWITTER);
			
		}

	}

	
	@Override
	protected void saveLogInStatePreference(boolean isLogin) {
		String theToken = mAccessToken.getToken();
		String theTokenSecret = mAccessToken.getTokenSecret();

		mEditor.putString(SMConstants.KEY_TWITTER_ACCESS_TOKEN,
				theToken);
		mEditor.putString(SMConstants.KEY_TWITTER_ACCESS_TOKEN_SECRET,
				theTokenSecret);
		mEditor.putBoolean(SMConstants.KEY_TWITTER_LOGIN, isLogin);
		mEditor.commit();

	}

}