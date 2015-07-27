package world.plus.manager.sns4.write;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

public class AppnetPost extends PlusPost {

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

	public AppnetPost(Activity activity, Fragment fragment) {
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
				SMConstants.APPNET_ACCESS_TOKEN, "");

		mLat = lat;
		mLon = lon;
		mMessage = text;

		if (imgUrl != null && !imgUrl.equals("")) {

			new CreateFileTask().execute(imgUrl);

		} else {
			new CreatePostTask().execute();
		}

		// Stop processing after 30 seconds
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (mNotReceivedResponse) {
					mCallback.onError(SMConstants.APPNET);

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
		mEditor.putBoolean(SMConstants.KEY_POST_APPNET, value);
		mEditor.commit();
	}

	/**
	 * Function to create post
	 * */
	class CreatePostTask extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {

			// Set up request
			HttpsURLConnection httpsUrlConnection = null;
			String annotationsCount = (mLat != 0 && mLon != 0) ? "1" : "0";
			URL url = null;
			try {
				url = new URL(SMConstants.APPNET_API_URL
						+ "/stream/0/posts?include_post_annotations="
						+ annotationsCount + "&access_token=" + mAccessToken);

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

				// 테스트 후 삭제!!
				String testJson = "{\"type\": \"net.app.core.geolocation\","
						+ "\"value\": {" + "\"latitude\": 74.0064,"
						+ "\"longitude\": 40.7142}}";
				final byte[] bodyBytes = testJson.getBytes("UTF-8");

				// final byte[] bodyBytes =
				// getAnnotationsJson(mMessage).getBytes(
				// "UTF-8");
				httpsUrlConnection
						.setFixedLengthStreamingMode(bodyBytes.length);

				final OutputStream outputStream = httpsUrlConnection
						.getOutputStream();
				outputStream.write(bodyBytes);
				outputStream.close();

				httpsUrlConnection.connect();

				if (httpsUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

					return HttpUtils.streamToString(httpsUrlConnection,
							httpsUrlConnection.getInputStream());
				}

			} catch (ClientProtocolException e) {
			} catch (IOException e) {

				Log.i("appnet", e.getMessage());
			}

			return null;

		}

		/**
		 * Create annotaions json
		 * 
		 * @param message
		 * @return
		 */
		private String getAnnotationsJson(String message) {

			try {
				JSONObject mainjson = new JSONObject();
				mainjson.put("text", message);

				if (mLat != 0 && mLon != 0) {

					JSONArray annotationArray = new JSONArray();

					JSONObject geoJson = new JSONObject();
					JSONObject subGeoJson = new JSONObject();

					// Geo location
					subGeoJson.put("latitude", mLat);
					subGeoJson.put("longitude", mLon);
					geoJson.put("value", subGeoJson);
					geoJson.put("type", "net.app.core.geolocation");

					// Annotations
					annotationArray.put(geoJson);

					mainjson.put("annotations", annotationArray);
				}

				return mainjson.toString();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String response) {
			mNotReceivedResponse = false;
			if (response == null) {

				mCallback.onError(SMConstants.APPNET);
			} else {
				// Write error log to file

				mCallback.onCompleted(SMConstants.APPNET);

			}
		}

	}

	/**
	 * Function to create file
	 * */
	class CreateFileTask extends AsyncTask<String, Void, String> {

		public void write(DataOutputStream out, String outStr)
				throws IOException {
			out.writeBytes(outStr);
		}

		public byte[] readBytes(String fileUri) throws IOException {
			// this dynamically extends to take the bytes you read
			File file = new File(fileUri);
			InputStream in = null;
			ByteArrayOutputStream byteBuffer = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file));

				byteBuffer = new ByteArrayOutputStream();

				// this is storage overwritten on each iteration with bytes
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];

				// we need to know how may bytes were read to write them to the
				// byteBuffer
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					byteBuffer.write(buffer, 0, len);
				}
			} catch (Exception e) {
				Log.i("foursquare", "file attachment error");
			}

			// and then we can return your byte array.
			return byteBuffer.toByteArray();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... params) {
			String imageUrl = params[0];

			String accessToken = mSharedPreferences.getString(
					SMConstants.APPNET_ACCESS_TOKEN, "");
			// Set up request
			HttpURLConnection httpUrlConnection = null;
			URL url = null;
			try {
				url = new URL(SMConstants.APPNET_API_URL
						+ "/stream/0/files?access_token=" + accessToken);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {

				httpUrlConnection = (HttpURLConnection) url.openConnection();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				httpUrlConnection.setReadTimeout(10000);
				httpUrlConnection.setConnectTimeout(15000);
				httpUrlConnection.setRequestMethod("POST");
				httpUrlConnection.setUseCaches(false);
				httpUrlConnection.setDoInput(true);
				httpUrlConnection.setDoOutput(true);

				httpUrlConnection
						.setRequestProperty("Connection", "Keep-Alive");
				httpUrlConnection.setRequestProperty(HEADER_CONTENT_TYPE,
						CONTENT_TYPE);
				httpUrlConnection.setChunkedStreamingMode(0);

				final OutputStream outputStream = httpUrlConnection
						.getOutputStream();
				final OutputStreamWriter streamWriter = new OutputStreamWriter(
						outputStream);

				writeBoundary(streamWriter);

				streamWriter.write(String.format(
						CONTENT_DISPOSITION_HEADER_WITH_FILENAME, "content",
						"resized.jpg"));
				streamWriter.write(String.format(CONTENT_TYPE_HEADER,
						"image/jpeg"));

				streamWriter.write("\r\n");
				streamWriter.flush();
				byte[] fileData = readBytes(imageUrl);
				outputStream.write(fileData, 0, fileData.length);

				writeBoundary(streamWriter);
				writeDispositionHeader(streamWriter, "type",
						"com.example.awesome");

				String kind = "image";
				if (kind != null) {
					writeBoundary(streamWriter);
					writeDispositionHeader(streamWriter, "kind", kind);
				}

				writeBoundary(streamWriter);
				writeDispositionHeader(streamWriter, "name", "resized.jpg");

				writeBoundary(streamWriter);
				writeDispositionHeader(streamWriter, "public",
						String.valueOf(true));

				writeBoundary(streamWriter);

				streamWriter.close();
				outputStream.close();
				httpUrlConnection.connect();

				if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

					return HttpUtils.streamToString(httpUrlConnection,
							httpUrlConnection.getInputStream());
				}

			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

			return null;

		}

		protected void writeDispositionHeader(OutputStreamWriter streamWriter,
				String name, String body) throws IOException {
			streamWriter.write(String.format(CONTENT_DISPOSITION_HEADER, name));
			streamWriter.write("\r\n");
			streamWriter.write(body);
		}

		protected void writeBoundary(OutputStreamWriter streamWriter)
				throws IOException {
			streamWriter.write(DELIMITER);
			streamWriter.write(BOUNDARY);
			streamWriter.write("\r\n");
		}

		protected void onPostExecute(String response) {
			if (response == null) {

				mCallback.onError(SMConstants.APPNET);
			} else {

				parseResponse(response);
			}
		}

		private void parseResponse(String response) {
			try {
				JSONObject jsonObj = (JSONObject) new JSONTokener(response)
						.nextValue();

				JSONObject data = (JSONObject) jsonObj.getJSONObject("data");

				// String url = data.optString("url");

				String fileToken = data.optString("file_token");
				String fileId = data.optString("id");

				// Create post with file
				new CreatePostWithFileTask().execute(fileToken, fileId);

			} catch (Exception ex) {
				Log.i("appnet", ex.getMessage());
			}

		}

	}

	/**
	 * Function to create post with image
	 * */
	class CreatePostWithFileTask extends AsyncTask<String, Void, String> {

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... params) {
			String fileToken = params[0];
			String fileId = params[1];

			// Set up request
			HttpsURLConnection httpsUrlConnection = null;
			URL url = null;
			try {
				url = new URL(
						SMConstants.APPNET_API_URL
								+ "/stream/0/posts?include_post_annotations=1&access_token="
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
				final byte[] bodyBytes = getAnnotationsJson(fileToken, fileId)
						.getBytes("UTF-8");
				httpsUrlConnection
						.setFixedLengthStreamingMode(bodyBytes.length);

				final OutputStream outputStream = httpsUrlConnection
						.getOutputStream();
				outputStream.write(bodyBytes);
				outputStream.close();

				httpsUrlConnection.connect();

				if (httpsUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

					return HttpUtils.streamToString(httpsUrlConnection,
							httpsUrlConnection.getInputStream());
				}

			} catch (ClientProtocolException e) {
			} catch (IOException e) {

				Log.i("appnet", e.getMessage());
			}

			return null;

		}

		private String getAnnotationsJson(String fileToken, String fileId) {

			try {
				JSONObject mainjson = new JSONObject();

				JSONArray annotationArray = new JSONArray();
				JSONObject fileJson = new JSONObject();
				JSONObject subFileJson = new JSONObject();
				JSONObject subSubFileJson = new JSONObject();
				subSubFileJson.put("file_id", fileId);

				subSubFileJson.put("format", "oembed");
				subSubFileJson.put("file_token", fileToken);
				subFileJson.put("+net.app.core.file", subSubFileJson);
				fileJson.put("value", subFileJson);
				fileJson.put("type", "net.app.core.oembed");
				// Annotations
				annotationArray.put(fileJson);
				// Geo location

				if (mLat != 0 && mLon != 0) {

					JSONObject geoJson = new JSONObject();
					JSONObject subGeoJson = new JSONObject();

					subGeoJson.put("latitude", mLat);
					subGeoJson.put("longitude", mLon);
					geoJson.put("value", subGeoJson);
					geoJson.put("type", "net.app.core.geolocation");

					annotationArray.put(geoJson);
				}

				mainjson.put("text", mMessage);
				mainjson.put("annotations", annotationArray);

				return mainjson.toString();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String response) {
			mNotReceivedResponse = false;
			if (response == null) {

				mCallback.onError(SMConstants.APPNET);
			} else {
				mCallback.onCompleted(SMConstants.APPNET);

			}
		}

	}

	/**
	 * File class for making annotation json
	 * 
	 * @author user
	 * 
	 */
	class AppnetFile {
		String file_id;
		String file_token;
		String format;

	}

}
