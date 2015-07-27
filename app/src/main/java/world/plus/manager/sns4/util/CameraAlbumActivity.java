package world.plus.manager.sns4.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import world.plus.manager.sns4.main.SMConstants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Activity that manage getting image from camera or album
 * 
 * @author user
 * 
 */
public class CameraAlbumActivity extends Activity {

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;

	private Uri mImageUri;
	private String mFullPath;
	private String mCurrentPhotoPath;
	private static final String TYPE_IMAGE = "image/*";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if user wants to get image from camera or album
		Intent intent = getIntent();
		String type = intent.getStringExtra(SMConstants.KEY_IMAGE_SOURCE);
		if (type.equals(SMConstants.SOURCE_CAMERA))
			doTakePhotoAction();
		else
			doTakeAlbumAction();
	}

	public void doTakePhotoAction() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
			}
		}

	}

	public void doTakeAlbumAction() {

		Intent intent = new Intent();
		intent.setType(TYPE_IMAGE);
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(intent, PICK_FROM_ALBUM);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			finish();
			return;
		}

		switch (requestCode) {
		case PICK_FROM_ALBUM: {
			mImageUri = data.getData();
			mFullPath = mImageUri.toString();

			break;

		}

		case PICK_FROM_CAMERA: {

			// Add piture taken with camera to album
			galleryAddPic();

			// mFullPath = mImageUri.getPath();
			mFullPath = mCurrentPhotoPath;
			Log.d("image", mFullPath);

			break;
		}

		}

		// Return to WriteFragment with path of image file
		Intent data2 = new Intent();

		data2.putExtra(SMConstants.EXTRA_IMAGE_PATH, mFullPath);
		setResult(RESULT_OK, data2);
		finish();

	}

	/**
	 * Create temp file to save image
	 * 
	 * @return
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	/**
	 * Add piture taken with camera to album
	 */
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		// Add "file:" to path of image file
		File f = new File("file:" + mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

}
