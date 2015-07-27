package world.plus.manager.sns4.manage_account;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.LoginListener;
import world.plus.manager.sns4.util.PlusHttpClient;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class LinkedinOAuthHelper extends PlusOAuth {
	private final LoginListener mLoginListener;
	private String mAccessToken;

	public LinkedinOAuthHelper(Activity activity, Fragment fragment) {
		super(activity, fragment);
		mLoginListener = (LoginListener) fragment;
	}

	public boolean isLoggedInAlready() {
		return mSharedPreference.getBoolean(SMConstants.KEY_LINKEDIN_LOGIN,
				false);
	}

	public void doLogin() {

		
		// Start webview to get oauth token
		Intent i = new Intent(mActivity, WebviewLoginActivity.class);
		i.putExtra(SMConstants.KEY_URL,
				SMConstants.LINKEDIN_AUTH_URL_TOTAL);
		mFragment.startActivityForResult(i, SMConstants.LINKEDIN_OAUTH);
	}

	/**
	 * Log out Linkedin
	 */
	public void doLogout() {

		mEditor.remove(SMConstants.LINKEDIN_ACCESS_TOKEN);
		mEditor.remove(SMConstants.KEY_LINKEDIN_LOGIN);
		mEditor.commit();

	}

	/**
	 * Save Twitter token to sharedpreference
	 * 
	 * @param data
	 */
	public void saveTokenToPreference(Intent data) {

		String authCode = (String) data.getExtras().get(
				SMConstants.LINKEDIN_AUTH_CODE);

		new GetAccessTokenTask().execute(authCode);

	}

	

	/**
	 * Get LinkedIn access token
	 * 
	 * @author user
	 * 
	 */
	private class GetAccessTokenTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return PlusHttpClient.doGet(SMConstants.LINKEDIN_TOKEN_URL
					+ "grant_type=authorization_code" + "&code=" + params[0]
					+ "&redirect_uri=" + SMConstants.LINKEDIN_CALLBACK_URL
					+ "&client_id=" + SMConstants.LINKEDIN_API_KEY
					+ "&client_secret=" + SMConstants.LINKEDIN_SECRET_KEY);

		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			JSONObject data;
			String accessToken = null;
			try {
				data = (JSONObject) new JSONTokener(response).nextValue();

				accessToken = data.optString("access_token");
				Log.i("linkedin", "accessToken: " + accessToken);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            mAccessToken = accessToken;

			saveLogInStatePreference(true);
			mLoginListener.onLoginSuccess(SMConstants.LINKEDIN);
		}

	}

	@Override
	protected void saveLogInStatePreference(boolean isLogin) {
		mEditor.putString(SMConstants.LINKEDIN_ACCESS_TOKEN,
				mAccessToken);
		mEditor.putBoolean(SMConstants.KEY_LINKEDIN_LOGIN, isLogin);
		mEditor.commit();

	}

}
