package world.plus.manager.sns4.write;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Place;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import world.plus.manager.sns4.main.SMConstants;

public class GetTwitterPlaceHelper {
	static ResponseList<Place> doIt(String access_token,
			String access_token_secret, double lat, double lon, String query) {
		try {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(SMConstants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(SMConstants.TWITTER_CONSUMER_SECRET);
			builder.setHttpRetryCount(2);
			builder.setHttpRetryIntervalSeconds(2);
			builder.setHttpConnectionTimeout(0);
			builder.setHttpReadTimeout(0);
			builder.setHttpStreamingReadTimeout(0);

			AccessToken accessToken = new AccessToken(access_token,
					access_token_secret);

			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);
			GeoLocation location = new GeoLocation(lat, lon);
			GeoQuery geoQuery = new GeoQuery(location);
			if (query != null && !query.equals(""))
				geoQuery.setQuery(query);
			geoQuery.setMaxResults(10);
			// !!!Check response and deal with it
			return twitter.searchPlaces(geoQuery);

		} catch (TwitterException e) {

		}
		return null;
	}

}
