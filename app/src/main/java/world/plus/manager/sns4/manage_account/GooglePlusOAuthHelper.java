package world.plus.manager.sns4.manage_account;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.main.MainActivity;
import android.app.Activity;

public class GooglePlusOAuthHelper extends PlusOAuth {
	public GooglePlusOAuthHelper(Activity activity) {
		super(activity, null);

	}

	/**
	 * Save Google Plus login to sharedpreference
	 * 
	 * @param b
	 * 
	 */
	public void saveLogInStatePreference(boolean b) {

		mEditor.putBoolean(SMConstants.KEY_GOOGLE_PLUS_LOGIN, b);

		mEditor.commit();

	}

	/**
	 * Check user already logged in Google Plus
	 * */
	public boolean isLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreference.getBoolean(
				SMConstants.KEY_GOOGLE_PLUS_LOGIN, false);
	}

	@Override
	public void doLogin() {

		
		// TODO Auto-generated method stub
		((MainActivity) mActivity).logInGooglePlus();
	}

	@Override
	protected void doLogout() {
		// TODO Auto-generated method stub
		((MainActivity) mActivity).logOutGooglePlus();
	}
}
