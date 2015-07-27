package world.plus.manager.sns4.write;

import java.io.File;
import java.io.FileNotFoundException;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.widget.Toast;

public class GooglePlusPost extends PlusPost {

	public GooglePlusPost(Activity activity, Fragment fragment) {
		super(activity, fragment);

	}

	/**
	 * Post to Google Plus
	 * 
	 * @param message
	 * @param imgUrl
	 */
	public void post(String text, String imgUrl, String venueId, double lat,
			double lon) {

		if (imgUrl == null || imgUrl.equals("")) {
			postText(text);
		} else {
			postTextAndImage(text, imgUrl);
		}

	}

	/**
	 * Send post with photo
	 * 
	 * @param message
	 * @param imgUrl
	 */
	private void postTextAndImage(String message, String imgUrl) {
		File tmpFile = new File(imgUrl);
		String photoUri = null;
		try {
			photoUri = MediaStore.Images.Media.insertImage(
					mActivity.getContentResolver(), tmpFile.getAbsolutePath(),
					null, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent shareIntent = ShareCompat.IntentBuilder
				.from((Activity) mActivity).setText(message).setType("image/*")
				.setStream(Uri.parse(photoUri)).getIntent()
				.setPackage("com.google.android.apps.plus");

		// Check if google plus app is installed
		if (shareIntent.resolveActivity(mActivity.getPackageManager()) != null) {
			mActivity.startActivityForResult(shareIntent,
					SMConstants.GOOGLE_PLUS_POST);
		} else {
			// Show toast to tell user that google plus is not installed
			Toast.makeText(mActivity, R.string.googleplus_not_installed,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Send post only with text message
	 * 
	 * @param message
	 */
	private void postText(String message) {

		Intent shareIntent = ShareCompat.IntentBuilder
				.from((Activity) mActivity).setText(message)
				.setType("text/plain").getIntent()
				.setPackage("com.google.android.apps.plus");

		// Check if google plus app is installed
		if (shareIntent.resolveActivity(mActivity.getPackageManager()) != null) {
			mActivity.startActivityForResult(shareIntent,
					SMConstants.GOOGLE_PLUS_POST);
		} else {
			// Show toast to tell user that google plus is not installed
			Toast.makeText(mActivity, R.string.googleplus_not_installed,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Save if send post to Google Plus
	 * 
	 * @param value
	 */
	public void savePostPreference(boolean value) {
		mEditor.putBoolean(SMConstants.KEY_POST_GOOGLE_PLUS, value);
		mEditor.commit();
	}

}
