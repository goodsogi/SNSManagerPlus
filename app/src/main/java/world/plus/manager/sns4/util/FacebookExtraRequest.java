package world.plus.manager.sns4.util;

import java.io.File;
import java.io.FileNotFoundException;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;

public class FacebookExtraRequest {
	private static final String MY_FEED = "me/feed";
	private static final String MY_ALBUM = "me/albums";
	private static final String MY_PHOTOS = "me/photos";

	/**
	 * Creates a new Request configured to retrieve a user's own profile.
	 * 
	 * @param session
	 *            the Session to use, or null; if non-null, the session must be
	 *            in an opened state
	 * @param callback
	 *            a callback that will be called when the request is completed
	 *            to handle success or error conditions
	 * @return a Request that is ready to execute
	 */
	public static Request newAlbumIdRequest(Session session,
			final Callback callback) {
		Callback wrapper = new Callback() {
			@Override
			public void onCompleted(Response response) {
				if (callback != null) {
					callback.onCompleted(response);
				}
			}
		};
		return new Request(session, MY_ALBUM, null, null, wrapper);
	}

	public static Request newStatusUpdateRequest(Session session,
			String message, String placeId, Callback callback) {
		Bundle parameters = new Bundle();
		parameters.putString("message", message);

		if (placeId != null) {
			parameters.putString("place", placeId);
		}

		return new Request(session, MY_FEED, parameters, HttpMethod.POST,
				callback);
	}

	public static Request newUploadPhotoRequest(Session session, File file,
			String message, String albumId, String placeId, Callback callback)
			throws FileNotFoundException {
		ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file,
				ParcelFileDescriptor.MODE_READ_ONLY);
		Bundle parameters = new Bundle(1);
		parameters.putParcelable("picture", descriptor);
		parameters.putString("message", message);

		if (placeId != null) {
			parameters.putString("place", placeId);
		}

		return new Request(session, MY_PHOTOS, parameters, HttpMethod.POST,
				callback);
		// 페이스북 권한 관련 오류발생 (Application does not have the capability to make
		// this API call)
		// 앨범 아이디 사용이 원인 같음
		// return new Request(session, albumId, parameters, HttpMethod.POST,
		// callback);
	}

	// public static Request newUploadPhotoRequest(Session session, File file,
	// String message, String albumId, String placeId, Callback callback) {
	// // TODO Auto-generated method stub
	//
	// Bundle parameters = new Bundle();
	// parameters.putString("message", message);
	//
	// if (placeId != null) {
	// parameters.putString("place", placeId);
	// }
	//
	//
	// return new Request(session, albumId, parameters, HttpMethod.POST,
	// callback);
	// }

}
