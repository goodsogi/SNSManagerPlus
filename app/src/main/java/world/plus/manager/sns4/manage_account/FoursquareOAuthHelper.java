package world.plus.manager.sns4.manage_account;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.InternectConnectionDetector;
import world.plus.manager.sns4.util.LoginListener;
import world.plus.manager.sns4.util.PlusHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * Twitter Login Helper
 * 
 * @author user
 * 
 */
public class FoursquareOAuthHelper extends PlusOAuth {
	private final LoginListener mLoginListener;
	private String mAccessToken;

	public FoursquareOAuthHelper(Activity activity, Fragment fragment) {
		super(activity, fragment);
		mLoginListener = (LoginListener) fragment;
		}

	/**
	 * Log in Foursquare
	 * */
	public void doLogin() {
		// If phone is not connected to internet, show error message
		if (!InternectConnectionDetector.hasConnection(mActivity))
			return;
			// Start webview to get oauth token
		Intent i = new Intent(mActivity, WebviewLoginActivity.class);
		i.putExtra(SMConstants.KEY_URL,
				SMConstants.FOURSQUARE_AUTH_URL_TOTAL);
		mFragment.startActivityForResult(i, SMConstants.FOURSQUARE_OAUTH);

	}

	/**
	 * Log out Foursquare
	 */
	public void doLogout() {

		mEditor.remove(SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN);
		mEditor.remove(SMConstants.KEY_FOURSQUARE_LOGIN);
		mEditor.commit();

	}

	/**
	 * Save Twitter token to sharedpreference
	 * 
	 * @param data
	 */
	public void saveTokenToPreference(Intent data) {

		String requestToken = (String) data.getExtras().get(
				SMConstants.FOURSQUARE_REQUEST_TOKEN);

		new GetFoursquareAccessTokenTask().execute(requestToken);

	}

	/**
	 * Check user already logged in using twitter Login flag is fetched from
	 * Shared Preferences
	 * */
	public boolean isLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreference.getBoolean(
				SMConstants.KEY_FOURSQUARE_LOGIN, false);
	}

	private class GetFoursquareAccessTokenTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return PlusHttpClient
					.doGet(SMConstants.FOURSQUARE_TOKEN_URL_TOTAL
							+ "&code=" + params[0]);
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if (response == null)
				return;
			JSONObject jsonObj = null;
			String accessToken = null;
			try {
				jsonObj = (JSONObject) new JSONTokener(response).nextValue();

				accessToken = jsonObj.getString("access_token");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


           mAccessToken = accessToken;

			saveLogInStatePreference(true);
			mLoginListener.onLoginSuccess(SMConstants.FOURSQUARE);

		}

	}

	
	@Override
	protected void saveLogInStatePreference(boolean isLogin) {
		mEditor.putString(SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN,
				mAccessToken);
		mEditor.putBoolean(SMConstants.KEY_FOURSQUARE_LOGIN, isLogin);
		mEditor.commit();

	}
}