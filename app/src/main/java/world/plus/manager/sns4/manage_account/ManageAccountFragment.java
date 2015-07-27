package world.plus.manager.sns4.manage_account;

import java.util.ArrayList;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.main.CommonFragment;
import world.plus.manager.sns4.util.GuideDisplayer;
import world.plus.manager.sns4.util.OnLoginErrorListener;
import world.plus.manager.sns4.util.ProfileManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ManageAccountFragment extends CommonFragment implements
		OnProfileDownloadFinshListener, OnLoginListener,
		OnLoginErrorListener {

	// profile user name manager
	private ProfileManager mProfileUserNameManager;
	// Twitter OAuth helper class
	private TwitterOAuthHelper mTwitterOAuthHelper;
	private FoursquareOAuthHelper mFoursquareOAuthHelper;
	private AppnetOAuthHelper mAppnetOAuthHelper;
	private LinkedinOAuthHelper mLinkedinOAuthHelper;

	private ProgressDialog mConnectionProgressDialog;
	// Custom spinner that can select same item
	private world.plus.manager.sns4.manage_account.SpinnerTrigger mSpinner;
	private ArrayList<Integer> mSnsArray;
	private AddSnsListAdapter mAdapter;

	public ManageAccountFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_manage_account,
				container, false);

		init();

		// Show Admob
		showAdmob(mRootView);
		// Create SNS add spinner
		createSpinner(mRootView);

		makeList(mRootView);

		return mRootView;

	}

	/**
	 * If it is firt run, show toast to tell user to click plus button to log in
	 */
	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences sharedPreference = mActivity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		// When app is installed first, run this fragment to add SNS. After
		// that, do not run this fragment at first when launch this app
		if (sharedPreference.getBoolean(SMConstants.KEY_FIRST_INSTALLED,
				true)) {

			// Set KEY_FIRST_INSTALLED on sharedpreference false
			Editor editor = sharedPreference.edit();
			editor.putBoolean(SMConstants.KEY_FIRST_INSTALLED, false);
			editor.commit();
		}
	}

	/**
	 * Send to Google Analytics
	 */
	@Override
	public void onResume() {
		super.onResume();
		// When ManageAccountFragment first run, show guide image
		GuideDisplayer.show(mActivity, mRootView, mSharedPreference,
				SMConstants.FIRST_RUN_MANAGE_ACCOUNT, mSharedPreference
						.getBoolean(SMConstants.FIRST_RUN_MANAGE_ACCOUNT,
								true), R.drawable.guide_manage_account);

		mTracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
		mTracker.send(MapBuilder.createAppView().build());

	}

	/**
	 * Called when logged in Facebook
	 */
	@Override
	public void onLogin() {
		// Show progress dialog
		mConnectionProgressDialog.show();
	}

	/**
	 * Called when error happened at login to Twitter
	 */
	@Override
	public void onError(int snsType) {
		mConnectionProgressDialog.dismiss();

		Toast.makeText(mActivity,
				R.string.fail_login_to + SMConstants.getSnsName(snsType),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_manage_account_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_sns:
			addSns();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Instantiate ProfileUsernameManager to manage profile and user name
		mProfileUserNameManager = new ProfileManager(mActivity, this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Twitter Oauth
		if (requestCode == SMConstants.TWITTER_OAUTH
				&& resultCode == Activity.RESULT_OK) {
			mConnectionProgressDialog.show();
			// Save login in sharedpreference
			mTwitterOAuthHelper.saveTokenToPreference(data);
			// Display Twitter login UI

			mProfileUserNameManager.getTwitterProfileData();
			return;

		}

		// Foursquare Oauth
		if (requestCode == SMConstants.FOURSQUARE_OAUTH
				&& resultCode == Activity.RESULT_OK) {
			mConnectionProgressDialog.show();
			// Save login in sharedpreference
			mFoursquareOAuthHelper.saveTokenToPreference(data);

			return;
		}

		// Appnet Oauth
		if (requestCode == SMConstants.APPNET_OAUTH
				&& resultCode == Activity.RESULT_OK) {
			mConnectionProgressDialog.show();
			// Save login in sharedpreference
			mAppnetOAuthHelper.saveTokenToPreference(data);

			return;
		}
		// Linkedin Oauth
		if (requestCode == SMConstants.LINKEDIN_OAUTH
				&& resultCode == Activity.RESULT_OK) {
			mConnectionProgressDialog.show();
			// Save login in sharedpreference
			mLinkedinOAuthHelper.saveTokenToPreference(data);

			return;
		}
	}

	/**
	 * Called when profile image download finished
	 */
	@Override
	public void onProfileImageSaved(int snsName) {
		if (mConnectionProgressDialog.isShowing())
			mConnectionProgressDialog.dismiss();
		addRowToList(snsName);

	}

	private void createSpinner(View mRootView) {
		// Show sns spinner
		mSpinner = (world.plus.manager.sns4.manage_account.SpinnerTrigger) mRootView
				.findViewById(R.id.spinner_sns_add);
		mSpinner.setPrompt(getString(R.string.social_networks));

		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(mActivity, R.array.array_social_networks,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);

		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			private boolean mIsFirstSelected = true;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mIsFirstSelected) {
					mIsFirstSelected = false;
					return;
				}

				doLogin(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void doLogin(int position) {
		// TODO Auto-generated method stub

		PlusOAuth plusOAuth = getOAuthHelper(position);
		if (plusOAuth.isLoggedInAlready()) {
			showAlreadyLoginToast();

		} else {
			plusOAuth.doLogin();
		}

	}

	private PlusOAuth getOAuthHelper(int position) {
		switch (position) {
		case 0:
			return new FacebookOAuthHelper(mActivity, this);

		case 1:
			return mTwitterOAuthHelper;

		case 2:
			// Perform actual login in MainActivity
			return new GooglePlusOAuthHelper(mActivity);

		case 3:
			return mFoursquareOAuthHelper;

		case 4:
			return mLinkedinOAuthHelper;

		case 5:
			return mAppnetOAuthHelper;

		}
		return null;
	}

	private void showAlreadyLoginToast() {
		// TODO Auto-generated method stub
		Toast.makeText(mActivity, R.string.already_login, Toast.LENGTH_SHORT)
				.show();
	}

	private void addSns() {
		mSpinner.performClick();

	}

	/**
	 * Show Admob
	 */
	private void showAdmob(View rootView) {
		// 애드몹 광고 생성
		AdView adview = (AdView) rootView.findViewById(R.id.ad_manage_account);

		// AdRequest re = new AdRequest();
		AdRequest request = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		adview.loadAd(request);

	}

	private void addRowToList(int snsName2) {

		mSnsArray.add(snsName2);
		mAdapter.notifyDataSetChanged();

	}

	/**
	 * Init variables
	 */
	private void init() {
		
		 mTwitterOAuthHelper= new TwitterOAuthHelper(mActivity, this);
		 mFoursquareOAuthHelper= new FoursquareOAuthHelper(mActivity, this);
		 mAppnetOAuthHelper= new AppnetOAuthHelper(mActivity, this);
		 mLinkedinOAuthHelper= new LinkedinOAuthHelper(mActivity, this);
		
		

		// Instantiate progress dialog that will show during log in
		mConnectionProgressDialog = new ProgressDialog(mActivity);
		mConnectionProgressDialog.setMessage("Signing in...");

		setHasOptionsMenu(true);

	}

	/**
	 * Make sns list
	 * 
	 * @param mRootView
	 */
	private void makeList(View mRootView) {
		ListView snsList = (ListView) mRootView
				.findViewById(R.id.list_sns_account);
		mSnsArray = new ArrayList<Integer>();
		checkSnsLogin();

		// If size of mSnsArray is zero, show guide text
		// if (mSnsArray.size() == 0)
		// showGuideText(mRootView);

		mAdapter = new AddSnsListAdapter(mActivity, this, mSnsArray);
		snsList.setAdapter(mAdapter);

	}

	/**
	 * Check SNS login state
	 */
	private void checkSnsLogin() {
		if (mSharedPreference.getBoolean(SMConstants.KEY_GOOGLE_PLUS_LOGIN,
				false))
			mSnsArray.add(SMConstants.GOOGLE_PLUS);
		if (mSharedPreference.getBoolean(SMConstants.KEY_FACEBOOK_LOGIN,
				false))
			mSnsArray.add(SMConstants.FACEBOOK);
		if (mSharedPreference.getBoolean(SMConstants.KEY_TWITTER_LOGIN,
				false))
			mSnsArray.add(SMConstants.TWITTER);
		if (mSharedPreference.getBoolean(SMConstants.KEY_FOURSQUARE_LOGIN,
				false))
			mSnsArray.add(SMConstants.FOURSQUARE);
		if (mSharedPreference.getBoolean(SMConstants.KEY_APPNET_LOGIN,
				false))
			mSnsArray.add(SMConstants.APPNET);
		if (mSharedPreference.getBoolean(SMConstants.KEY_LINKEDIN_LOGIN,
				false))
			mSnsArray.add(SMConstants.LINKEDIN);

	}

}
