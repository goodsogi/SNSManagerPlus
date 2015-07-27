package world.plus.manager.sns4.manage_account;

import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;

public abstract class PlusOAuth {
	protected Editor mEditor;
	protected Activity mActivity;
	protected SharedPreferences mSharedPreference;
	protected Fragment mFragment;
	public PlusOAuth(Activity activity, Fragment fragment){
		mActivity = activity;
		mFragment = fragment;

		mSharedPreference = mActivity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreference.edit();
	}
	protected abstract void doLogin();
	protected abstract void doLogout();
	protected abstract boolean isLoggedInAlready();
	protected abstract void saveLogInStatePreference(boolean b);
	

}
