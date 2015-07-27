package world.plus.manager.sns4.connect_sns;

import java.util.ArrayList;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.ClickGuard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

public class GridAdapter implements ListAdapter {

	private ArrayList<Integer> mData;
	private Activity mActivity;
	private LayoutInflater mInflater;

	public GridAdapter(Activity activity, ArrayList<Integer> loginSNSs) {
		mActivity = activity;
		mData = loginSNSs;
		mInflater = LayoutInflater.from(mActivity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = mInflater.inflate(R.layout.gridview_item, parent, false);
		Button button = (Button) view.findViewById(R.id.button);
		Drawable icon = null;

		//Set background image of button
		if (isAppInstalled(getPackageName(mData.get(position)))) {
			try {
				icon = mActivity.getPackageManager().getApplicationIcon(
						getPackageName(mData.get(position)));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			button.setBackground(icon);
		} else {
			button.setBackgroundResource(R.drawable.web);
			button.setText(SMConstants.getSnsName(mData.get(position)));
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		}

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);

				if (isAppInstalled(getPackageName(mData.get(position)))) {
					runApp(getPackageName(mData.get(position)),
							getClassName(mData.get(position)));
				} else {
					runWeb(getWebUrl(mData.get(position)));
				}

			}
		});

		return view;
	}

	/**
	 * Get web url of SNS
	 * 
	 * @param app
	 * @return
	 */
	private String getWebUrl(int app) {
		// TODO Auto-generated method stub
		switch (app) {

		case SMConstants.TWITTER:
			return SMConstants.WEB_TWITTER_URL;
		case SMConstants.FACEBOOK:
			return SMConstants.WEB_FACEBOOK_URL;
		case SMConstants.GOOGLE_PLUS:
			return SMConstants.WEB_GOOGLE_PLUS_URL;
		case SMConstants.APPNET:
			return SMConstants.WEB_APPNET_URL;
		case SMConstants.FOURSQUARE:
			return SMConstants.WEB_FOURSQUARE_URL;
		case SMConstants.LINKEDIN:
			return SMConstants.WEB_LINKEDIN_URL;

		}
		return null;
	}

	/**
	 * Get package name of app
	 * 
	 * @param app
	 * @return
	 */
	private String getPackageName(int app) {
		// TODO Auto-generated method stub
		switch (app) {

		case SMConstants.TWITTER:
			return SMConstants.APP_TWITTER_PACKAGE_NAME;
		case SMConstants.FACEBOOK:
			return SMConstants.APP_FACEBOOK_PACKAGE_NAME;
		case SMConstants.GOOGLE_PLUS:
			return SMConstants.APP_GOOGLE_PLUS_PACKAGE_NAME;
		case SMConstants.APPNET:
			return SMConstants.APP_APPNET_PACKAGE_NAME;
		case SMConstants.FOURSQUARE:
			return SMConstants.APP_FOURSQUARE_PACKAGE_NAME;
		case SMConstants.LINKEDIN:
			return SMConstants.APP_LINKEDIN_PACKAGE_NAME;

		}
		return null;
	}

	/**
	 * Get class name of app
	 * 
	 * @param app
	 * @return
	 */
	private String getClassName(int app) {
		// TODO Auto-generated method stub
		switch (app) {

		case SMConstants.TWITTER:
			return SMConstants.APP_TWITTER_CLASS_NAME;
		case SMConstants.FACEBOOK:
			return SMConstants.APP_FACEBOOK_CLASS_NAME;
		case SMConstants.GOOGLE_PLUS:
			return SMConstants.APP_GOOGLE_PLUS_CLASS_NAME;
		case SMConstants.APPNET:
			return SMConstants.APP_APPNET_CLASS_NAME;
		case SMConstants.FOURSQUARE:
			return SMConstants.APP_FOURSQUARE_CLASS_NAME;
		case SMConstants.LINKEDIN:
			return SMConstants.APP_LINKEDIN_CLASS_NAME;

		}
		return null;
	}

	/**
	 * Check if app is installed with package name
	 * 
	 * @return
	 */
	private boolean isAppInstalled(String packageName) {
		PackageManager pm = mActivity.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	/**
	 * Run app
	 * 
	 * @param launchable
	 */
	private void runApp(String packageName, String className) {

		ComponentName name = new ComponentName(packageName, className);
		Intent i = new Intent();

		// i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		i.setComponent(name);

		// Check if app is installed
		if (i.resolveActivity(mActivity.getPackageManager()) != null)

			mActivity.startActivity(i);
	}

	/**
	 * Run web
	 * 
	 * @param url
	 */
	private void runWeb(String url) {
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		mActivity.startActivity(it);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

}
