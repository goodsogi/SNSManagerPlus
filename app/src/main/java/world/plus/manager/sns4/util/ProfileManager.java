package world.plus.manager.sns4.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.manage_account.ManageAccountFragment;
import world.plus.manager.sns4.manage_account.OnLoginListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Manage sns user name and profile image
 * 
 * @author user
 * 
 */
public class ProfileManager {

	private SharedPreferences mSharedPreference;
	private Twitter mTwitter;
	private Editor mEditor;
	private Activity mActivity;
	private Fragment mFragment;
	// Catch error that user is null at line 182
	OnLoginErrorListener mOnLoginErrorListener;
	OnLoginListener mOnLoginListener;

	public ProfileManager(Activity activity, Fragment fragment) {
		mActivity = activity;
		mFragment = fragment;
		mSharedPreference = activity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreference.edit();

		if (mFragment instanceof ManageAccountFragment) {
			try {
				mOnLoginErrorListener = (OnLoginErrorListener) mFragment;
			} catch (ClassCastException e) {
				throw new ClassCastException(mFragment.toString()
						+ " must implement mOnLoginErrorListener");
			}
			
			try {
				mOnLoginListener = (OnLoginListener) mFragment;
			} catch (ClassCastException e) {
				throw new ClassCastException(mFragment.toString()
						+ " must implement mOnLoginListener");
			}
		}

	}

	/**
	 * If logged in Facebook, get profile image and user name
	 */
	public void getFacebookProfileData() {
		// Get user id
		getFacebookUserId();

	}

	/**
	 * Get Facebook user id
	 */
	private void getFacebookUserId() {
		final Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// If the session is open, make an API call to get user data
			// and define a new callback to handle the response
			Request request = Request.newMeRequest(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							// If the response is successful
							if (session == Session.getActiveSession()) {
								if (user != null) {
									String facebookUserId = user.getId();// user
																			// id
									String facebookUserName = user.getName();// user's
									// name
									// Save Facebook user name on
									// sharedpreference
									mEditor.putString(
											SMConstants.KEY_FACEBOOK_USER_NAME,
											facebookUserName);
									mEditor.commit();
									// Get Facebook profile image
									getFacebookProfile(facebookUserId);
								}
							}
						}
					});
			Request.executeBatchAsync(request);
		}
	}

	/**
	 * Get Facebook profile image
	 * 
	 */
	protected void getFacebookProfile(String mFacebookUserId) {
		String profileUrl = "http://graph.facebook.com/" + mFacebookUserId
				+ "/picture?style=small";
		// Download profile image
		new ProfileImageDownloader(mActivity, mFragment).execute(profileUrl,
				SMConstants.FACEBOOK);

	}


	private class GetTwitterNameTask extends
			AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			mTwitter = getTwitterClient();
			// Get user name
			return getTwitterUserName();
		}

		@Override
		protected void onPostExecute(String userName) {
			super.onPostExecute(userName);
			if (userName == null)
				return;
			saveTwitterNamePreference(userName);
		}

	}

	private class GetTwitterProfileImageTask extends
			AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Get user profile image url
			return getTwitterProfileUrl();


		}

		@Override
		protected void onPostExecute(String profileUrl) {
			super.onPostExecute(profileUrl);
			if (profileUrl == null)
				return;
			// Download profile image
			new ProfileImageDownloader(mActivity, mFragment).execute(profileUrl,
					SMConstants.TWITTER);
		}

	}



	protected void saveTwitterNamePreference(String userName) {
		// Save user name on sharedpreference
		mEditor.putString(SMConstants.KEY_TWITTER_USER_NAME, userName);
		mEditor.commit();

	}

	/**
	 * If logged in Twitter, get profile image and user name
	 */
	public void getTwitterProfileData() {


		new GetTwitterNameTask().execute();


        new GetTwitterProfileImageTask().execute();





	}

	/**
	 * If logged in Instagram, get profile image
	 */
	public void getInstagramProfileImage() {

		// Get user profile image url
		String profileUrl = getInstagramProfileUrl();
		if (profileUrl == null) {
			mOnLoginErrorListener.onError(SMConstants.FOURSQUARE);
			return;
		}

		// Download profile image
		new ProfileImageDownloader(mActivity, mFragment).execute(profileUrl,
				SMConstants.FOURSQUARE);

	}

	private String getInstagramProfileUrl() {
		String userId = mSharedPreference.getString(
				SMConstants.KEY_INSTAGRAM_USER_ID, "");
		String accessToken = mSharedPreference.getString(
				SMConstants.KEY_INSTAGRAM_ACCESS_TOKEN, "");

		String urlString = SMConstants.INSTAGRAM_API_URL + "/users/"
				+ userId + "?access_token=" + accessToken;

		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream inputStream = null;
		String response = null;
		try {
			inputStream = url.openConnection().getInputStream();
			response = streamToString(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String imageUrlString = null;
		try {
			JSONObject jsonObject = (JSONObject) new JSONTokener(response)
					.nextValue();
			JSONObject profileImageJsonObject = jsonObject
					.getJSONObject("data");
			imageUrlString = profileImageJsonObject
					.getString("profile_picture");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return imageUrlString;
	}

	private String streamToString(InputStream is) throws IOException {
		String string = "";

		if (is != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			string = stringBuilder.toString();
		}

		return string;
	}




	/**
	 * Get Twitter user name
	 *
	 * @return
	 */
	public String getTwitterUserName() {

		try {
			return mTwitter.getScreenName();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get Twitter client with access token
	 * 
	 * @return
	 */
	private Twitter getTwitterClient() {
		// Set access token to twitter
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(SMConstants.TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(SMConstants.TWITTER_CONSUMER_SECRET);
		// Access Token
		String access_token = mSharedPreference.getString(
				SMConstants.KEY_TWITTER_ACCESS_TOKEN, "");
		// Access Token Secret
		String access_token_secret = mSharedPreference.getString(
				SMConstants.KEY_TWITTER_ACCESS_TOKEN_SECRET, "");

		AccessToken accessToken = new AccessToken(access_token,
				access_token_secret);
		return new TwitterFactory(builder.build()).getInstance(accessToken);
	}

	/**
	 * Get Twitter profile image url
	 * 
	 * @return
	 */
	public String getTwitterProfileUrl() {

		User user = null;
		try {
			user = mTwitter.showUser(mTwitter.getScreenName());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		if (user == null)
			return "";
		return user.getBiggerProfileImageURL();
	}

	/**
	 * Save Google Plus user name
	 * 
	 * @param personName
	 */
	public void saveGooglePlusUserName(String personName) {
		mEditor.putString(SMConstants.KEY_GOOGLE_PLUS_USER_NAME, personName);
		mEditor.commit();
	}

	/**
	 * Save Google Plus profile image on inner storage
	 * 
	 */
	public void saveGooglePlusProfile(String profileUrl) {
		mOnLoginListener.onLogin();
		new ProfileImageDownloader(mActivity, mFragment).execute(profileUrl,
				SMConstants.GOOGLE_PLUS);
	}

	/**
	 * Get saved user name
	 * 
	 * @return
	 */
	public String getSavedUserName(int snsName) {
		String key = null;
		switch (snsName) {
		case SMConstants.FACEBOOK:
			key = SMConstants.KEY_FACEBOOK_USER_NAME;
			break;
		case SMConstants.GOOGLE_PLUS:
			key = SMConstants.KEY_GOOGLE_PLUS_USER_NAME;
			break;
		case SMConstants.TWITTER:
			key = SMConstants.KEY_TWITTER_USER_NAME;
			break;

		case SMConstants.FOURSQUARE:
			key = SMConstants.KEY_FOURSQUARE_USER_NAME;
			break;
		case SMConstants.APPNET:
			key = SMConstants.KEY_APPNET_USER_NAME;

			break;

		case SMConstants.LINKEDIN:
			key = SMConstants.KEY_LINKEDIN_USER_NAME;
			break;
		}
		return mSharedPreference.getString(key, "");
	}

	/**
	 * Get saved profile image bitmap
	 * 
	 * @return
	 */
	public Bitmap getSavedProfileBitmap(int snsName) {

		String fileName = SMConstants.getSnsName(snsName)
				+ SMConstants.IMAGE_FILE_NAME;

		String imageFileName = mActivity.getFilesDir() + File.separator
				+ SMConstants.IMAGE_FOLDER + File.separator + fileName;

		Bitmap bitmap = BitmapFactory.decodeFile(imageFileName);
		return bitmap;
	}

}
