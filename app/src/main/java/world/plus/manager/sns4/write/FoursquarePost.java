package world.plus.manager.sns4.write;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.ErrorLogger;
import world.plus.manager.sns4.util.HttpUtils;
import world.plus.manager.sns4.util.PlusHttpClient;
import world.plus.manager.sns4.util.TimeUtils;

public class FoursquarePost extends PlusPost {

    public static final String TEST_AUTH_STRING = "";
    final Handler mHandler = new Handler();
    public boolean mIsPostHandled;
    public int mErrorCount;
    public boolean mPostCancelled;
    protected boolean mNotReceivedResponse;
    // Handle callback regarding post
    OnPostCallbackListener mCallback;
    private ErrorLogger mErrorLogger;

    public FoursquarePost(Activity activity, Fragment fragment) {
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

        mErrorLogger = new ErrorLogger();
        mNotReceivedResponse = true;
    }

    private void createCheckinTaskTest(String text, String venueId) {

        String urlString = SMConstants.FOURSQUARE_API_URL
                + "/checkins/add?oauth_token="
                + mSharedPreferences.getString(
                SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN, "");


        RequestParams params = new RequestParams();
        params.put("v", TimeUtils.getTodayDateFoursquare());
        params.put("venueId", venueId);
        params.put("shout", text);

        AsyncHttpClient client = new AsyncHttpClient();


        client.post(urlString, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {

                    mCallback.onError(SMConstants.FOURSQUARE);
                } else {
                    // Write error log to file
                    // mErrorLogger.write("twitter success");
                    mCallback.onCompleted(SMConstants.FOURSQUARE);


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mCallback.onError(SMConstants.FOURSQUARE);
            }


        });
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

        if (imgUrl != null && !imgUrl.equals("")) {
            new AddPhotoTask().execute(text, imgUrl, venueId);
        } else {


            new CreateCheckinTask().execute(text, venueId);
        }

		// Stop processing after 30 seconds
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (mNotReceivedResponse) {
					mCallback.onError(SMConstants.FOURSQUARE);
					mErrorLogger
							.write("too late to get response to Foursquare");

				}
			}
		}, SMConstants.POST_DELAY_TIME);

    }

    /**
     * Save if send post to Twitter
     *
     * @param value
     */
    public void savePostPreference(boolean value) {
        mEditor.putBoolean(SMConstants.KEY_POST_FOURSQUARE, value);
        mEditor.commit();
    }


    /**
     * Function to update status
     */
    class AddPhotoTask extends AsyncTask<String, Void, String> {


        /**
         * getting Places JSON
         */
        protected String doInBackground(String... params) {
            String message = params[0];
            String imageUrl = params[1];
            String venueId = params[2];

            String urlString = "https://api.foursquare.com/v2/photos/add?oauth_token="
                    + mSharedPreferences.getString(
                    SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN, "");

            MultipartEntity entity = new MultipartEntity();
            try {
                entity.addPart("v",
                        new StringBody(TimeUtils.getTodayDateFoursquare()));

               entity.addPart("venueId", new StringBody(venueId));
                entity.addPart("postText", new StringBody(message));
               // entity.addPart("checkinId", new StringBody("4bf58dd8d48988d1f2931735"));


                //entity.addPart("oauth_token", new StringBody(accessToken));
                if (imageUrl != null && !imageUrl.equals("")) {
                    ByteArrayBody imgBody = null;
                    try {
                        imgBody = new ByteArrayBody(
                                HttpUtils.readBytes(imageUrl), "image/jpeg",
                                "FS_image");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    entity.addPart("image", imgBody);
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return PlusHttpClient.doPostMultipartEntirty(urlString, entity);

        }


        protected void onPostExecute(String response) {
            // mIsPostHandled = true;
            // mHandler.removeCallbacksAndMessages(null);
            if (response == null) {

                mCallback.onError(SMConstants.FOURSQUARE);
            } else {
                // Write error log to file
                // mErrorLogger.write("twitter success");
                mCallback.onCompleted(SMConstants.FOURSQUARE);


            }
        }


    }

    /**
     * Function to update status
     */
    class CreateCheckinTask extends AsyncTask<String, Void, String> {

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... params) {
            String message = params[0];
            String venueId = params[1];

            String urlString = SMConstants.FOURSQUARE_API_URL
                    + "/checkins/add?oauth_token="
                    + mSharedPreferences.getString(
                    SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN, "");

            MultipartEntity entity = new MultipartEntity();
            try {
                entity.addPart("v",
                        new StringBody(TimeUtils.getTodayDateFoursquare()));

                entity.addPart("venueId", new StringBody(venueId));
                entity.addPart("shout", new StringBody(message));

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return PlusHttpClient.doPostMultipartEntirty(urlString, entity);
        }

        protected void onPostExecute(String response) {
            // mIsPostHandled = true;
            // mHandler.removeCallbacksAndMessages(null);
            if (response == null) {

                mCallback.onError(SMConstants.FOURSQUARE);
            } else {
                // Write error log to file
                // mErrorLogger.write("twitter success");
                mCallback.onCompleted(SMConstants.FOURSQUARE);

            }
        }

    }

}
