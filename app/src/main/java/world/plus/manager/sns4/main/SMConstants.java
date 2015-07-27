package world.plus.manager.sns4.main;

public class SMConstants {

	// Universal Image Downloader
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	// Common
	private final static int ONE_SECOND_MILLI = 1000;
	private final static int ONE_MINUTES_MILLI = ONE_SECOND_MILLI * 60;
	public static final String KEY_DRAFTED_TWITTER_VENUE_ID = "draftedTwitterVenueId";
	public static final String KEY_DRAFTED_FACEBOOK_VENUE_ID = "draftedFacebookVenueId";
	public static final String KEY_DRAFTED_FOURSQUARE_VENUE_ID = "draftedFoursquareVenueId";
	public static final String KEY_DRAFTED_LON = "draftedLon";
	public static final String KEY_DRAFTED_LAT = "draftedLat";
	public static final String KEY_IS_PROFILE_SHOWN = "isProfileSaved";
	public static String SELECTED_FOURSQUARE_VENU_NAME_TEXT = "foursquareVenueNameText";
	public final static int POST_DELAY_TIME = ONE_SECOND_MILLI * 10;
	public static final int THREE_MINUTES = ONE_MINUTES_MILLI * 3;

	public static final int TWITTER = 0;
	public static final int FACEBOOK = 1;
	public static final int GOOGLE_PLUS = 2;
	public static final int FOURSQUARE = 3;
	public static final int APPNET = 4;
	public static final int LINKEDIN = 5;

	public static final int WRITE_FRAGMENT = 0;
	public static final int CONNECT_SNS_FRAGMENT = 1;
	public static final int MANAGE_ACCOUNT_FRAGMENT = 2;

	public static final String TWITTER_TEXT = "Twitter";
	public static final String FACEBOOK_TEXT = "Facebook";
	public static final String GOOGLE_PLUS_TEXT = "Google Plus";
	public static final String FOURSQUARE_TEXT = "Foursquare";
	public static final String APPNET_TEXT = "Appnet";
	public static final String LINKEDIN_TEXT = "LinkedIn";

	public static final String EXTRA_IMAGE_PATH = "imgPath";
	public static final int FROM_CAMERA_ALBUM = 12;
	public static final String PREF_NAME = "sns_manager";

	public static final String TAG_WRITE_FRAGMENT = "writeFragment";
	public static final String KEY_FIRST_INSTALLED = "firstInstalled";
	public static final String TAG_MANAGE_ACCOUNT_FRAGMENT = "SNSManageFragment";
	public static final String TAG_CONNECT_SNS_FRAGMENT = "ConnectSNSFragment";
	public static final String TAG_SETTINGS_FRAGMENT = "SettingFragment";

	public static final String KEY_IMAGE_SOURCE = "imageSource";
	public static final String SOURCE_GALLERY = "gallery";
	public static final String SOURCE_CAMERA = "camera";
	// foler name on sdcard for saving profile image
	public static final String IMAGE_FOLDER = "SnsManger+";
	public static final String IMAGE_FILE_NAME = "_profile.jpg";
	public static final int BUFFER_SIZE = 1024 * 8;

	// Twitter
	public static final String KEY_URL = "URL";

	public static String TWITTER_CONSUMER_KEY = "WCaYqV0OSPxIGBVEP74pKQ";
	public static String TWITTER_CONSUMER_SECRET = "m2G13HA56dVzLOduAQ7N5tEAAi7NbVc561TU6mhsC0Q";

	public static final int TWITTER_OAUTH = 777;
	public static final String KEY_TWITTER_ACCESS_TOKEN = "twitter_access_token";
	public static final String KEY_TWITTER_ACCESS_TOKEN_SECRET = "twitter_access_token_secret";

	public static final String KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	public static final String TWITTER_CALLBACK_URL = "twitter-callback:///";

	public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	public static final String KEY_POST_TWITTER = "postTwitter";

	public static final String APP_TWITTER_PACKAGE_NAME = "com.twitter.android";
	public static final String APP_TWITTER_CLASS_NAME = "com.twitter.android.StartActivity";
	public static final String WEB_TWITTER_URL = "https://mobile.twitter.com";

	public static final String KEY_TWITTER_USER_NAME = "twitterUserName";
	public static final String KEY_TWITTER_PROFILE = "twitterProfile";

	// Facebook
	public static final String KEY_FACEBOOK_LOGIN = "isFacebookLogedIn";
	public static final String KEY_POST_FACEBOOK = "postFacebook";
	public static final String KEY_FACEBOOK_USER_NAME = "facebookUserName";
	public static final String KEY_FACEBOOK_PROFILE = "facebookProfile";

	public static final String APP_FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
	public static final String APP_FACEBOOK_CLASS_NAME = "com.facebook.katana.LoginActivity";
	public static final String WEB_FACEBOOK_URL = "https://m.facebook.com";

	// Google Plus
	public static final String KEY_GOOGLE_PLUS_LOGIN = "isGooglePlusLogedIn";
	public static final String KEY_POST_GOOGLE_PLUS = "postGooglePlus";
	public static final int GOOGLE_PLUS_POST = 15;

	public static final String APP_GOOGLE_PLUS_PACKAGE_NAME = "com.google.android.apps.plus";
	public static final String APP_GOOGLE_PLUS_CLASS_NAME = "com.google.android.apps.plus.phone.HomeActivity";
	public static final String WEB_GOOGLE_PLUS_URL = "https://plus.google.com";
	public static final String KEY_GOOGLE_PLUS_USER_NAME = "googleplusUserName";
	public static final String KEY_GOOGLE_PLUS_PROFILE = "googleplusProfile";

	// Instagram
	public static String INSTAGRAM_CLIENT_ID = "b0f2f3efafae4bfd8610381bf6e24905";
	public static String INSTAGRAM_CLIENT_SECRET = "fec5fe5d0abc467186b46a3fcd11dfc6";
	public static final String INSTAGRAM_CALLBACK_URL = "instagram-callback:/";

	public static final String INSTAGRAM_AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	public static final String INSTAGRAM_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String INSTAGRAM_API_URL = "https://api.instagram.com/v1";

	public static final String INSTAGRAM_AUTH_URL_TOTAL = INSTAGRAM_AUTH_URL
			+ "?client_id="
			+ INSTAGRAM_CLIENT_ID
			+ "&amp;redirect_uri="
			+ INSTAGRAM_CALLBACK_URL
			+ "&amp;response_type=code&amp;display=touch&amp;scope=likes+comments+relationships";
	public static final String INSTAGRAM_TOKEN_URL_TOTAL = INSTAGRAM_TOKEN_URL
			+ "?client_id=" + INSTAGRAM_CLIENT_ID + "&amp;client_secret="
			+ INSTAGRAM_CLIENT_SECRET + "&amp;redirect_uri="
			+ INSTAGRAM_CALLBACK_URL + "&amp;grant_type=authorization_code";

	public static final String INSTAGRAM_REQUEST_TOKEN = "instagramRequestToken";
	public static final int INSTAGRAM_OAUTH = 342;
	public static final String KEY_INSTAGRAM_LOGIN = "isInstagramLogedIn";
	public static final String KEY_INSTAGRAM_ACCESS_TOKEN = "instagramAccessToken";

	public static final String KEY_INSTAGRAM_USER_NAME = "instagramUserName";
	public static final String KEY_INSTAGRAM_PROFILE = "instagramProfile";
	public static final String KEY_INSTAGRAM_USER_ID = "instagramUserId";

	public static final String APP_INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
	public static final String APP_INSTAGRAM_CLASS_NAME = "com.instagram.android.activity.MainTabActivity";
	public static final String WEB_INSTAGRAM_URL = "http://instagram.com";

	// Foursquare
	public static String FOURSQUARE_CLIENT_ID = "VPOKKZENRHQ2YPQW1BEYOLF3QCRDH25AO5MAF1RT2HQNW0Z3";
	public static String FOURSQUARE_CLIENT_SECRET = "ESKUJTFPHJE1KJGDFV2SADPWUOS0LCDYLDOZ3MWGHEZZMMNI";
	public static final String FOURSQUARE_CALLBACK_URL = "myapp://connect";

	public static final String FOURSQUARE_AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	public static final String FOURSQUARE_TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";
	public static final String FOURSQUARE_API_URL = "https://api.foursquare.com/v2";

	public static final String FOURSQUARE_AUTH_URL_TOTAL = FOURSQUARE_AUTH_URL
			+ "&client_id=" + FOURSQUARE_CLIENT_ID + "&redirect_uri="
			+ FOURSQUARE_CALLBACK_URL;
	public static final String FOURSQUARE_TOKEN_URL_TOTAL = FOURSQUARE_TOKEN_URL
			+ "&client_id="
			+ FOURSQUARE_CLIENT_ID
			+ "&client_secret="
			+ FOURSQUARE_CLIENT_SECRET
			+ "&redirect_uri="
			+ FOURSQUARE_CALLBACK_URL;;

	public static final String FOURSQUARE_REQUEST_TOKEN = "foursquareRequestToken";
	public static final int FOURSQUARE_OAUTH = 342;
	public static final String KEY_FOURSQUARE_LOGIN = "isFoursquareLogedIn";
	public static final String KEY_FOURSQUARE_ACCESS_TOKEN = "foursquareAccessToken";

	public static final String KEY_FOURSQUARE_USER_NAME = "foursquareUserName";
	public static final String KEY_FOURSQUARE_PROFILE = "foursquareProfile";
	public static final String KEY_FOURSQUARE_USER_ID = "foursquareUserId";

	public static final String APP_FOURSQUARE_PACKAGE_NAME = "com.joelapenna.foursquared";
	public static final String APP_FOURSQUARE_CLASS_NAME = "com.joelapenna.foursquared.MainActivity";
	public static final String WEB_FOURSQUARE_URL = "http://www.foursquare.com";

	public static final String KEY_POST_FOURSQUARE = "postFoursquare";

	// App.net
	public static String APPNET_CLIENT_ID = "fhYgBPZ8XkZhvfwRkZGXRBBYp5MVUdKp";
	public static String APPNET_CLIENT_SECRET = "sznh6Hskdn5LfrWXZXcyWPvcYHUtUJNH";
	public static final String APPNET_CALLBACK_URL = "appnet-callback:///";

	public static final String APPNET_AUTH_URL = "https://account.app.net/oauth/authenticate";
	public static final String APPNET_TOKEN_URL = "https://account.app.net/oauth/access_token";

	public static final String APPNET_PERMISSION = "basic write_post";
	public static final String APPNET_API_URL = "https://alpha-api.app.net";

	public static final String APPNET_AUTH_URL_TOTAL = APPNET_AUTH_URL
			+ "?client_id=" + APPNET_CLIENT_ID + "&response_type=token"
			+ "&redirect_uri=" + APPNET_CALLBACK_URL + "&scope="
			+ APPNET_PERMISSION;
	public static final String APPNET_TOKEN_URL_TOTAL = APPNET_TOKEN_URL
			+ "&client_id=" + APPNET_CLIENT_ID + "&client_secret="
			+ APPNET_CLIENT_SECRET + "&redirect_uri=" + APPNET_CALLBACK_URL;;

	public static final String APPNET_REQUEST_TOKEN = "appnetRequestToken";
	public static final int APPNET_OAUTH = 442;
	public static final String KEY_APPNET_LOGIN = "isAppnetLogedIn";
	public static final String APPNET_ACCESS_TOKEN = "appnetAccessToken";

	public static final String KEY_APPNET_USER_NAME = "appnetUserName";
	public static final String KEY_APPNET_PROFILE = "appnetProfile";
	public static final String KEY_APPNET_USER_ID = "appnetUserId";

	public static final String APP_APPNET_PACKAGE_NAME = "net.app.passport";
	public static final String APP_APPNET_CLASS_NAME = "net.app.adnpassport.LaunchActivity";
	public static final String WEB_APPNET_URL = "http://www.app.net";

	public static final String KEY_POST_APPNET = "postAppnet";
	
	// LinkedIn
	public static String LINKEDIN_USER_TOKEN = "9637375d-de0d-4434-8f5f-afc43d3a0ce6";
	public static String LINKEDIN_USER_SECRET = "7d92d8bf-f01d-4bbb-89b4-e59ae4142646";
	public static String LINKEDIN_API_KEY = "75ctl01u6b8w41";
	public static String LINKEDIN_SECRET_KEY = "IwT4H3Bc0qcNzOWW";
	public static String LINKEDIN_SCOPE = "&scope=w_messages";
	public static String LINKEDIN_STATE = "DCEEFWF45453sdffef424";
	public static final String LINKEDIN_CALLBACK_URL = "https://linkedin-callbac/";

	public static final String LINKEDIN_AUTH_URL = "https://www.linkedin.com/uas/oauth2/authorization?";
	public static final String LINKEDIN_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken?";

	public static final String LINKEDIN_PERMISSION = "basic write_post";
	public static final String LINKEDIN_API_URL = "https://api.linkedin.com/v1/";
	public static final String LINKEDIN_API_URL_USERNAME_PROFILE = LINKEDIN_API_URL
			+ "people/~:(id,first-name,last-name,picture-url)?format=json&oauth2_access_token=";
	
	public static final String LINKEDIN_API_URL_POST_SHARE = LINKEDIN_API_URL
			+ "people/~/shares?format=json&oauth2_access_token=";

	
	public static final String LINKEDIN_AUTH_URL_TOTAL = LINKEDIN_AUTH_URL
			+ "response_type=code" + "&client_id=" + LINKEDIN_API_KEY
			+ "&state=" + LINKEDIN_STATE +"&redirect_uri="
			+ LINKEDIN_CALLBACK_URL;

//	public static final String LINKEDIN_AUTH_URL_TOTAL = LINKEDIN_AUTH_URL
//			+ "response_type=code" + "&client_id=" + LINKEDIN_API_KEY
//			+ "&state=" + LINKEDIN_STATE + LINKEDIN_SCOPE +"&redirect_uri="
//			+ LINKEDIN_CALLBACK_URL;

	public static final String LINKEDIN_AUTH_CODE = "linkedinAuthCode";
	public static final int LINKEDIN_OAUTH = 464;
	public static final String KEY_LINKEDIN_LOGIN = "isLinkedinLogedIn";
	public static final String LINKEDIN_ACCESS_TOKEN = "linkedinAccessToken";

	public static final String KEY_LINKEDIN_USER_NAME = "linkedinUserName";
	public static final String KEY_LINKEDIN_PROFILE = "linkedinProfile";
	public static final String KEY_LINKEDIN_USER_ID = "linkedinUserId";

	public static final String APP_LINKEDIN_PACKAGE_NAME = "com.linkedin.android";
	public static final String APP_LINKEDIN_CLASS_NAME = "com.linkedin.android.authenticator.LaunchActivity";
	public static final String WEB_LINKEDIN_URL = "http://www.linkedin.com";

	public static final String KEY_POST_LINKEDIN = "postLinkedin";
	// Settings
	public static final String KEY_PREF_IMAGE_SIZE = "prefImageSize";
	public static final String KEY_PREF_AUTO_RESIZE = "prefAutoResize";
	public static final String KEY_DRAFTED_TEXT = "draftedText";
	public static final String KEY_DRAFTED_IMAGE = "draftedImage";
	public static final String KEY_PREF_KEEP_DRAFT = "prefKeepDraft";
	public static final int FROM_GALLERY = 35;
	public static final int FROM_CAMERA = 45;
	public static final String KEY_TIMELINE_ALBUM_ID = "timelineAlbumId";
	public static final String KEY_PREVIOUS_MESSAGE = "previousMessage";
	public static final int REQUEST_LOCATION_AGREEMENT = 436;
	public static final int REQUEST_PLACES = 222;
	public static final String SELECTED_FOURSQUARE_VENU_ID = "foursquareVenueId";
	public static final String SELECTED_FACEBOOK_PLACE_ID = "facebookPlaceId";
	public static final String SELECTED_TWITTER_PLACE_ID = "twitterPlaceId";
	public static final String SELECTED_LOCATION_LAT = "locationLat";
	public static final String SELECTED_LOCATION_LON = "locationLon";
	public static final String SELECTED_FOURSQUARE_VENU_NAME = "foursquareVenueName";
	public static final String SELECTED_FACEBOOK_PLACE_NAME = "facebookPlaceName";
	public static final String FIRST_RUN_MANAGE_ACCOUNT = "firstRunManageAccount";
	public static final String FIRST_RUN_WRITE_FRAGMENT = "firstRunWriteFragment";
	
	/**
	 * Get sns name
	 * 
	 * @return
	 */
	public static String getSnsName(int snsType) {
		switch (snsType) {
		case SMConstants.FACEBOOK:
			return FACEBOOK_TEXT;
		case SMConstants.TWITTER:
			return TWITTER_TEXT;
		case SMConstants.GOOGLE_PLUS:
			return GOOGLE_PLUS_TEXT;
		case SMConstants.FOURSQUARE:
			return FOURSQUARE_TEXT;
		case SMConstants.APPNET:
			return APPNET_TEXT;
		case SMConstants.LINKEDIN:
			return LINKEDIN_TEXT;
		}
		return "";

	}

}
