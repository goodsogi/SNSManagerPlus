package world.plus.manager.sns4.connect_sns;

import java.util.ArrayList;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.main.CommonFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Connect to SNS fragment
 * 
 * @author user
 * 
 */
public class ConnectSNSFragment extends CommonFragment {

	public ConnectSNSFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_connect_sns,
				container, false);

		makeGridView(rootView);

		showAdmob(rootView);

		return rootView;

	}

	/**
	 * Check app installed
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Send to Google Analytics
		mTracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
		mTracker.send(MapBuilder.createAppView().build());
	}

	/**
	 * Make grid view
	 * 
	 * @param rootView
	 */
	private void makeGridView(View rootView) {
		// TODO Auto-generated method stub
		ArrayList<Integer> loginSNSs = checkSnsLogin();

		GridView gridView = (GridView) rootView
				.findViewById(R.id.gridview_connect_sns);
		gridView.setAdapter(new GridAdapter(mActivity, loginSNSs));
	}

	/**
	 * Check SNS login state
	 * 
	 * @return
	 */
	private ArrayList<Integer> checkSnsLogin() {

		ArrayList<Integer> loginSNSs = new ArrayList<Integer>();

		if (mSharedPreference.getBoolean(SMConstants.KEY_GOOGLE_PLUS_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.GOOGLE_PLUS);

		}

		if (mSharedPreference.getBoolean(SMConstants.KEY_FACEBOOK_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.FACEBOOK);

		}

		if (mSharedPreference.getBoolean(SMConstants.KEY_TWITTER_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.TWITTER);

		}

		if (mSharedPreference.getBoolean(SMConstants.KEY_FOURSQUARE_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.FOURSQUARE);

		}

		if (mSharedPreference.getBoolean(SMConstants.KEY_APPNET_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.APPNET);

		}

		if (mSharedPreference.getBoolean(SMConstants.KEY_LINKEDIN_LOGIN,
				false)) {
			loginSNSs.add(SMConstants.LINKEDIN);

		}

		return loginSNSs;

	}

	/**
	 * Show Admob
	 */
	private void showAdmob(View rootView) {
		// 애드몹 광고 생성
		AdView adview = (AdView) rootView.findViewById(R.id.ad_connect_sns);

		// AdRequest re = new AdRequest();
		AdRequest request = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		adview.loadAd(request);

	}

}
