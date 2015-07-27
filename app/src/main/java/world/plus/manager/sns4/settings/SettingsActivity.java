package world.plus.manager.sns4.settings;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class SettingsActivity extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private String[] mImageSizes;
	private EasyTracker mTracker;

	@Override
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageSizes = getResources().getStringArray(R.array.imageSizes);
		addPreferencesFromResource(R.xml.preferences);

		// Set action bar
		setActionbar();

		// Set initial value of image size preference
		initImageSizePreference();

		// Set initial value of auto resize preference
		initAutoResizePreference();

		// Set initial value of keep draft preference
		initKeepDraft();

		// Instantiate google analytics
		mTracker = EasyTracker.getInstance(this);
	}

	/**
	 * Add sharedpreference change listener
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		mTracker.activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		mTracker.activityStop(this); // Add this method.
	}

	/**
	 * Remove sharedpreference change listener
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 * When user selects image size, display it on summary
	 */
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		String eventLabel = "";
		long eventValue = 0;
		if (key.equals(SMConstants.KEY_PREF_IMAGE_SIZE)) {
			Preference imageSizePref = findPreference(key);
			int position = Integer.parseInt(sharedPreferences
					.getString(key, ""));
			// Set summary to be the user-description for the selected value
			imageSizePref.setSummary(mImageSizes[position]);

			eventLabel = "image_size";
			eventValue = position;
		} else if (key.equals(SMConstants.KEY_PREF_KEEP_DRAFT)) {
			eventLabel = "keep_draft";
			boolean value = sharedPreferences.getBoolean(key, false);
			eventValue = (value) ? 1 : 0;

		} else if (key.equals(SMConstants.KEY_PREF_AUTO_RESIZE)) {
			eventLabel = "auto_resize";
			boolean value = sharedPreferences.getBoolean(key, false);
			eventValue = (value) ? 1 : 0;
		}

		// Send photo event to Google Analytics
		mTracker.send(MapBuilder.createEvent("ui_action", // Event category
															// (required)
				"setting", // Event action (required)
				eventLabel, // Event label
				eventValue) // Event value
				.build());
	}

	/**
	 * Set action bar
	 */
	private void setActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.activity_settings);
	}

	/**
	 * Set initial value of keep draft preference
	 */
	private void initKeepDraft() {
		Preference autoKeepDraft = findPreference(SMConstants.KEY_PREF_KEEP_DRAFT);
		boolean value = getPreferenceScreen().getSharedPreferences()
				.getBoolean(SMConstants.KEY_PREF_KEEP_DRAFT, true);
		autoKeepDraft.setDefaultValue(value);
	}

	/**
	 * Set initial value of image size preference
	 */
	private void initImageSizePreference() {
		Preference imageSizePref = findPreference(SMConstants.KEY_PREF_IMAGE_SIZE);
		int position = Integer.parseInt(getPreferenceScreen()
				.getSharedPreferences().getString(
						SMConstants.KEY_PREF_IMAGE_SIZE, "0"));
		// Set summary to be the user-description for the selected value
		imageSizePref.setSummary(mImageSizes[position]);
	}

	/**
	 * Set initial value of auto resize preference
	 */
	private void initAutoResizePreference() {
		Preference autoResizePref = findPreference(SMConstants.KEY_PREF_AUTO_RESIZE);
		boolean value = getPreferenceScreen().getSharedPreferences()
				.getBoolean(SMConstants.KEY_PREF_AUTO_RESIZE, true);
		autoResizePref.setDefaultValue(value);
	}

}
