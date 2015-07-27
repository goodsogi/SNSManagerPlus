package world.plus.manager.sns4.manage_account;

import java.io.File;
import java.util.ArrayList;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.PopupDialog;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class AddSnsListAdapter extends ArrayAdapter<Integer> {

	private LayoutInflater mInflater;
	private ArrayList<Integer> mValues;
	private SharedPreferences mSharedPreference;
	private FacebookOAuthHelper mFacebookOAuthhelper;
	private Activity mActivity;
	private TwitterOAuthHelper mTwitterOAuthHelper;
	private FoursquareOAuthHelper mFoursquareOAuthHelper;
	private AppnetOAuthHelper mAppnetOAuthHelper;
	private LinkedinOAuthHelper mLinkedinOAuthHelper;
	private GooglePlusOAuthHelper mGoogleplusOAuthHelper;
	private Editor mEditor;

	public AddSnsListAdapter(Activity activity, ManageAccountFragment fragment,
			ArrayList<Integer> values) {
		super(activity, R.layout.list_item_add_sns, values);
		// TODO Auto-generated constructor stub
		mActivity = activity;
		mValues = values;
		mInflater = LayoutInflater.from(activity);
		mSharedPreference = activity.getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreference.edit();

		mFacebookOAuthhelper = new FacebookOAuthHelper(mActivity, fragment);
		mTwitterOAuthHelper = new TwitterOAuthHelper(mActivity, fragment);
		mFoursquareOAuthHelper = new FoursquareOAuthHelper(mActivity, fragment);
		mLinkedinOAuthHelper = new LinkedinOAuthHelper(mActivity, fragment);
		mAppnetOAuthHelper = new AppnetOAuthHelper(mActivity, fragment);
		mGoogleplusOAuthHelper = new GooglePlusOAuthHelper(mActivity);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.list_item_add_sns, parent,
				false);
		TextView userSnsName = (TextView) rowView
				.findViewById(R.id.user_sns_name);

		String userName = getSavedUserName(mValues.get(position));
		String snsName = SMConstants.getSnsName(mValues.get(position));

		SpannableString text = new SpannableString(userName + "\n" + snsName);

		int start = 0;
		int end = userName.length() + 1;
		TextAppearanceSpan mUserNameTextAppearance = new TextAppearanceSpan(
				mActivity, R.style.TextAppearanceMedium);
		TextAppearanceSpan mSnsNameTextAppearance = new TextAppearanceSpan(
				mActivity, R.style.TextAppearanceSmallGray);
		text.setSpan(mUserNameTextAppearance, start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		start = end;
		end = text.length();
		text.setSpan(mSnsNameTextAppearance, start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		userSnsName.setText(text, BufferType.SPANNABLE);

		ImageView profile = (ImageView) rowView.findViewById(R.id.profile);
		profile.setImageBitmap(getSavedProfileBitmap(mValues.get(position)));

		Button logoutButton = (Button) rowView.findViewById(R.id.logout);
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showLogOutDialog(mValues.get(position), position);
			}
		});

		return rowView;
	}

	/**
	 * Make exit dialog
	 * 
	 * @param position
	 */
	void showLogOutDialog(final int snsType, final int position) {

		final PopupDialog popup = new PopupDialog((Activity) mActivity,
				R.layout.popup_exit);
		popup.setTitle(R.string.wanna_logout);
		popup.setFirstMenuText(R.string.no);
		popup.setSecondMenuText(R.string.yes);
		popup.setFirstMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();

			}
		});

		popup.setSecondMenuListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				// Remove logout sns row
				mValues.remove(position);
				notifyDataSetChanged();
				doSnsLogout(snsType);

			}
		});

		popup.show();
	}

	private void doSnsLogout(int sns) {

		PlusOAuth plusOAuth = getOAuthHelper(sns);

		plusOAuth.doLogout();
		plusOAuth.saveLogInStatePreference(false);

		// Save Post Preference to false
		mEditor.putBoolean(getKeyPostConstant(sns), false);
		mEditor.commit();

	}

	/**
	 * Return sns key post constant
	 * 
	 * @param sns
	 * @return
	 */
	private String getKeyPostConstant(int sns) {
		switch (sns) {
		case SMConstants.FACEBOOK:
			return SMConstants.KEY_POST_FACEBOOK;

		case SMConstants.TWITTER:

			return SMConstants.KEY_POST_TWITTER;

		case SMConstants.GOOGLE_PLUS:

			return SMConstants.KEY_POST_GOOGLE_PLUS;

		case SMConstants.FOURSQUARE:

			return SMConstants.KEY_POST_FOURSQUARE;

		case SMConstants.APPNET:

			return SMConstants.KEY_POST_APPNET;

		case SMConstants.LINKEDIN:

			return SMConstants.KEY_POST_LINKEDIN;
		}
		return null;
	}

	/**
	 * Get Oauth helper
	 * 
	 * @return
	 */
	private PlusOAuth getOAuthHelper(int sns) {
		// TODO Auto-generated method stub

		switch (sns) {
		case SMConstants.FACEBOOK:
			return mFacebookOAuthhelper;

		case SMConstants.TWITTER:

			return mTwitterOAuthHelper;

		case SMConstants.GOOGLE_PLUS:

			return mGoogleplusOAuthHelper;

		case SMConstants.FOURSQUARE:

			return mFoursquareOAuthHelper;

		case SMConstants.APPNET:

			return mAppnetOAuthHelper;

		case SMConstants.LINKEDIN:

			return mLinkedinOAuthHelper;
		}
		return null;

	}

	/**
	 * Get saved user name
	 * 
	 * @return
	 */
	public String getSavedUserName(int snsName) {
		String key = null;
		switch (snsName) {
		case SMConstants.FACEBOOK:
			key = SMConstants.KEY_FACEBOOK_USER_NAME;
			break;
		case SMConstants.GOOGLE_PLUS:
			key = SMConstants.KEY_GOOGLE_PLUS_USER_NAME;
			break;
		case SMConstants.TWITTER:
			key = SMConstants.KEY_TWITTER_USER_NAME;
			break;

		case SMConstants.FOURSQUARE:
			key = SMConstants.KEY_FOURSQUARE_USER_NAME;
			break;
		case SMConstants.APPNET:
			key = SMConstants.KEY_APPNET_USER_NAME;

			break;

		case SMConstants.LINKEDIN:
			key = SMConstants.KEY_LINKEDIN_USER_NAME;
			break;
		}
		return mSharedPreference.getString(key, "");
	}

	/**
	 * Get saved profile image bitmap
	 * 
	 * @return
	 */
	public Bitmap getSavedProfileBitmap(int snsName) {

		String fileName = SMConstants.getSnsName(snsName)
				+ SMConstants.IMAGE_FILE_NAME;

		String imageFileName = mActivity.getFilesDir() + File.separator
				+ SMConstants.IMAGE_FOLDER + File.separator + fileName;

		Bitmap bitmap = BitmapFactory.decodeFile(imageFileName);
		return bitmap;
	}

}
