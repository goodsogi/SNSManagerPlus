package world.plus.manager.sns4.manage_account;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.InternectConnectionDetector;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class AppnetOAuthHelper extends PlusOAuth {

	public AppnetOAuthHelper(Activity activity, Fragment fragment) {
		super(activity, fragment);

	}

	public boolean isLoggedInAlready() {
		return mSharedPreference.getBoolean(SMConstants.KEY_APPNET_LOGIN,
				false);
	}

	public void doLogin() {
	// If phone is not connected to internet, show error message
		if (!InternectConnectionDetector.hasConnection(mActivity))
			return;

		// Start webview to get oauth token
		Intent i = new Intent(mActivity, WebviewLoginActivity.class);
		i.putExtra(SMConstants.KEY_URL,
				SMConstants.APPNET_AUTH_URL_TOTAL);
		mFragment.startActivityForResult(i, SMConstants.APPNET_OAUTH);
	}

	/**
	 * Log out Appnet
	 */
	public void doLogout() {

		mEditor.remove(SMConstants.APPNET_ACCESS_TOKEN);
		mEditor.remove(SMConstants.KEY_APPNET_LOGIN);
		mEditor.commit();

	}

	/**
	 * Save Twitter token to sharedpreference
	 * 
	 * @param data
	 */
	public void saveTokenToPreference(Intent data) {

		String accessToken = (String) data.getExtras().get(
				SMConstants.APPNET_ACCESS_TOKEN);
		mEditor.putString(SMConstants.APPNET_ACCESS_TOKEN, accessToken);
		mEditor.putBoolean(SMConstants.KEY_APPNET_LOGIN, true);
		mEditor.commit();


	}

	

	@Override
	protected void saveLogInStatePreference(boolean b) {
		// TODO Auto-generated method stub

	}

}
