package world.plus.manager.sns4.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.net.Uri;

public class PiccasaImageSaver {
	private Activity mActivity;
	private PhotoResizer mPhotoResizer;

	public PiccasaImageSaver(Activity activity) {
		mActivity = activity;
		mPhotoResizer = new PhotoResizer(activity);
	}

	/**
	 * Save inputstream of image in backup album to file
	 * 
	 * @param uri
	 * @return
	 */
	public String doIt(Uri uri) {
		return saveFile(uri);

	}

	/**
	 * Save inputstream of image in backup album to file
	 * 
	 * @param uri
	 * @return
	 */
	private String saveFile(Uri uri) {
		InputStream is = null;

		try {
			is = mActivity.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		File photoFile = null;
		photoFile = mPhotoResizer.createNewFile();
		// Continue only if the File was successfully created
		if (photoFile == null)
			return "";

		OutputStream output = null;
		try {
			output = new FileOutputStream(photoFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		final byte[] buffer = new byte[8192];
		int read;

		try {
			while ((read = is.read(buffer)) != -1)
				output.write(buffer, 0, read);

			output.flush();
			output.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return photoFile.getPath();

	}

}
