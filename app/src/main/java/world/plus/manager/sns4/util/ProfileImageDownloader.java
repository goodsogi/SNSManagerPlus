package world.plus.manager.sns4.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.manage_account.OnProfileDownloadFinshListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ProfileImageDownloader extends AsyncTask<Object, Void, Bitmap> {
	private int mSnsName;

	private Activity mActivity;

	private Fragment mFragment;

	// listener to notify ManageAccountFragment that profile image finished
	// download
	OnProfileDownloadFinshListener mCallback;

	public ProfileImageDownloader(Activity activity, Fragment fragment) {
		mActivity = activity;
		mFragment = fragment;

		// This makes sure that the fragment has implemented
		// the callback interface. If not, it throws an exception
			try {
				mCallback = (OnProfileDownloadFinshListener) mFragment;
			} catch (ClassCastException e) {
				throw new ClassCastException(mFragment.toString()
						+ " must implement OnProfileDownloadFinshListener");
			}

	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		try {

			String url = (String) params[0];
			mSnsName = (Integer) params[1];
			URL imageUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) imageUrl
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = null;
			
			Log.d("facebook","url: " + url + "response code: " + connection.getResponseCode() + " url: " + connection.getURL());
			if(connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				URL imageUrl2 = connection.getURL();
				HttpURLConnection connection2 = (HttpURLConnection) imageUrl2
						.openConnection();
				connection2.setDoInput(true);
				connection2.connect();
				input = connection2.getInputStream();
			} else if( connection.getResponseCode() == HttpURLConnection.HTTP_OK){
			input = connection.getInputStream();
			}
			
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(Bitmap result) {
		if (result == null)
			return;

		// Save bitmap on internal storage
		saveBitmapSdcard(result);
	}

	/**
	 * Save bitmap on internal storage
	 * 
	 * @param result
	 */
	private void saveBitmapSdcard(Bitmap result) {

		String fileName = SMConstants.getSnsName(mSnsName) + "_profile" + ".jpg";

		final File dir = new File(mActivity.getFilesDir() + File.separator
				+ SMConstants.IMAGE_FOLDER);
		dir.mkdirs(); // create folders where write files
		final File file = new File(dir, fileName);

		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			final BufferedOutputStream bos = new BufferedOutputStream(fos,
					SMConstants.BUFFER_SIZE);
			result.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("Image", e.getMessage());

		}

		// Notify profile image is saved
		mCallback.onProfileImageSaved(mSnsName);

	}

	
}
