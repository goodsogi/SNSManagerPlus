package world.plus.manager.sns4.write;

import java.util.LinkedList;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.main.CommonFragment;
import world.plus.manager.sns4.main.MainActivity;
import world.plus.manager.sns4.manage_account.AppnetOAuthHelper;
import world.plus.manager.sns4.manage_account.FacebookOAuthHelper;
import world.plus.manager.sns4.manage_account.FoursquareOAuthHelper;
import world.plus.manager.sns4.manage_account.GooglePlusOAuthHelper;
import world.plus.manager.sns4.manage_account.LinkedinOAuthHelper;
import world.plus.manager.sns4.manage_account.OnProfileDownloadFinshListener;
import world.plus.manager.sns4.manage_account.TwitterOAuthHelper;
import world.plus.manager.sns4.util.CameraAlbumActivity;
import world.plus.manager.sns4.util.ClickGuard;
import world.plus.manager.sns4.util.CustomToastManager;
import world.plus.manager.sns4.util.DpPixelConverter;
import world.plus.manager.sns4.util.ErrorLogger;
import world.plus.manager.sns4.util.GuideDisplayer;
import world.plus.manager.sns4.util.ImagePathFinder;
import world.plus.manager.sns4.util.InternectConnectionDetector;
import world.plus.manager.sns4.util.LoginListener;
import world.plus.manager.sns4.util.PhotoResizer;
import world.plus.manager.sns4.util.PiccasaImageSaver;
import world.plus.manager.sns4.util.PopupDialog;
import world.plus.manager.sns4.util.ProfileManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class WriteFragment extends CommonFragment implements
		OnPostCallbackListener, LoginListener, OnProfileDownloadFinshListener {

	private static WriteFragment instance;
	protected int MAX_TEXT_LIMIT;
	// post helper class
	private TwitterPost mTwitterPost;
	private FacebookPost mFacebookPost;
	private GooglePlusPost mGooglePlusPost;
	private String mImgUri;
	// flag to check if send to certain sns
	private boolean mPostTwitter;
	private boolean mPostFacebook;
	private boolean mPostGooglePlus;

	private EditText mEditText;
	private ImageView mPhoto;
	private Editor mEditor;
	// Handle no sns button click
	OnNoSnsButtonClickListener mNoSnsButtonClickListener;
	public boolean mIsPhotoReady = true;
	// Handle when post is sent
	OnPostSentListener mOnPostSentListener;
	private NotificationManager mNotifyMgr;
	private TextView mTextLength;
	private PhotoResizer mResizer;
	private LinkedList<PostRequest> mRequestQueue;
	private ErrorLogger mErrorLogger;
	private FoursquarePost mFoursquarePost;
	private boolean mPostFoursquare;
	private String mSelectedFoursquareVenuId;
	private String mSelectedFacebookPlaceId;
	private String mSelectedTwitterPlaceId;
	private double mSelectedLocationLat;
	private double mSelectedLocationLon;
	private String mSelectedFoursquareVenuName;
	private String mSelectedFacebookPlaceName;
	private AppnetPost mAppnetPost;
	private boolean mPostAppnet;
	private LinkedinPost mLinkedinPost;
	private boolean mPostLinkedin;
	private ImageView mAddGeoTag;
	private boolean mIsFirstShowKeyboard = true;
	protected TwitterOAuthHelper mTwitterOAuthHelper;
	protected FoursquareOAuthHelper mFoursquareOAuthHelper;
	protected LinkedinOAuthHelper mLinkedinOAuthHelper;
	protected AppnetOAuthHelper mAppnetOAuthHelper;
	private InterstitialAd mInterstitial;
	final static int NOTI_ID = 1;
	private static final String MY_AD_UNIT_ID = "ca-app-pub-7576584379236747/9777894797"; // 애드몹
																							// 광고
																							// 아이디

	public WriteFragment() {
		// Empty constructor required for fragment subclasses
	}

	public static WriteFragment getInstance() {
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;

		mRootView = inflater.inflate(R.layout.fragment_write, container, false);

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Initialize variables
		init();

		// Initialize EditText
		initEditText(mRootView);

		

		addButtonListener();

		initLoginButtons();
		
		restoreDraft();
		
		// Add listener to add geo tag button
		addListenerAddGeoTag(mRootView);

	}

	@Override
	public void onProfileImageSaved(int snsName) {
           showUserName(snsName);
		   showUserProfile(snsName);
		   saveProfileShownSharedpreference();
	}

	private void saveProfileShownSharedpreference() {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(SMConstants.KEY_IS_PROFILE_SHOWN, true);
		editor.commit();

	}

	private void showUserProfile(int snsName) {
		ImageView userProfile = (ImageView) mActivity.findViewById(R.id.userProfile);
		userProfile.setImageBitmap(getBitmap(snsName));

	}

	private Bitmap getBitmap(int snsName) {



		ProfileManager profileManager = new ProfileManager(mActivity, this);
		return profileManager.getSavedProfileBitmap(snsName);
	}

	private void showUserName(int snsName) {
		TextView userName = (TextView) mActivity.findViewById(R.id.userName);
		userName.setText(getUserName(snsName));
 	}

	private String getUserName(int snsName) {
		ProfileManager profileManager = new ProfileManager(mActivity, this);
		return profileManager.getSavedUserName(snsName);

	}

	private void initLoginButtons() {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SMConstants.PREF_NAME, Context.MODE_PRIVATE);

		Button twitter = (Button) mActivity.findViewById(R.id.twitter);
		twitter.setSelected(sharedPreferences.getBoolean(SMConstants.KEY_POST_TWITTER, false));

		Button facebook = (Button) mActivity.findViewById(R.id.facebook);
		facebook.setSelected(sharedPreferences.getBoolean(SMConstants.KEY_POST_FACEBOOK, false));

		Button foursquare = (Button) mActivity.findViewById(R.id.foursquare);
		foursquare.setSelected(sharedPreferences.getBoolean(SMConstants.KEY_POST_FOURSQUARE, false));


		Button gplus = (Button) mActivity.findViewById(R.id.gplus);
		gplus.setSelected(sharedPreferences.getBoolean(SMConstants.KEY_POST_GOOGLE_PLUS, false));


		Button linkedin = (Button) mActivity.findViewById(R.id.linkedin);
		linkedin.setSelected(sharedPreferences.getBoolean(SMConstants.KEY_POST_LINKEDIN, false));












	}

	@Override
	public void onLoginSuccess(int snsType) {


		Button button = null;
		switch (snsType) {
			case SMConstants.FACEBOOK:  button = (Button) mActivity.findViewById(R.id.facebook);
				if(!isProfileShown()) showFacebookProfile();
				break;
			case SMConstants.TWITTER:  button = (Button) mActivity.findViewById(R.id.twitter);
				if(!isProfileShown()) showTwitterProfile();
				break;
			case SMConstants.GOOGLE_PLUS:  button = (Button) mActivity.findViewById(R.id.gplus);
				break;
			case SMConstants.FOURSQUARE:  button = (Button) mActivity.findViewById(R.id.foursquare);
				break;
			case SMConstants.LINKEDIN:  button = (Button) mActivity.findViewById(R.id.linkedin);
				break;



		}

		button.setSelected(true);

		Toast.makeText(mActivity, "Logged in " + SMConstants.getSnsName(snsType), Toast.LENGTH_SHORT).show();

	}

	private boolean isProfileShown() {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(SMConstants.KEY_IS_PROFILE_SHOWN, false);
	}

	private void showTwitterProfile() {
		ProfileManager profileUserNameManager = new ProfileManager(mActivity, this);
		profileUserNameManager.getTwitterProfileData();
	}

	private void showFacebookProfile() {
		ProfileManager profileUserNameManager = new ProfileManager(mActivity, this);
		profileUserNameManager.getFacebookProfileData();
	}

	private void addButtonListener() {

		// 지도
		ImageView map = (ImageView) mActivity.findViewById(R.id.add_geotag);
		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				if (!v.isSelected()) {
					Intent intent = new Intent(mActivity, PlacesActivity.class);
					startActivityForResult(intent,
							SMConstants.REQUEST_PLACES);
				} else {
					showGeoPopup();
				}

			}
		});

		// 휴지통
		ImageView trashCan = (ImageView) mActivity.findViewById(R.id.trash_can);
		trashCan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				showDeleteDialog();

			}
		});

		// 사진
		FrameLayout photo = (FrameLayout) mActivity
				.findViewById(R.id.photo_frame);
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				showPhotoPopup();

			}
		});

		mPhoto = (ImageView) mActivity.findViewById(R.id.photo_imageview);

		// 전송
		Button send = (Button) mActivity.findViewById(R.id.send_post);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				post();

			}
		});

		// 트위터
		Button twitter = (Button) mActivity.findViewById(R.id.twitter);
		twitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isSelected()) {

					if (!mSharedPreference.getBoolean(
							SMConstants.KEY_TWITTER_LOGIN, false)) {

						mTwitterOAuthHelper = new TwitterOAuthHelper(mActivity,
								WriteFragment.this);
						mTwitterOAuthHelper.doLogin();
						mEditor.putBoolean(SMConstants.KEY_POST_TWITTER, true);
						setMaxTextLength();

						return;
					}

					v.setSelected(true);
					mEditor.putBoolean(SMConstants.KEY_POST_TWITTER, true);
					setMaxTextLength();

				} else {
					v.setSelected(false);
					mEditor.putBoolean(SMConstants.KEY_POST_TWITTER, false);

				}
				mEditor.commit();

			}
		});

		// 페이스북
		Button facebook = (Button) mActivity.findViewById(R.id.facebook);
		facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isSelected()) {

					if (!mSharedPreference.getBoolean(
							SMConstants.KEY_FACEBOOK_LOGIN, false)) {

						FacebookOAuthHelper facebookOAuthHelper = new FacebookOAuthHelper(
								mActivity, WriteFragment.this);
						facebookOAuthHelper.doLogin();

						mEditor.putBoolean(SMConstants.KEY_POST_FACEBOOK, true);
						setMaxTextLength();
						return;

					}
					v.setSelected(true);
					mEditor.putBoolean(SMConstants.KEY_POST_FACEBOOK, true);
					setMaxTextLength();

				} else {
					v.setSelected(false);
					mEditor.putBoolean(SMConstants.KEY_POST_FACEBOOK, false);

				}
				mEditor.commit();

			}
		});

		// 구글플러스
		Button gplus = (Button) mActivity.findViewById(R.id.gplus);
		gplus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isSelected()) {

					if (!mSharedPreference.getBoolean(
							SMConstants.KEY_GOOGLE_PLUS_LOGIN, false)) {

						GooglePlusOAuthHelper googlePlusOAuthHelper = new GooglePlusOAuthHelper(
								mActivity);
						googlePlusOAuthHelper.doLogin();

						mEditor.putBoolean(SMConstants.KEY_POST_GOOGLE_PLUS,
								true);
						setMaxTextLength();

						return;
					}
					v.setSelected(true);
					mEditor.putBoolean(SMConstants.KEY_POST_GOOGLE_PLUS,
							true);
					setMaxTextLength();

				} else {
					v.setSelected(false);
					mEditor.putBoolean(SMConstants.KEY_POST_GOOGLE_PLUS,
							false);

				}
				mEditor.commit();

			}
		});

		// 포스퀘어
		Button foursquare = (Button) mActivity.findViewById(R.id.foursquare);
		foursquare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isSelected()) {

					if (!mSharedPreference.getBoolean(
							SMConstants.KEY_FOURSQUARE_LOGIN, false)) {

						mFoursquareOAuthHelper = new FoursquareOAuthHelper(
								mActivity, WriteFragment.this);
						mFoursquareOAuthHelper.doLogin();

						mEditor.putBoolean(SMConstants.KEY_POST_FOURSQUARE,
								true);
						setMaxTextLength();

						return;
					}
					v.setSelected(true);
					mEditor.putBoolean(SMConstants.KEY_POST_FOURSQUARE,
							true);
					setMaxTextLength();

				} else {
					v.setSelected(false);
					mEditor.putBoolean(SMConstants.KEY_POST_FOURSQUARE,
							false);

				}
				mEditor.commit();

			}
		});

		// linkedin
		Button linkedin = (Button) mActivity.findViewById(R.id.linkedin);
		linkedin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isSelected()) {

					if (!mSharedPreference.getBoolean(
							SMConstants.KEY_LINKEDIN_LOGIN, false)) {

						mLinkedinOAuthHelper = new LinkedinOAuthHelper(
								mActivity, WriteFragment.this);
						mLinkedinOAuthHelper.doLogin();

						mEditor.putBoolean(SMConstants.KEY_POST_LINKEDIN, true);
						setMaxTextLength();

						return;
					}
					v.setSelected(true);
					mEditor.putBoolean(SMConstants.KEY_POST_LINKEDIN, true);
					setMaxTextLength();

				} else {
					v.setSelected(false);
					mEditor.putBoolean(SMConstants.KEY_POST_LINKEDIN, false);

				}
				mEditor.commit();

			}
		});

//		// appnet
//		Button appnet = (Button) mActivity.findViewById(R.id.appnet);
//		appnet.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (!v.isSelected()) {
//
//					v.setSelected(true);
//					if (!mSharedPreference.getBoolean(
//							SMConstants.KEY_APPNET_LOGIN, false)) {
//
//						mAppnetOAuthHelper = new AppnetOAuthHelper(mActivity,
//								WriteFragment.this);
//						mAppnetOAuthHelper.doLogin();
//					}
//
//					mEditor.putBoolean(SMConstants.KEY_POST_APPNET, true);
//					setMaxTextLength();
//
//				} else {
//					v.setSelected(false);
//					mEditor.putBoolean(SMConstants.KEY_POST_APPNET, false);
//
//				}
//				mEditor.commit();
//
//			}
//		});

	}

	/**
	 * Show photo image on ImageView
	 */
	public void showImage(String imgUrl) {
		// If image url is "", hide photo frame
		if (imgUrl.equals("")) {
			// mPhotoFrame.setVisibility(View.GONE);
			return;
		}

		// Set text length limit with image
		setMaxTextLengthWithImage();

		mImgUri = imgUrl;

		// Decode file for the size of photo
		int widthHeight = DpPixelConverter.toPixel(mActivity, 55);
		Bitmap bm = decodeFileForPhotoSize(widthHeight, widthHeight);
		mPhoto.setImageBitmap(bm);
		// Save image path to sharedpreference

		mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, mImgUri);
		mEditor.commit();


	}


	public void runHttpRequest() {

		final PostRequest request = mRequestQueue.peek();
		if (request == null) {
			// Send log to Crashlytics
			Crashlytics.log("Finished posting");
			cancelNotification();
//			mEditor.putString(SMConstants.KEY_DRAFTED_TEXT, "");
//			mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, "");
//			mEditor.commit();

			// 광고 표시
			if (mInterstitial.isLoaded()) {
				mInterstitial.show();
			}

			return;
		}

		switch (request.snsName) {
		case SMConstants.GOOGLE_PLUS:
			mRequestQueue.poll();
			mGooglePlusPost.post(request.message, request.imgUri, null, 0, 0);
			break;

		case SMConstants.FACEBOOK:
			mFacebookPost.post(request.message, request.imgUri,
					request.venueId, 0, 0);
			break;

		case SMConstants.TWITTER:
			mTwitterPost.post(request.message, request.imgUri, request.venueId,
					request.lat, request.lon);
			break;

		case SMConstants.FOURSQUARE:
			mFoursquarePost.post(request.message, request.imgUri,
					request.venueId, 0, 0);
			break;
		case SMConstants.APPNET:
			mAppnetPost.post(request.message, request.imgUri, "", request.lat,
					request.lon);
			break;
		case SMConstants.LINKEDIN:
			mLinkedinPost.post(request.message, request.imgUri, "",
					request.lat, request.lon);
			break;
		}

	}

	/**
	 * Called when succeeded in sending post
	 */
	@Override
	public void onCompleted(final int snsName) {
		mRequestQueue.poll();
		
		
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mActivity, "Sent post to "
						+ SMConstants.getSnsName(snsName),
						Toast.LENGTH_SHORT).show();

			}
		});

		runHttpRequest();
	}

	/**
	 * Called when error occurred during sending post
	 */
	@Override
	public void onError(final int snsName) {

		// Send error event to Google Analytics
		mTracker.send(MapBuilder.createEvent("post", // Event category
				// (required)
				"error_to", // Event action (required)
				SMConstants.getSnsName(snsName), // Event label
				null) // Event value
				.build());

		mErrorLogger.write("try again");
		// 에러처리

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mActivity, "Couldn't send post to "
								+ SMConstants.getSnsName(snsName) + ". Try again",
						Toast.LENGTH_SHORT).show();

			}
		});

		mRequestQueue.poll();
		runHttpRequest();
	}

	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	private void setMaxTextLength() {

		if (mSharedPreference.getBoolean(SMConstants.KEY_POST_TWITTER,
				false)) {
			MAX_TEXT_LIMIT = 140;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_FOURSQUARE, false)) {
			MAX_TEXT_LIMIT = 200;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_APPNET, false)) {
			MAX_TEXT_LIMIT = 256;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_LINKEDIN, false)) {
			MAX_TEXT_LIMIT = 700;
		} else {
			MAX_TEXT_LIMIT = 5000;
		}

		int textLength = mEditText.getText().toString().length();
		int lengh = MAX_TEXT_LIMIT - textLength;
		mTextLength.setText(String.valueOf(lengh));

	}

	private void getPlusPost() {

		mFacebookPost = new FacebookPost(mActivity, this);

		mTwitterPost = new TwitterPost(mActivity, this);
		mGooglePlusPost = new GooglePlusPost(mActivity, this);

		mFoursquarePost = new FoursquarePost(mActivity, this);

		mAppnetPost = new AppnetPost(mActivity, this);
		mLinkedinPost = new LinkedinPost(mActivity, this);
	}

	/**
	 * Initialize variables
	 */
	private void init() {

		mEditor = mSharedPreference.edit();

		// This makes sure that the fragment has implemented

		// Resize image
		mResizer = new PhotoResizer(mActivity);

		// Put requests in queue
		mRequestQueue = new LinkedList<PostRequest>();

		// Gets an instance of the NotificationManager service
		mNotifyMgr = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// write log to try again to file
		mErrorLogger = new ErrorLogger();

		// 광고
		// Create the interstitial.
		mInterstitial = new InterstitialAd(mActivity);
		mInterstitial.setAdUnitId(MY_AD_UNIT_ID);

		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		mInterstitial.loadAd(adRequest);

		// 글쓰기 객체 생성
		getPlusPost();

	}

	/**
	 * Check if no sns was logged in
	 * 
	 * @return
	 */
	private boolean checkNoSnsLogin() {
		boolean isGooglePlusLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_GOOGLE_PLUS_LOGIN, false);
		boolean isFacebooksLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_FACEBOOK_LOGIN, false);
		boolean isTwitterLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_TWITTER_LOGIN, false);
		boolean isFoursquareLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_FOURSQUARE_LOGIN, false);
		boolean isAppnetLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_APPNET_LOGIN, false);
		boolean isLinkedinLogin = mSharedPreference.getBoolean(
				SMConstants.KEY_LINKEDIN_LOGIN, false);

		if (!isFacebooksLogin && !isGooglePlusLogin && !isTwitterLogin
				&& !isFoursquareLogin && !isAppnetLogin & !isLinkedinLogin) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK)
			return;
		String photoSource = null;
		switch (requestCode) {

		// Get image from camera or album
		case SMConstants.REQUEST_PLACES:
			mSelectedFoursquareVenuId = data
					.getStringExtra(SMConstants.SELECTED_FOURSQUARE_VENU_ID);
			mSelectedFacebookPlaceId = data
					.getStringExtra(SMConstants.SELECTED_FACEBOOK_PLACE_ID);
			mSelectedTwitterPlaceId = data
					.getStringExtra(SMConstants.SELECTED_TWITTER_PLACE_ID);
			mSelectedFoursquareVenuName = data
					.getStringExtra(SMConstants.SELECTED_FOURSQUARE_VENU_NAME);
			mSelectedFacebookPlaceName = data
					.getStringExtra(SMConstants.SELECTED_FACEBOOK_PLACE_NAME);
			mSelectedLocationLat = data.getDoubleExtra(
					SMConstants.SELECTED_LOCATION_LAT, 0);
			mSelectedLocationLon = data.getDoubleExtra(
					SMConstants.SELECTED_LOCATION_LON, 0);

			if ((mSelectedLocationLat != 0 && mSelectedLocationLon != 0)
					|| mSelectedFoursquareVenuId != null
					|| mSelectedFacebookPlaceId != null
					|| mSelectedTwitterPlaceId != null) {
				mAddGeoTag.setSelected(true);
			} else {
				mAddGeoTag.setSelected(false);
			}

			// Show place page on EditText
			showPlacePage();

			break;

		case SMConstants.GOOGLE_PLUS_POST:

			runHttpRequest();
			return;

			// Get image from camera or album
		case SMConstants.FROM_CAMERA:
			photoSource = "camera";
			mImgUri = data.getStringExtra(SMConstants.EXTRA_IMAGE_PATH);

			showImage(mImgUri);

			break;

		// Get image from camera or album
		case SMConstants.FROM_GALLERY:
			String uriString = data
					.getStringExtra(SMConstants.EXTRA_IMAGE_PATH);
			Uri uri = Uri.parse(uriString);

			// If image is from camera album, extract path using mediastore
			if (uriString.toString().contains("storage")) {
				photoSource = "camera_album";
				mImgUri = ImagePathFinder.getPathCameraAlbum(mActivity, uri);
				showImage(mImgUri);

			} else {
				photoSource = "picasa";
				// Save image that has uri like
				// "content://com.google.android.gallery3d.provider/picasa/item/5973539989398179218"
				// to file
				new ImageFileSaveTask().execute(uri);

			}

			break;

		case SMConstants.TWITTER_OAUTH:
			// Save login in sharedpreference
			mTwitterOAuthHelper.saveTokenToPreference(data);
			return;

		case SMConstants.FOURSQUARE_OAUTH:
			// Save login in sharedpreference
			mFoursquareOAuthHelper.saveTokenToPreference(data);
			return;

		case SMConstants.APPNET_OAUTH:
			// Save login in sharedpreference
			mAppnetOAuthHelper.saveTokenToPreference(data);
			return;

		case SMConstants.LINKEDIN_OAUTH:
			// Save login in sharedpreference
			mLinkedinOAuthHelper.saveTokenToPreference(data);
			return;

		}

		// Send photo event to Google Analytics
		mTracker.send(MapBuilder.createEvent("ui_action", // Event category
															// (required)
				"attach_photo", // Event action (required)
				photoSource, // Event label
				null) // Event value
				.build());

	}

	/**
	 * Show place page on EditText
	 */
	private void showPlacePage() {
		UrlShortener shortener = new UrlShortener();
		if (mSelectedFoursquareVenuId != null
				&& !mSelectedFoursquareVenuId.equals("")) {
			mEditText.append("(@ " + mSelectedFoursquareVenuName + ") ");
			shortener.shorten(mEditText, "https://foursquare.com/v/"
					+ mSelectedFoursquareVenuId);

		} else if (mSelectedFacebookPlaceId != null
				&& !mSelectedFacebookPlaceId.equals("")) {
			mEditText.append("(@ " + mSelectedFacebookPlaceName + ") ");
			shortener.shorten(mEditText, "https://www.facebook.com/pages/"
					+ mSelectedFacebookPlaceName + "/"
					+ mSelectedFacebookPlaceId);

		}

	}

	/**
	 * Cancel notification
	 */
	private void cancelNotification() {
//		String tickerMessage = mActivity
//				.getString(R.string.posted_successfully);
		Intent resultIntent = new Intent(mActivity, MainActivity.class);
		// Because clicking the notification opens a new ("special") activity,
		// there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				mActivity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Sets an ID for the notification, so it can be updated
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
				mActivity)
				.setContentTitle(mActivity.getString(R.string.app_name))
				.setSmallIcon(R.drawable.plane)
				.setContentIntent(resultPendingIntent);

		// Because the ID remains unchanged, the existing notification is
		// updated.
		mNotifyMgr.notify(NOTI_ID, mNotifyBuilder.build());
		mNotifyMgr.cancel(NOTI_ID);
	}

	/**
	 * show notification
	 * 
	 * @param message
	 */
	private void showNotification(String message) {

		String tickerMessage = mActivity.getString(R.string.sending_post);

		Intent resultIntent = new Intent(mActivity, MainActivity.class);
		// Because clicking the notification opens a new ("special") activity,
		// there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				mActivity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mActivity).setSmallIcon(R.drawable.plane)
				.setContentTitle(mActivity.getString(R.string.sending_post))
				.setContentText(message).setTicker(tickerMessage)
				.setProgress(0, 0, true).setContentIntent(resultPendingIntent);

		// Builds the notification and issues it.
		mNotifyMgr.notify(NOTI_ID, mBuilder.build());

	}

	/**
	 * Add listener to add geo tag button
	 * 
	 * @param rootView
	 */
	private void addListenerAddGeoTag(View rootView) {
		mAddGeoTag = (ImageView) rootView.findViewById(R.id.add_geotag);
		mAddGeoTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Pop up dialog to ask if user wants to delete text, image or
				// both
				ClickGuard.work(v);
				if (!v.isSelected()) {
					Intent intent = new Intent(mActivity, PlacesActivity.class);
					startActivityForResult(intent,
							SMConstants.REQUEST_PLACES);
				} else {
					showGeoPopup();
				}

			}
		});
	}

	/**
	 * Show geo popup
	 */
	protected void showGeoPopup() {
		// TODO Auto-generated method stub
		final PopupDialog popup = new PopupDialog(mActivity,
				R.layout.popup_layout);
		popup.setTitle(R.string.geo_popup_title);
		popup.setFirstMenuText(R.string.change_location);
		popup.setSecondMenuText(R.string.delete_location);
		popup.setFirstMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(mActivity, PlacesActivity.class);
				startActivityForResult(intent, SMConstants.REQUEST_PLACES);

			}
		});

		popup.setSecondMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				mAddGeoTag.setSelected(false);

				mSelectedLocationLat = 0;
				mSelectedLocationLon = 0;
				mSelectedFoursquareVenuId = "";
				mSelectedFacebookPlaceId = "";
				mSelectedTwitterPlaceId = "";

			}
		});

		popup.show();
	}

	/**
	 * Pop up dialog to ask if user wants to delete text, image or both
	 */
	protected void showDeleteDialog() {

		final PopupDialog popup = new PopupDialog(mActivity,
				R.layout.popup_layout);
		popup.setTitle(R.string.delete_popup_title);
		popup.setFirstMenuText(R.string.delete_text);
		popup.setSecondMenuText(R.string.delete_photo);
		popup.setThirdMenuText(R.string.delete_text_photo);
		popup.setFirstMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				mEditText.setText("");
				mEditor.putString(SMConstants.KEY_DRAFTED_TEXT, "");
				mEditor.commit();
			}
		});

		popup.setSecondMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				// Set text length limit with image
				setMaxTextLength();
				mImgUri = "";
				mPhoto.setImageResource(R.drawable.camera);
				mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, "");
				mEditor.commit();

			}
		});
		popup.setThirdMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				// Set text length limit with image
				setMaxTextLength();
				mEditText.setText("");
				mImgUri = "";
				mPhoto.setImageResource(R.drawable.camera);
				mEditor.putString(SMConstants.KEY_DRAFTED_TEXT, "");
				mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, "");
				mEditor.commit();

			}
		});
		popup.show();
	}

	/**
	 * Restore text and image on sharedpreference on onResume
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Show guide image if this is first run
		GuideDisplayer.show(mActivity, mRootView, mSharedPreference,
				SMConstants.FIRST_RUN_WRITE_FRAGMENT, mSharedPreference
						.getBoolean(SMConstants.FIRST_RUN_WRITE_FRAGMENT,
								true), R.drawable.guide_write_fragment);

		// Send to Google Analytics
		mTracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
		mTracker.send(MapBuilder.createAppView().build());

	}

	// Restore draft text and image
	private void restoreDraft() {
		String draftedText = mSharedPreference.getString(
				SMConstants.KEY_DRAFTED_TEXT, "");
		String draftedImage = mSharedPreference.getString(
				SMConstants.KEY_DRAFTED_IMAGE, "");

		mEditText.setText(draftedText);
		mImgUri = draftedImage;
		showImage(draftedImage);


		mSelectedLocationLat = Double.parseDouble(mSharedPreference.getString(SMConstants.KEY_DRAFTED_LAT, "0"));
		mSelectedLocationLat = Double.parseDouble(mSharedPreference.getString(SMConstants.KEY_DRAFTED_LON, "0"));
		mSelectedFoursquareVenuId = mSharedPreference.getString(SMConstants.KEY_DRAFTED_FOURSQUARE_VENUE_ID, "");
		mSelectedFacebookPlaceId = mSharedPreference.getString(SMConstants.KEY_DRAFTED_FACEBOOK_VENUE_ID, "");
		mSelectedTwitterPlaceId = mSharedPreference.getString(SMConstants.KEY_DRAFTED_TWITTER_VENUE_ID, "");

        setLocationIconSelected();



	}

	private void setLocationIconSelected() {
		mAddGeoTag = (ImageView) mActivity.findViewById(R.id.add_geotag);

		if ((mSelectedLocationLat != 0 && mSelectedLocationLon != 0)
				|| mSelectedFoursquareVenuId != null
				|| mSelectedFacebookPlaceId != null
				|| mSelectedTwitterPlaceId != null) {
			mAddGeoTag.setSelected(true);
		} else {
			mAddGeoTag.setSelected(false);
		}
	}

	/**
	 * Save text and image on sharedpreference on onStop
	 */
	@Override
	public void onStop() {



		super.onStop();
		saveDraft();
	}

	private void saveDraft() {
		String draftText = "";
		String draftImagePath = "";

		draftText = mEditText.getText().toString();

		draftImagePath = mImgUri;

		// Save text and image on sharedpreference
		mEditor.putString(SMConstants.KEY_DRAFTED_TEXT, draftText);
		mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, draftImagePath);
		mEditor.putString(SMConstants.KEY_DRAFTED_LAT, String.valueOf(mSelectedLocationLat));
		mEditor.putString(SMConstants.KEY_DRAFTED_LON, String.valueOf(mSelectedLocationLon));
		mEditor.putString(SMConstants.KEY_DRAFTED_FOURSQUARE_VENUE_ID, mSelectedFoursquareVenuId);
		mEditor.putString(SMConstants.KEY_DRAFTED_FACEBOOK_VENUE_ID, mSelectedFacebookPlaceId);
		mEditor.putString(SMConstants.KEY_DRAFTED_TWITTER_VENUE_ID, mSelectedTwitterPlaceId);


		mEditor.commit();
	}

	/**
	 * Initialize EditText
	 * 
	 * @param rootView
	 */
	private void initEditText(View rootView) {
		mEditText = (EditText) rootView.findViewById(R.id.post_edittext);

		mTextLength = (TextView) rootView.findViewById(R.id.text_length);

		// If post to twitter button is selected, text length is 140. Otherwise
		// it is 5000.
		mPostTwitter = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_TWITTER, false);
		MAX_TEXT_LIMIT = mPostTwitter ? 140 : 5000;

		TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				int textLengthValue = MAX_TEXT_LIMIT - s.length();
				mTextLength.setText(String.valueOf(textLengthValue));
				mTextLength.setTextColor((textLengthValue >= 0) ? Color.DKGRAY
						: Color.RED);
			}

		};

		mEditText.addTextChangedListener(watcher);

	}

	/**
	 * Post message and/or image to sns
	 */
	private void post() {
		// If no internet connection, stop posting.
		if (!InternectConnectionDetector.hasConnection(mActivity))
			return;

//		// Check if at least ons sns is logged in
//		if (!checkSNSLogin())
//			return;

		// Check if at least one sns button is selected
		if (!checkSNSButtonClicked())
			return;

		// Check if message and image url is not null
		if (!checkMessageImageReady())
			return;

		// Check if photo is ready
		if (!checkPhotoReady())
			return;

		// get message from EditText
		String message = mEditText.getText().toString();

		// Check with previous message
//		if (checkPreviousMessage(message))
//			return;

		String resizedImagePath = null;
		// Resize image if necessary
		if (mImgUri != null && !mImgUri.equals("")) {

			resizedImagePath = mResizer.work(mImgUri);
		}

		if (mPostGooglePlus) {
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.GOOGLE_PLUS, "");

		}

		if (mPostTwitter) {
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.TWITTER, mSelectedTwitterPlaceId);

		}

		if (mPostFacebook) {
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.FACEBOOK, mSelectedFacebookPlaceId);

		}

		if (mPostFoursquare) {
			// Check if venue is selected

			if (mSelectedFoursquareVenuId == null
					|| mSelectedFoursquareVenuId.equals("")) {
				showFoursquareVenuErrorDialog();

				return;

			}
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.FOURSQUARE, mSelectedFoursquareVenuId);

		}

		if (mPostAppnet) {
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.APPNET, "");

		}

		if (mPostLinkedin) {
			addPostRequestQueue(message, resizedImagePath,
					SMConstants.LINKEDIN, "");

		}

		runHttpRequest();

		// Save message to shared preference
		saveMessageSharedPreference(message);

		showNotification(message);

	}

	/**
	 * Show error dialog when Foursquare venue id is null
	 */
	private void showFoursquareVenuErrorDialog() {
		String errorMessage = null;
		errorMessage = getString(R.string.add_venue_foursquare);
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
			}
		});
		alert.setMessage(errorMessage);
		alert.show();
		mRequestQueue.clear();

	}

	/**
	 * Add request to queue
	 * 
	 * @param message
	 * @param resizedImagePath
	 * @param snsType
	 * @param placeId
	 */
	private void addPostRequestQueue(String message, String resizedImagePath,
			int snsType, String placeId) {
		PostRequest request = new PostRequest();
		request.message = message;
		request.imgUri = resizedImagePath;
		request.snsName = snsType;
		request.venueId = placeId;
		request.lat = mSelectedLocationLat;
		request.lon = mSelectedLocationLon;
		mRequestQueue.add(request);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private boolean checkPreviousMessage(String message) {
		String previousMessage = mSharedPreference.getString(
				SMConstants.KEY_PREVIOUS_MESSAGE, "");

		if (message.equals(previousMessage)) {
			Toast.makeText(mActivity, R.string.same_with_previous_message,
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	/**
	 * Save message to shared preference
	 * 
	 * @param message
	 */
	private void saveMessageSharedPreference(String message) {
		mEditor.putString(SMConstants.KEY_PREVIOUS_MESSAGE, message);
		mEditor.commit();
	}

	/**
	 * Check if photo is ready
	 * 
	 * @return
	 */
	private boolean checkPhotoReady() {
		if (!mIsPhotoReady)
			Toast.makeText(mActivity, R.string.photo_not_ready,
					Toast.LENGTH_SHORT).show();
		return mIsPhotoReady;
	}

	/**
	 * Check if at least ons sns is logged in
	 * 
	 * @return
	 */
	private boolean checkSNSLogin() {
		boolean isNoSnsLogin = checkNoSnsLogin();
		if (isNoSnsLogin) {

			CustomToastManager manager = new CustomToastManager(mActivity);
			View line = (View) mActivity.findViewById(R.id.sns_seperator);
			manager.showPopup(line, R.string.login_sns,
					R.id.write_check_text_size);
			return false;
		}
		return true;
	}

	/**
	 * Check if at least one sns button is selected
	 */
	private boolean checkSNSButtonClicked() {
		mPostTwitter = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_TWITTER, false);
		mPostFacebook = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_FACEBOOK, false);
		mPostGooglePlus = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_GOOGLE_PLUS, false);

		mPostFoursquare = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_FOURSQUARE, false);

		mPostAppnet = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_APPNET, false);
		mPostLinkedin = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_LINKEDIN, false);

		if (!mPostTwitter && !mPostFacebook && !mPostGooglePlus
				&& !mPostFoursquare && !mPostAppnet && !mPostLinkedin) {

			return false;

		}
		return true;
	}

	/**
	 * Check if message and image url is not null
	 * 
	 * @return
	 */
	private boolean checkMessageImageReady() {

		String message = mEditText.getText().toString();

		if (message.equals("") && (mImgUri == null || mImgUri.equals(""))) {
			Toast.makeText(mActivity, R.string.put_in_message_select_img,
					Toast.LENGTH_SHORT).show();
			return false;

		}
		return true;
	}

	/**
	 * Show popup to get image
	 */
	protected void showPhotoPopup() {
		final PopupDialog popup = new PopupDialog(mActivity,
				R.layout.popup_layout);
		popup.setTitle(R.string.photo_popup_title);
		popup.setFirstMenuText(R.string.photo_from_gallery);
		popup.setSecondMenuText(R.string.photo_from_camera);
		popup.setFirstMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(mActivity, CameraAlbumActivity.class);
				intent.putExtra(SMConstants.KEY_IMAGE_SOURCE,
						SMConstants.SOURCE_GALLERY);
				startActivityForResult(intent, SMConstants.FROM_GALLERY);

			}
		});

		popup.setSecondMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(mActivity, CameraAlbumActivity.class);
				intent.putExtra(SMConstants.KEY_IMAGE_SOURCE,
						SMConstants.SOURCE_CAMERA);
				startActivityForResult(intent, SMConstants.FROM_CAMERA);

			}
		});

		if (mImgUri != null && !mImgUri.equals("")) {
			popup.setThirdMenuText(R.string.delete_photo);
			popup.setThirdMenuListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					popup.dismiss();
					// Set text length limit with image
					setMaxTextLength();
					mImgUri = "";
					mPhoto.setImageResource(R.drawable.camera);
					mEditor.putString(SMConstants.KEY_DRAFTED_IMAGE, "");
					mEditor.commit();
				}
			});

		}

		popup.show();

	}

	private void setMaxTextLengthWithImage() {

		if (mSharedPreference.getBoolean(SMConstants.KEY_POST_TWITTER,
				false)) {
			MAX_TEXT_LIMIT = 117;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_FOURSQUARE, false)) {
			MAX_TEXT_LIMIT = 200;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_APPNET, false)) {
			MAX_TEXT_LIMIT = 233;
		} else if (mSharedPreference.getBoolean(
				SMConstants.KEY_POST_LINKEDIN, false)) {
			MAX_TEXT_LIMIT = 677;
		} else {
			MAX_TEXT_LIMIT = 4977;
		}

		int textLength = mEditText.getText().toString().length();
		int lengh = MAX_TEXT_LIMIT - textLength;
		mTextLength.setText(String.valueOf(lengh));

	}

	/**
	 * Set text length limit with image
	 * 
	 */
	protected void setTextLengthWithImage(boolean isShowing) {
		mPostTwitter = mSharedPreference.getBoolean(
				SMConstants.KEY_POST_TWITTER, false);
		MAX_TEXT_LIMIT = (isShowing) ? (mPostTwitter) ? 118 : 4977
				: (mPostTwitter) ? 140 : 5000;
		int textLength = mEditText.getText().toString().length();
		int lengh = MAX_TEXT_LIMIT - textLength;
		mTextLength.setText(String.valueOf(lengh));

	}

	private Bitmap decodeFileForPhotoSize(int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mImgUri, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(mImgUri, options);

	}

	/**
	 * Save image to file
	 * 
	 * @author user
	 * 
	 */
	class ImageFileSaveTask extends AsyncTask<Uri, Void, String> {

		private ProgressBar bar;

		@Override
		protected String doInBackground(Uri... params) {
			mIsPhotoReady = false;
			PiccasaImageSaver saver = new PiccasaImageSaver(mActivity);
			return saver.doIt(params[0]);

		}

		protected void onPreExecute() {

			// Show progress bar
			bar = (ProgressBar) mActivity.findViewById(R.id.photo_progress_bar);
			bar.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(String imagePath) {
			mIsPhotoReady = true;
			// Hide progress bar
			bar.setVisibility(View.GONE);
			mImgUri = imagePath;
			showImage(imagePath);
		}

	}

	

}
