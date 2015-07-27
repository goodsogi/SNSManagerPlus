package world.plus.manager.sns4.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Resize image according to sharedpreference settings
 * 
 * @author user
 * 
 */
public class PhotoResizer {

	private Activity mActivity;
	private int IMAGE_ALLOW_WIDTH = 0;
	private int IMAGE_ALLOW_HEIGHT = 0;
	private int mSrcWidth;
	private int mSrcHeight;

	private static final String PHOTO_RESIZE_FILENAME = "resized_photo.jpg";

	public PhotoResizer(Activity activity) {
		mActivity = activity;
		// Set image size to resize
		setResizeImageSize();
	}

	/**
	 * Set image size to resize
	 * 
	 * @return
	 */
	private void setResizeImageSize() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(mActivity);

		int size = Integer.parseInt(sharedPref.getString(
				SMConstants.KEY_PREF_IMAGE_SIZE, "0"));
		switch (size) {
		case 0:
			IMAGE_ALLOW_WIDTH = 1200;
			IMAGE_ALLOW_HEIGHT = 1200;
			break;

		case 1:
			IMAGE_ALLOW_WIDTH = 800;
			IMAGE_ALLOW_HEIGHT = 600;
			break;
		case 2:
			IMAGE_ALLOW_WIDTH = 320;
			IMAGE_ALLOW_HEIGHT = 480;
			break;
		}
	}

	/**
	 * Resize image
	 * 
	 * @param imgUrl
	 * @return
	 */
	public String work(String imgUrl) {

		Bitmap bitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgUrl, options);

		mSrcWidth = options.outWidth;
		mSrcHeight = options.outHeight;

		Log.d("image", "original width: " + mSrcWidth + " height: "
				+ mSrcHeight);

		if (mSrcWidth * mSrcHeight > IMAGE_ALLOW_WIDTH * IMAGE_ALLOW_HEIGHT) {

			int scaleSize = (mSrcWidth * mSrcHeight)
					/ (IMAGE_ALLOW_WIDTH * IMAGE_ALLOW_HEIGHT);

			options = new BitmapFactory.Options();
			// Calculate and get sample size
			// options.inSampleSize = getSampleSize(scaleSize);

			// Get bitmap with options
			// bitmap = BitmapFactory.decodeFile(imgUrl, options);
			int sampleSize = getSampleSize(scaleSize);
			boolean done = false;
			while (!done) {
				options.inSampleSize = sampleSize++;
				try {
					bitmap = BitmapFactory.decodeFile(imgUrl, options);
					done = true;
				} catch (OutOfMemoryError e) {
					// Ignore. Try again.
				}
			}

			return createNewSizeBitmap(bitmap);
		}

		return imgUrl;

	}

	public boolean checkSize(Uri uri) {
		IMAGE_ALLOW_WIDTH = 1200;
		IMAGE_ALLOW_HEIGHT = 1200;

		InputStream is = null;

		try {
			is = mActivity.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(is, null, options);

		mSrcWidth = options.outWidth;
		mSrcHeight = options.outHeight;

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (mSrcWidth * mSrcHeight > IMAGE_ALLOW_WIDTH * IMAGE_ALLOW_HEIGHT) ? true
				: false;
	}

	/**
	 * Resize image
	 * 
	 * @param imgUrl
	 * @return
	 */
	public String workPiccasa(Uri uri) {

		int scaleSize = (mSrcWidth * mSrcHeight)
				/ (IMAGE_ALLOW_WIDTH * IMAGE_ALLOW_HEIGHT);

		int sampleSize = getSampleSize(scaleSize);

		InputStream is = null;
		try {
			is = mActivity.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;

		boolean done = false;
		while (!done) {
			options.inSampleSize = sampleSize++;
			try {
				bitmap = BitmapFactory.decodeStream(is, null, options);
				done = true;
			} catch (OutOfMemoryError e) {
				// Ignore. Try again.
			}
		}
		return createNewSizeBitmap(bitmap);

	}

	private String createNewSizeBitmap(Bitmap bitmap) {
		if (bitmap != null) {

			// Get new width and height
			Rect resizedRect1 = getRatioSize(bitmap.getWidth(),
					bitmap.getHeight(), IMAGE_ALLOW_WIDTH, IMAGE_ALLOW_HEIGHT);
			Rect resizedRect2 = getRatioSize(bitmap.getWidth(),
					bitmap.getHeight(), IMAGE_ALLOW_HEIGHT, IMAGE_ALLOW_WIDTH);

			int newWidth = getNewWidth(bitmap, resizedRect1, resizedRect2);
			int newHeight = getNewHeight(bitmap, resizedRect1, resizedRect2);

			Log.d("resizedImage", "new width: " + newWidth + " new height: "
					+ newHeight);

			if (newWidth > 0 && newHeight > 0) {
				Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth,
						newHeight, true);
				// Save bitmap to file
				return SaveBitmapToFile(newBitmap);

			}
			bitmap.recycle();
		} else {
		}
		return null;

	}

	public File createNewFile() {

		File dir = new File(mActivity.getFilesDir() + File.separator
				+ SMConstants.IMAGE_FOLDER);
		dir.mkdirs(); // create folders where write files
		File resizedFile = new File(dir, PHOTO_RESIZE_FILENAME);

		try {
			resizedFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resizedFile;
	}

	/**
	 * Save bitmap to file
	 * 
	 * @param bitmap
	 * @return
	 */
	private String SaveBitmapToFile(Bitmap bitmap) {

		File resizedFile = createNewFile();
		// Write bitmap to file
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(resizedFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

		// Return absolute path of file
		return resizedFile.getAbsolutePath();

	}

	// Get new height of bitmap
	private int getNewHeight(Bitmap bitmap, Rect resizedRect1, Rect resizedRect2) {
		int newHeight = 0;

		if (resizedRect1.width() * resizedRect1.height() > resizedRect2.width()
				* resizedRect2.height()) {
			newHeight = resizedRect1.height();
		} else {
			newHeight = resizedRect2.height();
		}
		return newHeight;
	}

	// Get new width of bitmap
	private int getNewWidth(Bitmap bitmap, Rect resizedRect1, Rect resizedRect2) {
		int newWidth = 0;

		if (resizedRect1.width() * resizedRect1.height() > resizedRect2.width()
				* resizedRect2.height()) {
			newWidth = resizedRect1.width();
		} else {
			newWidth = resizedRect2.width();
		}
		return newWidth;
	}

	/**
	 * Calculate sample size
	 * 
	 * @return
	 */
	private int getSampleSize(int scaleSize) {
		int sampleSize = 1;
		switch (scaleSize) {
		case 1:
			sampleSize = 1;
			break;
		case 2:
		case 3:
			sampleSize = 1;
			break;
		case 4:
		case 5:
		case 6:
		case 7:
			sampleSize = 2;
			break;
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			sampleSize = 4;
			break;
		default:
			sampleSize = 8;
			break;
		}
		return sampleSize;
	}

	/**
	 * Create cache folder
	 */
	public void createCacheFolder() {
		if (isAvailableExternalMemory() == false)
			return;

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android");
		if (file.exists() == false) {
			if (file.mkdir() == false) {
				// mIsCreatedCacheFolder = false;
				// return;
			}
		}
		file = new File(file.getAbsolutePath() + "/data");
		if (file.exists() == false) {
			if (file.mkdir() == false) {
				// mIsCreatedCacheFolder = false;
				// return;
			}
		}
		file = new File(file.getAbsolutePath() + "/"
				+ mActivity.getPackageName());
		if (file.exists() == false) {
			if (file.mkdir() == false) {
				// mIsCreatedCacheFolder = false;
				// return;
			}
		}
		File cachefile = new File(file.getAbsolutePath() + "/cache");
		if (cachefile.exists() == false) {
			if (cachefile.mkdir() == false) {
				// mIsCreatedCacheFolder = false;
				// return;
			}
		}
		File imageFile = new File(file.getAbsoluteFile() + "/images");
		if (imageFile.exists() == false) {
			imageFile.mkdir();
		}
		// mIsCreatedCacheFolder = true;
	}

	public static Rect getRatioSize(int imageWidth, int imageHeight,
			int maxWidth, int maxHeight) {
		int newWidth = 0;
		int newHeight = 0;
		float fRatioW = (float) maxWidth / (float) imageWidth;
		float fRatioH = (float) maxHeight / (float) imageHeight;

		if (fRatioW > fRatioH) {
			newHeight = maxHeight;
			newWidth = (int) (fRatioH * imageWidth);
		} else {
			newWidth = maxWidth;
			newHeight = (int) (fRatioW * imageHeight);
		}
		return new Rect(0, 0, newWidth, newHeight);
	}

	/**
	 * Get external image folder
	 * 
	 * @return
	 */
	public File getExternalImagesFolder() {
		// if (mIsCreatedCacheFolder == false)
		// return null;
		if (isAvailableExternalMemory() == false)
			return null;

		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/Android"
				+ "/data"
				+ "/"
				+ mActivity.getPackageName() + "/images");
		return folder;
	}

	/**
	 * Check if external memory if available or not
	 * 
	 * @return
	 */
	public boolean isAvailableExternalMemory() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return (mExternalStorageAvailable == true && mExternalStorageWriteable == true);
	}

}
