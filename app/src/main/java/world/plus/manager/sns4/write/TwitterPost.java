package world.plus.manager.sns4.write;

import java.io.File;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.ErrorLogger;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public class TwitterPost extends PlusPost {

	// Handle callback regarding post
	OnPostCallbackListener mCallback;
	public boolean mIsPostHandled;
	final Handler mHandler = new Handler();
	private Toast mToast;
	private ErrorLogger mErrorLogger;
	public int mErrorCount;
	public boolean mPostCancelled;
	protected boolean mNotReceivedResponse;

	public TwitterPost(Activity activity, Fragment fragment) {
		super(activity, fragment);

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

		mToast = Toast.makeText(mActivity, "", Toast.LENGTH_SHORT);
		mErrorLogger = new ErrorLogger();
		mNotReceivedResponse = true;
	}

	/**
	 * Post
	 * 
	 * @param text
	 * @param imgUrl
	 * @param placeId
	 * @param lon
	 * @param lat
	 */
	public void post(String text, String imgUrl, String placeId, double lat,
			double lon) {

		new UpdateTwitterStatus().execute(text, imgUrl, placeId, lat, lon);

//		// Stop processing after 30 seconds
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//
//				if (mNotReceivedResponse) {
//					mCallback.onError(SMConstants.TWITTER);
//					mErrorLogger.write("too late to get response to twitter");
//
//				}
//			}
//		}, SMConstants.POST_DELAY_TIME);

	}

	/**
	 * Save if send post to Twitter
	 * 
	 * @param value
	 */
	public void savePostPreference(boolean value) {
		mEditor.putBoolean(SMConstants.KEY_POST_TWITTER, value);
		mEditor.commit();
	}

	/**
	 * Function to update status
	 * */
	class UpdateTwitterStatus extends
			AsyncTask<Object, String, twitter4j.Status> {

		/**
		 * getting Places JSON
		 * */
		protected twitter4j.Status doInBackground(Object... args) {

			// Check text length and if it is over 140, cut it

			final String imgUrl = (String) args[1];
			final String text = checkLength((String) args[0], imgUrl);
			final String placeId = (String) args[2];
			final double lat = (Double) args[3];
			final double lon = (Double) args[4];
			Log.d("facebook", "twitter text length: " + text.length());

			twitter4j.Status response = null;
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(SMConstants.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(SMConstants.TWITTER_CONSUMER_SECRET);
				builder.setHttpRetryCount(2);
				builder.setHttpRetryIntervalSeconds(2);
				builder.setHttpConnectionTimeout(0);
				builder.setHttpReadTimeout(0);
				builder.setHttpStreamingReadTimeout(0);

				// Access Token
				String access_token = mSharedPreferences.getString(
						SMConstants.KEY_TWITTER_ACCESS_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(
						SMConstants.KEY_TWITTER_ACCESS_TOKEN_SECRET, "");

				AccessToken accessToken = new AccessToken(access_token,
						access_token_secret);

				Twitter twitter = new TwitterFactory(builder.build())
						.getInstance(accessToken);
				StatusUpdate statusUpdate = new StatusUpdate(text);

				if (placeId != null && !placeId.equals("")) {

					statusUpdate.setDisplayCoordinates(true);
					statusUpdate.setPlaceId(placeId);

				}

				if (lat != 0 && lon != 0) {
					statusUpdate.setDisplayCoordinates(true);
					GeoLocation location = new GeoLocation(lat, lon);
					statusUpdate.setLocation(location);
				}

				if (imgUrl != null && !imgUrl.equals("")) {
					// Resize image

					// statusUpdate.setMedia("image name",
					// mResizer.makeInputStream(imgUrl));

					File imgFile = new File(imgUrl);
					statusUpdate.setMedia(imgFile);
				}

				// !!!Check response and deal with it
				response = twitter.updateStatus(statusUpdate);

				return response;

			} catch (TwitterException e) {
				mErrorLogger.write(e.getMessage());

				//mCallback.onError(SMConstants.TWITTER);

			}
			return null;
		}

		/**
		 * Check text length and if it is over 140, cut it
		 * 
		 * @param string
		 * @param imgUrl
		 * @return
		 */
		private String checkLength(String string, String imgUrl) {

			if (imgUrl != null && !imgUrl.equals("")) {

				if (string.length() < 118) {
					return string;
				} else {
					// Show toast to warn over 140
					mToast.setText(R.string.length_over_118);
					mToast.show();

					return string.substring(0, 114) + "...";

				}

			} else {

				if (string.length() < 140) {
					return string;
				} else {
					// Show toast to warn over 140
					mToast.setText(R.string.length_over_140);
					mToast.show();

					return string.substring(0, 136) + "...";

				}
			}

		}

		protected void onPostExecute(twitter4j.Status response) {
			// mIsPostHandled = true;
			// mHandler.removeCallbacksAndMessages(null);
			mNotReceivedResponse = false;
			if (response == null) {

				mCallback.onError(SMConstants.TWITTER);
			} else {
				// Write error log to file
				// mErrorLogger.write("twitter success");
				mCallback.onCompleted(SMConstants.TWITTER);
			}
		}

	}

}
