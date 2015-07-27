package world.plus.manager.sns4.write;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Place;
import twitter4j.ResponseList;
import world.plus.manager.sns4.R;
import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.ClickGuard;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphPlace;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PlacesActivity extends SherlockActivity implements
		LocationListener {
	// Facebook place search constants
	private static final int FACEBOOK_PLACE_SEARCH_RADIOUS_IN_METERS = 1000;
	private static final int FACEBOOK_PLACE_SEARCH_RESULT_LIMITS = 5;
	// Facebook OAuth
	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	// Intent extra
	private String mSelectedFacebookPlaceId;
	private String mSelectedTwitterPlaceId;
	private String mSelectedFoursquareVenueId;

	private GraphLocation mSelectedFacebookLocation;
	protected Location mSelectedFoursquareLocation;

	protected String mSelectedFoursquareVenueName;
	private String mSelectedFacebookPlaceName;
	// Check login
	private boolean mIsFacebooksLogin;
	private boolean mIsTwitterLogin;
	private boolean mIsFoursquareLogin;

	// Location
	private LocationManager mLocationManager;
	private String mProvider;
	protected Location mLocation;

	// Icon display
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;

	// SharedPreference
	private SharedPreferences mSharedPreferences;

	// ProgressDialog
	private AlertDialog mProgressDialog;

	/**
	 * Called when Facebook session is opened for getting places
	 * 
	 * @author user
	 * 
	 */
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.equals(SessionState.OPENED)) {
				getFacebookPlaces("");
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);

		setActionbar();
		initLocationManager();
		// Add listener to search nearby button
		addListenerSearchNearbyButton();
		// Add listener to container toggle button
		addListenerContainerToggleButton();
		// Initialize sharedpreference, imageloader
		initFieldVariables();

		// Check sns login
		checkSnsLogin();

		// Show progress dialog while loading places
		if (mIsFacebooksLogin || mIsFoursquareLogin || mIsTwitterLogin)
			showProgressDialog();

	}

	/**
	 * Send selected venu to WriteFragment when back button was pressed
	 */
	@Override
	public void onBackPressed() {

		sendLocationDataWriteFragment();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_places_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		// When confirm button is clicked
		case R.id.action_select_place:
			sendLocationDataWriteFragment();
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLocationChanged(final Location location) {

		mLocationManager.removeUpdates(PlacesActivity.this);
		mLocation = location;
		// Get places
		getPlaces();

		// Show current location
		TextView currentLocation = (TextView) findViewById(R.id.current_location);
		currentLocation.setText(location.getLatitude() + ", "
				+ location.getLongitude());

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/**
	 * Open Facebook session to get places
	 */
	public void openForRead() {
		Session session = new Session(this);
		Session.setActiveSession(session);
		if (session == null
				|| session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		}
	}

	/**
	 * Send location data back to WriteFragment
	 */
	private void sendLocationDataWriteFragment() {

		SMConstants.SELECTED_FOURSQUARE_VENU_NAME_TEXT =  mSelectedFoursquareVenueName;

		Intent intent = new Intent();

		intent.putExtra(SMConstants.SELECTED_FOURSQUARE_VENU_ID,
				mSelectedFoursquareVenueId);
		intent.putExtra(SMConstants.SELECTED_FACEBOOK_PLACE_ID,
				mSelectedFacebookPlaceId);
		intent.putExtra(SMConstants.SELECTED_TWITTER_PLACE_ID,
				mSelectedTwitterPlaceId);
		intent.putExtra(SMConstants.SELECTED_FOURSQUARE_VENU_NAME,
				mSelectedFoursquareVenueName);
		intent.putExtra(SMConstants.SELECTED_FACEBOOK_PLACE_NAME,
				mSelectedFacebookPlaceName);
		intent.putExtra(SMConstants.SELECTED_LOCATION_LAT, getLat());
		intent.putExtra(SMConstants.SELECTED_LOCATION_LON, getLon());
		setResult(Activity.RESULT_OK, intent);
	}

	/**
	 * Get longitude for post
	 * 
	 * @return
	 */
	private double getLon() {
		if (mSelectedFoursquareVenueId != null
				&& !mSelectedFoursquareVenueId.equals("")) {
			return mSelectedFoursquareLocation.getLongitude();
		} else if (mSelectedFacebookPlaceId != null
				&& !mSelectedFacebookPlaceId.equals("")) {
			return mSelectedFacebookLocation.getLongitude();

		} else if (((CheckBox) findViewById(R.id.checkbbox_current_location))
				.isChecked()) {
			return mLocation.getLongitude();
		} else {
			return 0;
		}
	}

	/**
	 * Get lattitude for post
	 * 
	 * @return
	 */
	private double getLat() {
		if (mSelectedFoursquareVenueId != null
				&& !mSelectedFoursquareVenueId.equals("")) {
			return mSelectedFoursquareLocation.getLatitude();
		} else if (mSelectedFacebookPlaceId != null
				&& !mSelectedFacebookPlaceId.equals("")) {
			return mSelectedFacebookLocation.getLatitude();

		} else if (((CheckBox) findViewById(R.id.checkbbox_current_location))
				.isChecked()) {
			return mLocation.getLatitude();
		} else {
			return 0;
		}
	}

	/**
	 * Show progress dialog while loading places
	 */
	private void showProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Getting places...");
		mProgressDialog.show();

	}

	/**
	 * Initialize sharedpreference, imageloader
	 */
	private void initFieldVariables() {
		mSharedPreferences = getSharedPreferences(SMConstants.PREF_NAME,
				Context.MODE_PRIVATE);

		mImageLoader = ImageLoader.getInstance();
		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	/**
	 * Check sns login
	 * 
	 * @return
	 */
	private void checkSnsLogin() {

		SharedPreferences sharedPreference = getSharedPreferences(
				SMConstants.PREF_NAME, Context.MODE_PRIVATE);

		mIsFacebooksLogin = sharedPreference.getBoolean(
				SMConstants.KEY_FACEBOOK_LOGIN, false);
		mIsTwitterLogin = sharedPreference.getBoolean(
				SMConstants.KEY_TWITTER_LOGIN, false);
		mIsFoursquareLogin = sharedPreference.getBoolean(
				SMConstants.KEY_FOURSQUARE_LOGIN, false);

	}

	/**
	 * Set action bar
	 */
	private void setActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.activity_places);
	}

	/**
	 * Add listener to container toggle button
	 */
	private void addListenerContainerToggleButton() {
		// Add listener to Foursquare toggle button
		ListView foursquareVenuesList = (ListView) findViewById(R.id.list_foursquare_venues);
		LinearLayout selectedVenue = (LinearLayout) findViewById(R.id.selected_foursquare_venue);
		Button foursquareVenuesToggle = (Button) findViewById(R.id.toggle_foursquare_venues);

		makeClickListenerToggle(foursquareVenuesToggle, selectedVenue,
				foursquareVenuesList);
		// Add listener to Facebook toggle button
		final ListView facebookPlacesList = (ListView) findViewById(R.id.list_facebook_places);
		final LinearLayout selectedFacebookPlace = (LinearLayout) findViewById(R.id.selected_facebook_place);
		Button facebookPlacesToggle = (Button) findViewById(R.id.toggle_facebook_places);

		makeClickListenerToggle(facebookPlacesToggle, selectedFacebookPlace,
				facebookPlacesList);

		// Add listener to Twitter toggle button
		final ListView twitterPlacesList = (ListView) findViewById(R.id.list_twitter_places);
		final LinearLayout selectedTwitterPlace = (LinearLayout) findViewById(R.id.selected_twitter_place);
		Button twitterPlacesToggle = (Button) findViewById(R.id.toggle_twitter_places);

		makeClickListenerToggle(twitterPlacesToggle, selectedTwitterPlace,
				twitterPlacesList);
	}

	/**
	 * Add click listener to each toggle button
	 * 
	 * @param button
	 *            : toggle button
	 * @param linearLayout
	 *            : selected place
	 * @param listView
	 *            : place listview
	 */
	private void makeClickListenerToggle(Button button,
			final LinearLayout linearLayout, final ListView listView) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				if (v.isSelected()) {
					v.setSelected(false);
					listView.setVisibility(View.VISIBLE);
					linearLayout.setVisibility(View.GONE);
				} else {
					v.setSelected(true);
					listView.setVisibility(View.GONE);
				}

			}
		});
	}

	/**
	 * Add listener to search nearby button
	 */
	private void addListenerSearchNearbyButton() {
		Button searchNearby = (Button) findViewById(R.id.search_nearby);
		final EditText querySearchNearby = (EditText) findViewById(R.id.query_search_nearby);
		searchNearby.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickGuard.work(v);
				String query = querySearchNearby.getText().toString();
				if (!query.equals("")) {
					// Start search nearby with query

					if (mIsFoursquareLogin)
						new GetFoursquareVenuesTask().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, query);
					if (mIsFacebooksLogin)
						getFacebookPlaces(query);
					if (mIsTwitterLogin)
						new GetTwitterPlaces().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, query);

				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SMConstants.REQUEST_LOCATION_AGREEMENT:

			Criteria criteria = new Criteria();
			mProvider = mLocationManager.getBestProvider(criteria, true);
			if (mProvider == null) {
				// If user doesn't agree with location usage, finish activity
				finish();
			} else {
				// If user agreed with location usage, get current location
				getCurrentLocation();
			}
			break;
		}
	}

	private void initLocationManager() {
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		mProvider = mLocationManager.getBestProvider(criteria, true);

		if (mProvider == null) {
			// Move to lcation setting page
			new AlertDialog.Builder(this)
					.setTitle(R.string.agree_location_service)
					.setNeutralButton(R.string.go,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
											SMConstants.REQUEST_LOCATION_AGREEMENT);
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									finish();
								}
							}).show();
		} else {
			getCurrentLocation();
		}

	}

	/**
	 * Get current location
	 */
	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		// mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000, 10, this);
		mLocationManager
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
						10, PlacesActivity.this);

	}

	/**
	 * Get places
	 * 
	 * @param location
	 */
	private void getPlaces() {
		// TODO Auto-generated method stub
		if (mIsFoursquareLogin)
			new GetFoursquareVenuesTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, "");
		if (mIsFacebooksLogin)
			checkIfSessionOpen();
		if (mIsTwitterLogin)
			new GetTwitterPlaces().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, "");

	}

	/**
	 * Check if Facebook session is open
	 */
	private void checkIfSessionOpen() {
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened()) {

			getFacebookPlaces("");

			// Handle when session is null
		} else if (session == null
				|| session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			openForRead();

		}
	}

	/**
	 * Get Facebook places
	 * 
	 * @param location
	 */
	private void getFacebookPlaces(String query) {

		Session session = Session.getActiveSession();
		Request req = null;
		req = Request.newPlacesSearchRequest(session, mLocation,
				FACEBOOK_PLACE_SEARCH_RADIOUS_IN_METERS,
				FACEBOOK_PLACE_SEARCH_RESULT_LIMITS, query,
				new Request.GraphPlaceListCallback() {

					@Override
					public void onCompleted(final List<GraphPlace> places,
							Response response) {
						if (response.getError() != null)
							return;
						makeFacebookPlaces(places);
					}
				});

		req.executeAsync();

	}

	/**
	 * Make Facebook places
	 * 
	 * @param places
	 */
	private void makeFacebookPlaces(final List<GraphPlace> places) {
		final ListView facebookPlacesList = (ListView) findViewById(R.id.list_facebook_places);
		final Button facebookPlacesToggle = (Button) findViewById(R.id.toggle_facebook_places);
		LinearLayout titleFacebookPlace = (LinearLayout) findViewById(R.id.title_facebook_place);
		View lineFacebokPlace = (View) findViewById(R.id.line_facebook_place);

		FacebookPlacesListAdapter adapter = new FacebookPlacesListAdapter(
				PlacesActivity.this, places);
		facebookPlacesList.setAdapter(adapter);

		facebookPlacesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Set location data for post
				mSelectedFacebookPlaceId = places.get(position).getId();
				mSelectedFacebookLocation = places.get(position).getLocation();
				mSelectedFacebookPlaceName = places.get(position).getName();

				// Make selected place row
				final LinearLayout selectedVenue = (LinearLayout) findViewById(R.id.selected_facebook_place);
				CheckBox selectedCheckbox = (CheckBox) selectedVenue
						.findViewById(R.id.checkbox_venue);

				String iconUrl = "http://graph.facebook.com/"
						+ places.get(position).getId() + "/picture";

				makeSelectedPlace(selectedVenue, selectedCheckbox,
						places.get(position).getName(), iconUrl,
						facebookPlacesList, facebookPlacesToggle);

				selectedCheckbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mSelectedFacebookPlaceId = null;
								mSelectedFacebookLocation = null;
								mSelectedFacebookPlaceName = null;
								// Process change of check state of selected
								// place row
								processCheckChange(selectedVenue,
										facebookPlacesList,
										facebookPlacesToggle);
							}
						});

			}
		});

		// Do the other jobs to finish initializing listview
		initListview(facebookPlacesList, titleFacebookPlace, lineFacebokPlace);
	}

	/**
	 * Make venues list
	 * 
	 * @param venueList
	 */
	private void makeFoursquareVenuesList(
			final ArrayList<FsqVenue> venuesListData) {
		// TODO Auto-generated method stub
		final ListView venuesList = (ListView) findViewById(R.id.list_foursquare_venues);
		final Button foursquareVenuesToggle = (Button) findViewById(R.id.toggle_foursquare_venues);
		LinearLayout titleFoursquareVenue = (LinearLayout) findViewById(R.id.title_foursquare_venue);
		View lineFoursuareVenue = (View) findViewById(R.id.line_foursquare_venue);

		FoursquareVenuesListAdapter adapter = new FoursquareVenuesListAdapter(
				this, venuesListData);
		venuesList.setAdapter(adapter);

		venuesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Set location data for post
				mSelectedFoursquareVenueId = venuesListData.get(position).id;
				mSelectedFoursquareVenueName = venuesListData.get(position).name;
				mSelectedFoursquareLocation = venuesListData.get(position).location;

				// Make selected place row
				final LinearLayout selectedVenue = (LinearLayout) findViewById(R.id.selected_foursquare_venue);
				CheckBox selectedCheckbox = (CheckBox) selectedVenue
						.findViewById(R.id.checkbox_venue);

				makeSelectedPlace(selectedVenue, selectedCheckbox,
						venuesListData.get(position).name,
						venuesListData.get(position).iconUrl, venuesList,
						foursquareVenuesToggle);

				selectedCheckbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {

								mSelectedFoursquareVenueId = null;
								mSelectedFoursquareLocation = null;
								mSelectedFoursquareVenueName = null;

								// Process change of check state of selected
								// place row
								processCheckChange(selectedVenue, venuesList,
										foursquareVenuesToggle);

							}
						});

			}
		});
		// Do the other jobs to finish initializing listview
		initListview(venuesList, titleFoursquareVenue, lineFoursuareVenue);

	}

	/**
	 * Make Twitter places
	 * 
	 * @param response
	 */
	private void makeTwitterPlaces(final ResponseList<Place> response) {
		final ListView twitterPlacesList = (ListView) findViewById(R.id.list_twitter_places);
		final Button twitterPlacesToggle = (Button) findViewById(R.id.toggle_twitter_places);
		LinearLayout titleTwitterPlace = (LinearLayout) findViewById(R.id.title_twitter_place);
		View lineTwitterPlace = (View) findViewById(R.id.line_twitter_place);

		TwitterPlacesListAdapter adapter = new TwitterPlacesListAdapter(
				PlacesActivity.this, response);
		twitterPlacesList.setAdapter(adapter);

		twitterPlacesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Set location data for post
				mSelectedTwitterPlaceId = response.get(position).getId();

				// Make selected place row
				final LinearLayout selectedVenue = (LinearLayout) findViewById(R.id.selected_twitter_place);
				CheckBox selectedCheckbox = (CheckBox) selectedVenue
						.findViewById(R.id.checkbox_venue);

				makeSelectedPlace(selectedVenue, selectedCheckbox, response
						.get(position).getName(), "", twitterPlacesList,
						twitterPlacesToggle);

				selectedCheckbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mSelectedTwitterPlaceId = null;

								// Process change of check state of selected
								// place row
								processCheckChange(selectedVenue,
										twitterPlacesList, twitterPlacesToggle);

							}
						});

			}
		});
		// Do the other jobs to finish initializing listview
		initListview(twitterPlacesList, titleTwitterPlace, lineTwitterPlace);
	}

	/**
	 * Make selected place row
	 * 
	 * @param selectedVenue
	 * @param checkBox
	 * @param placeName
	 * @param iconUrl
	 * @param listView
	 * @param toggle
	 */
	private void makeSelectedPlace(View selectedVenue, CheckBox checkBox,
			String placeName, String iconUrl, ListView listView, Button toggle) {
		ImageView selectedIcon = (ImageView) selectedVenue
				.findViewById(R.id.icon_venue);
		TextView selectedName = (TextView) selectedVenue
				.findViewById(R.id.name_venue);

		if (iconUrl == null || iconUrl.equals("")) {
			selectedIcon.setImageResource(R.drawable.empty_photo);
		} else {
			mImageLoader.displayImage(iconUrl, selectedIcon, mOptions, null);

		}

		selectedName.setText(placeName);
		checkBox.setChecked(true);
		checkBox.setClickable(true);

		// Show selected place row and hide listview
		selectedVenue.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		toggle.setSelected(true);
	}

	/**
	 * Do the other jobs to finish initializing listview
	 * 
	 * @param listView
	 * @param linearLayout
	 * @param line
	 */
	private void initListview(ListView listView, LinearLayout linearLayout,
			View line) {
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setVisibility(View.VISIBLE);
		linearLayout.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	/**
	 * Process change of check state of selected place row
	 * 
	 * @param selectedVenue
	 * @param listView
	 * @param toggle
	 */
	private void processCheckChange(View selectedVenue, ListView listView,
			Button toggle) {
		// When checkbox unchecked, hide selected
		// venu
		// row and recreate listview
		selectedVenue.setVisibility(View.GONE);
		listView.setItemChecked(-1, true);
		listView.setVisibility(View.VISIBLE);
		toggle.setSelected(false);
	}

	/**
	 * Get Foursquare venues
	 * 
	 * @author user
	 * 
	 */
	class GetFoursquareVenuesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return GetFoursquareVenuesHelper.doIt(params[0], String
					.valueOf(mLocation.getLatitude()), String.valueOf(mLocation
					.getLongitude()), mSharedPreferences.getString(
					SMConstants.KEY_FOURSQUARE_ACCESS_TOKEN, ""));

		}

		protected void onPostExecute(String response) {
			// mIsPostHandled = true;
			// mHandler.removeCallbacksAndMessages(null);
			if (response == null)
				return;

			// Make venues list
			makeFoursquareVenuesList(FoursquareVenuesParser.doIt(response));

		}

	}

	/**
	 * Get Twitter place
	 * */
	class GetTwitterPlaces extends AsyncTask<String, Void, ResponseList<Place>> {

		/**
		 * getting Places JSON
		 * */
		protected ResponseList<Place> doInBackground(String... args) {
			String query = args[0];

			return GetTwitterPlaceHelper
					.doIt(mSharedPreferences.getString(
							SMConstants.KEY_TWITTER_ACCESS_TOKEN, ""),
							mSharedPreferences
									.getString(
											SMConstants.KEY_TWITTER_ACCESS_TOKEN_SECRET,
											""), mLocation.getLatitude(),
							mLocation.getLongitude(), query);

		}

		protected void onPostExecute(final ResponseList<Place> response) {
			if (response == null)
				return;
			// Make Twitter places
			makeTwitterPlaces(response);
		}
	}

}
