package world.plus.manager.sns4.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePathFinder {

	/**
	 * Get image file in camera album
	 * 
	 * @param uri
	 * @return
	 */
	static public String getPathCameraAlbum(Activity activity, Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		if (uri == null) {
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}

		Cursor mCursor = activity.getContentResolver().query(uri, projection,
				null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
		if (mCursor == null || mCursor.getCount() < 1) {
			return null;
		}
		int column_index = mCursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		mCursor.moveToFirst();

		String path = mCursor.getString(column_index);

		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}

		return path;
	}

}
