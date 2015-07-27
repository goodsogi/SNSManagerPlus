package world.plus.manager.sns4.write;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import world.plus.manager.sns4.main.SMConstants;
import world.plus.manager.sns4.util.PlusHttpClient;
import world.plus.manager.sns4.util.TimeUtils;

public class GetFoursquareVenuesHelper {
	static String doIt(String query, String lat, String lon, String accessToken) {
		try {
			query = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		String ll = lat + "," + lon;

		String v = TimeUtils.getTodayDateFoursquare();

		String urlString = (query != null && !query.equals("")) ? SMConstants.FOURSQUARE_API_URL
				+ "/venues/search?ll="
				+ ll
				+ "&query="
				+ query
				+ "&oauth_token=" + accessToken + "&v=" + v
				: SMConstants.FOURSQUARE_API_URL + "/venues/search?ll="
						+ ll + "&oauth_token=" + accessToken + "&v=" + v;

		return PlusHttpClient.doGet(urlString);

	}

}
