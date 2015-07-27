package world.plus.manager.sns4.write;

import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;

public abstract class PlusPost {
	protected Activity mActivity;
	// Shared Preferences
	protected SharedPreferences mSharedPreferences;
	protected Editor mEditor;
	protected Fragment mFragment;
	protected PlusPost(Activity activity, Fragment fragment) {
		mActivity = activity;
		mFragment = fragment;
		// Shared Preferences
		mSharedPreferences = mActivity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	
	
	protected abstract void post(String text, String imgUrl, String venueId, double lat,
			double lon);

	protected abstract void savePostPreference(boolean value);

}
