package world.plus.manager.sns4.util;

import world.plus.manager.sns4.R;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToastManager {
	private Activity mActivity;

	public CustomToastManager(Activity activity) {
		mActivity = activity;
	}

	/**
	 * Show popup to tell user to log in at least one sns
	 * 
	 * @param rootView
	 * 
	 * @param rootView
	 */
	public void showPopup(final View view, final int messageId, int testViewId) {

		// Check length of textview
		final TextView checkTextSize = (TextView) mActivity
				.findViewById(testViewId);
		checkTextSize.setText(messageId);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// Create custom toast
				LayoutInflater inflater = LayoutInflater.from(mActivity);
				View layout = inflater.inflate(R.layout.custom_toast,
						null);
				TextView text = (TextView) layout
						.findViewById(R.id.custom_toast_text);
				text.setText(messageId);
				// Calculate position of view on screen
				Rect location = locateView(view);

				// Test using toast instead of popupwindow
				Toast toast = new Toast(mActivity);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, location.left
						- checkTextSize.getWidth(), location.top
						- (checkTextSize.getHeight() * 4));

				toast.show();

			}
		}, 2000);

	}

	/**
	 * Calculate position of view on screen
	 * 
	 * @param v
	 * @return
	 */
	private Rect locateView(View v) {
		int[] loc_int = new int[2];
		if (v == null)
			return null;
		try {
			v.getLocationOnScreen(loc_int);
		} catch (NullPointerException npe) {
			// Happens when the view doesn't exist on screen anymore.
			return null;
		}
		Rect location = new Rect();
		location.left = loc_int[0];
		location.top = loc_int[1];
		location.right = location.left + v.getWidth();
		location.bottom = location.top + v.getHeight();

		Log.d("location", "top: " + location.top + " left: " + location.left
				+ " height: " + v.getHeight());
		return location;
	}

}
