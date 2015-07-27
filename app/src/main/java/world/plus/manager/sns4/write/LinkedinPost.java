package world.plus.manager.sns4.write;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.HttpUtils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
/**
 * 사진 전송을 하려면 피카사, 플리커 등에 업로드하고 url을 가져와야 함. 
 * 
 * 장소는 등록 안됨 
 * @author jeff
 *
 */
public class LinkedinPost  extends PlusPost {

	private Activity mActivity;
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
	private Editor mEditor;
	private Fragment mFragment;
	// Handle callback regarding post
	OnPostCallbackListener mCallback;

	public boolean mPostCancelled;
	protected boolean mNotReceivedResponse;
	// Test
	protected static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String BOUNDARY = "bWljaGFlbCBqYWNrc29uIGlzIHN0aWxsIGFsaXZl";
	protected static final String CONTENT_TYPE = "multipart/form-data; boundary="
			+ BOUNDARY;
	protected static final String CONTENT_DISPOSITION_HEADER_WITH_FILENAME = "Content-Disposition: form-data; name=\"%1$s\"; filename=\"%2$s\"\r\n";
	private static final String DELIMITER = "--";
	protected static final String CONTENT_TYPE_HEADER = "Content-Type: %1$s\r\n";
	protected static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition: form-data; name=\"%1$s\"\r\n";

	private double mLat;
	private double mLon;
	private String mAccessToken;
	private String mMessage;

	public LinkedinPost(Activity activity, Fragment fragment) {
		super(activity, fragment);
		mActivity = activity;
		mFragment = fragment;
		// Shared Preferences
		mSharedPreferences = mActivity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
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

		mNotReceivedResponse = true;

		
	}

	/**
	 * Post
	 * 
	 * @param text
	 * @param imgUrl
	 * @param venueId
	 */
	public void post(String text, String imgUrl, String venueId, double lat,
			double lon) {
		
		mAccessToken = mSharedPreferences.getString(
				SMConstants.LINKEDIN_ACCESS_TOKEN, "");

		// mLat = lat;
		// mLon = lon;
		mMessage = text;

		new PostSharesTask().execute();

		// if (imgUrl != null && !imgUrl.equals("")) {
		//
		// new CreateFileTask().execute(imgUrl);
		//
		// } else {
		// new CreatePostTask().execute();
		// }

		// Stop processing after 30 seconds
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (mNotReceivedResponse) {
					mCallback.onError(SMConstants.LINKEDIN);

				}
			}
		}, SMConstants.POST_DELAY_TIME);

	}

	/**
	 * Save if send post to App.net
	 * 
	 * @param value
	 */
	public void savePostPreference(boolean value) {
		mEditor.putBoolean(SMConstants.KEY_POST_LINKEDIN, value);
		mEditor.commit();
	}

	/**
	 * Function to create post
	 * */
	class PostSharesTask extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {

			// Set up request
			HttpsURLConnection httpsUrlConnection = null;
			URL url = null;
			try {
				url = new URL(SMConstants.LINKEDIN_API_URL_POST_SHARE
						+ mAccessToken);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {

				httpsUrlConnection = (HttpsURLConnection) url.openConnection();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				httpsUrlConnection.setReadTimeout(10000);
				httpsUrlConnection.setConnectTimeout(15000);
				httpsUrlConnection.setRequestMethod("POST");
				httpsUrlConnection.setUseCaches(false);
				httpsUrlConnection.setDoInput(true);
				httpsUrlConnection.setDoOutput(true);
				httpsUrlConnection.setRequestProperty("Content-Type",
						"application/json");
				httpsUrlConnection.setRequestProperty("x-li-format", "json");

				// make the json payload using json-simple

				final byte[] bodyBytes = getPostParamsJson().getBytes("UTF-8");
				httpsUrlConnection
						.setFixedLengthStreamingMode(bodyBytes.length);

				final OutputStream outputStream = httpsUrlConnection
						.getOutputStream();
				outputStream.write(bodyBytes);
				outputStream.close();

				httpsUrlConnection.connect();

				if (httpsUrlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

					return HttpUtils.streamToString(httpsUrlConnection,
							httpsUrlConnection.getInputStream());
				}

			} catch (ClientProtocolException e) {
			} catch (IOException e) {

				Log.i("linkedin", e.getMessage());
			}

			return null;

		}

		/**
		 * Create annotaions json
		 * 
		 * @param message
		 * @return
		 */
		private String getPostParamsJson() {

			JSONObject jsonMain = new JSONObject();

			JSONObject contentObject = new JSONObject();
			try {

				jsonMain.put("comment", mMessage);

				// contentObject.put("title", "A title for your share");
				// contentObject.put("submitted-url",
				// "http://www.linkedin.com");
				// contentObject.put("submitted-image-url",
				// "http://www.flickr.com/photos/121295997@N06/13380363064/");
				//
				// jsonMain.put("content", contentObject);

				JSONObject visibilityObject = new JSONObject();
				visibilityObject.put("code", "anyone");

				jsonMain.put("visibility", visibilityObject);

				return jsonMain.toString();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String response) {
			mNotReceivedResponse = false;
			if (response == null) {

				mCallback.onError(SMConstants.LINKEDIN);
			} else {
				// Write error log to file

				mCallback.onCompleted(SMConstants.LINKEDIN);

			}
		}

	}

}
