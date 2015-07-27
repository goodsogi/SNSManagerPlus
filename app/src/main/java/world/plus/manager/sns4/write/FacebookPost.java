package world.plus.manager.sns4.write;

import java.io.File;
import java.io.FileNotFoundException;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.manage_account.FacebookOAuthHelper;
import world.plus.manager.sns4.manage_account.OnGetPublishPermissionListener;
import world.plus.manager.sns4.util.ErrorLogger;
import world.plus.manager.sns4.util.FacebookExtraRequest;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookPost extends PlusPost implements
		OnGetPublishPermissionListener {

	private PendingAction mPendingAction = PendingAction.NONE;
	private String mText;
	private String mImgUrl;
	// Handle callback regarding post
	OnPostCallbackListener mCallback;
	private FacebookOAuthHelper mOAuthHelper;
	protected boolean mIsPostHandled;
	final Handler mHandler = new Handler();
	private Session mSession;
	private ErrorLogger mErrorLogger;
	private int mErrorCount;
	protected boolean mPostCancelled;
	private String mAlbumId;
	private String mPlaceId;

	private static FacebookPost instance;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	public static FacebookPost getInstance() {
		return instance;
	}

	public FacebookPost(Activity activity, Fragment fragment) {
		super(activity, fragment);
		// test
		instance = this;
		mIsPostHandled = false;

		// This makes sure that the fragment has implemented
		// the callback interface. If not, it throws an exception
		if (mFragment instanceof WriteFragment) {
			try {
				mCallback = (OnPostCallbackListener) mFragment;
			} catch (ClassCastException e) {
				throw new ClassCastException(mFragment.toString()
						+ " must implement OnPostCallback");
			}
		}

		mOAuthHelper = new FacebookOAuthHelper(mActivity, mFragment);
		mErrorLogger = new ErrorLogger();

		// Get timeline album id from sharedpreference
		mAlbumId = mSharedPreferences.getString(
				SMConstants.KEY_TIMELINE_ALBUM_ID, "");

	}

	/**
	 * post
	 * 
	 * @param text
	 * @param imgUrl
	 */
	public void post(String text, String imgUrl, String placeId, double lat,
			double lon) {

		this.mText = text;
		this.mImgUrl = imgUrl;
		this.mPlaceId = placeId;

		if (imgUrl == null || imgUrl.equals("")) {
			mPendingAction = PendingAction.POST_STATUS_UPDATE;
			performPublish();
		} else {
			mPendingAction = PendingAction.POST_PHOTO;
			performPublish();
		}
	}

	/**
	 * Check if user has publish permission
	 * 
	 * @return
	 */
	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	/**
	 * Handle pending action
	 */
	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = mPendingAction;

		mPendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_PHOTO:
			Log.d("facebook", "photo post image: " + mImgUrl);
			sendPostTextWithImage();

			break;
		case POST_STATUS_UPDATE:
			Log.d("facebook", "photo text image: " + mImgUrl);
			sendPostOnlyText();
			break;
		}

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!mIsPostHandled) {
					mPostCancelled = true;
					// Write error log to file
					mErrorLogger.write("too late to get response to facebook");
					mCallback.onError(SMConstants.FACEBOOK);
				}

			}
		}, SMConstants.POST_DELAY_TIME);
		//

	}

	/**
	 * Perform publishing
	 * 
	 * @param action
	 */
	private void performPublish() {
		mSession = Session.getActiveSession();
		if (mSession != null && mSession.isOpened()) {

			if (hasPublishPermission()) {
				handlePendingAction();
			} else {
				mOAuthHelper.requestNewPublishPermissions(mSession);

			}
			// Handle when session is null
		} else if (mSession == null
				|| mSession.getState()
						.equals(SessionState.CREATED_TOKEN_LOADED)) {
			mOAuthHelper.openForRead();

		}

	}

	/**
	 * Save if send post to Facebook
	 * 
	 * @param value
	 */
	public void savePostPreference(boolean value) {
		mEditor.putBoolean(SMConstants.KEY_POST_FACEBOOK, value);
		mEditor.commit();
	}

	/**
	 * I created this method to send photo and image to wall
	 * 
	 * @param session
	 * @param file
	 * @param message
	 * @param callback
	 * @return
	 * @throws FileNotFoundException
	 */

	/**
	 * Send only text to wall
	 */
	private void sendPostOnlyText() {

		Request request = FacebookExtraRequest.newStatusUpdateRequest(
				Session.getActiveSession(), mText, mPlaceId,
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						mIsPostHandled = true;
						if (response.getError() == null) {
							// Write error log to file
							// mErrorLogger.write("facebook success");
							mCallback.onCompleted(SMConstants.FACEBOOK);
						} else {
							// Write error log to file
							mErrorLogger.write(response.getError().toString());
							mCallback.onError(SMConstants.FACEBOOK);

						}
					}
				});

		request.executeAsync();

	}

	/**
	 * Send text and image to wall
	 */
	private void sendPostTextWithImage() {

		File file = new File(mImgUrl);

		Request req = null;
		try {
			req = FacebookExtraRequest.newUploadPhotoRequest(mSession, file,
					mText, mAlbumId, mPlaceId, new Request.Callback() {

						@Override
						public void onCompleted(Response response) {
							mIsPostHandled = true;
							mHandler.removeCallbacksAndMessages(null);
							if (response.getError() == null) {
								// Write error log to file
								// mErrorLogger.write("facebook success");
								mCallback.onCompleted(SMConstants.FACEBOOK);
							} else {
								// Write error log to file
								mErrorLogger.write(response.getError()
										.toString());
								mCallback.onError(SMConstants.FACEBOOK);

							}

						}
					});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		req.executeAsync();

	}

	/**
	 * Handle error
	 * 
	 * @param response
	 */
	protected void handleError(Response response) {
		// Retry two times
		if (mErrorCount++ > 1 && mPostCancelled) {
			mCallback.onError(SMConstants.FACEBOOK);
			return;
		}

		Toast.makeText(mActivity, "retry", Toast.LENGTH_SHORT).show();

		mErrorLogger.write(response.getError().toString());

		// Retry after 10 seconds
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				sendPostOnlyText();
			}
		}, 10000);
	}

	/**
	 * Called when get publish permission
	 */
	@Override
	public void onGet() {
		if (mPendingAction != PendingAction.NONE)
			post(mText, mImgUrl, mPlaceId, 0, 0);
	}

}
