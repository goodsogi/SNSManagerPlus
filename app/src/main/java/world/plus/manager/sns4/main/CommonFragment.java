package world.plus.manager.sns4.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;

public class CommonFragment extends SherlockFragment {
	protected View mRootView;
	protected FragmentActivity mActivity;
	protected EasyTracker mTracker;
	protected SharedPreferences mSharedPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		// Instantiate google analytics
		mTracker = EasyTracker.getInstance(mActivity);

		mSharedPreference = mActivity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
	}

}
