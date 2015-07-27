package world.plus.manager.sns4.manage_account;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.FacebookExtraRequest;
import world.plus.manager.sns4.util.LoginListener;
import world.plus.manager.sns4.write.FacebookPost;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FacebookOAuthHelper extends PlusOAuth {
	private final LoginListener mLoginListener;
	public static final List<String> PERMISSIONS = Arrays
			.asList("user_photos, publish_actions");

	// Facebook OAuth callback
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Session.StatusCallback statusCallbackPost = new SessionStatusCallbackPost();


	public FacebookOAuthHelper(Activity activity, Fragment fragment) {
		super(activity, fragment);
		mLoginListener = (LoginListener) fragment;

		

		// This makes sure that the fragment has implemented
		// the callback interface. If not, it throws an exception
		
		// Instantiate progress dialog that will show during log in

	}

	/**
	 * Log in Facebook
	 */
	public void doLogin() {
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {

			session = new com.facebook.Session(mActivity);

			com.facebook.Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new com.facebook.Session.OpenRequest(
						mActivity).setCallback(statusCallback));
				return;
			}
		}

		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new com.facebook.Session.OpenRequest(mActivity)
					.setCallback(statusCallback));
		} else {
			com.facebook.Session.openActiveSession(mActivity, true,
					statusCallback);
		}

	}

	/**
	 * Log out Facebook
	 */
	public void doLogout() {
		Session session = Session.getActiveSession();
		if (session != null && !session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	/**
	 * Save Google Plus login to sharedpreference
	 * 
	 * @param b
	 * 
	 */
	public void saveLogInStatePreference(boolean b) {

		mEditor.putBoolean(SMConstants.KEY_FACEBOOK_LOGIN, b);

		mEditor.commit();

	}

	/**
	 * Check user already logged in using Facebook Login flag is fetched from
	 * Shared Preferences
	 * */
	public boolean isLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreference.getBoolean(SMConstants.KEY_FACEBOOK_LOGIN,
				false);
	}

	/**
	 * Called when Facebook session is changed
	 * 
	 * @author user
	 * 
	 */
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.d("facebook", " called at FacebookOauthHelper");
			onSessionStateChange(session, state, exception);
		}
	}

	/**
	 * Called when Facebook session state is changed
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			// Get timeline album id
			getTimelineAlbumId();
			// Save login in sharedpreference
			saveLogInStatePreference(true);

			mLoginListener.onLoginSuccess(SMConstants.FACEBOOK);



		} else if (state.equals(SessionState.OPENED)) {

			requestNewPublishPermissions(session);


		} else if (state.isClosed()) {

		}
	}

	/**
	 * Called when Facebook session is changed
	 * 
	 * @author user
	 * 
	 */
	private class SessionStatusCallbackPost implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.equals(SessionState.OPENED)) {
				if (FacebookPost.getInstance() != null) {
					FacebookPost.getInstance().onGet();
				}
			}
		}
	}

	/**
	 * Save timeline album id to sharedpreference
	 * 
	 * @param albumId
	 */
	private void saveAlbumIdPreference(String albumId) {
		mEditor.putString(SMConstants.KEY_TIMELINE_ALBUM_ID, albumId);

		mEditor.commit();

	}

	private void getTimelineAlbumId() {

		Request request = FacebookExtraRequest.newAlbumIdRequest(
				Session.getActiveSession(), new Request.Callback() {
					@Override
					public void onCompleted(Response response) {

						JSONArray albums = null;
						try {
							albums = response.getGraphObject()
									.getInnerJSONObject().getJSONArray("data");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < albums.length(); i++) {
							JSONObject album = null;
							try {
								album = albums.getJSONObject(i);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							try {
								if (album.getString("type").equalsIgnoreCase(
										"wall")) {
									String albumId = album.getString("id");
									saveAlbumIdPreference(albumId);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}

				});

		request.executeAsync();

	}

	public void requestNewPublishPermissions(Session session) {
		session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
				mActivity, PERMISSIONS));
	}

	public void openForRead() {
		Session session = new Session(mActivity);
		Session.setActiveSession(session);
		if (session == null
				|| session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setCallback(statusCallbackPost));
		}
	}

}
