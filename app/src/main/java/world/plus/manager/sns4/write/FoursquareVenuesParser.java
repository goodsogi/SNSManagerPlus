package world.plus.manager.sns4.write;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class FoursquareVenuesParser {
	static ArrayList<FsqVenue> doIt(String response) {
		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
		try {
			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			JSONArray venues = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("venues");

			int length = venues.length();

			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject item = (JSONObject) venues.get(i);

					// Filter venues that visited before
					// Disable the below filter when find nearby with query
					// if (!item.has("beenHere"))
					// continue;
					FsqVenue venue = new FsqVenue();

					venue.id = item.optString("id");
					venue.name = item.optString("name");

					if (!item.getJSONArray("categories").isNull(0)) {
						JSONObject category = (JSONObject) item.getJSONArray(
								"categories").get(0);
						JSONObject icon = (JSONObject) category
								.getJSONObject("icon");
						// If bg_64 is not inserted between prefix and suffix,
						// it gives out error.
						venue.iconUrl = icon.optString("prefix") + "bg_64"
								+ icon.optString("suffix");
					} else {
						venue.iconUrl = "";
					}
					// venue.iconUrl = icon.getString("prefix")
					// + "64" +icon.getString("suffix");
					JSONObject location = (JSONObject) item
							.getJSONObject("location");

					Location loc = new Location(LocationManager.GPS_PROVIDER);

					loc.setLatitude(Double.valueOf(location.optString("lat")));
					loc.setLongitude(Double.valueOf(location.optString("lng")));

					venue.location = loc;
					// Some venues has no address. So check if it hass
					// address

					// if (location.has("address"))
					// venue.address = location.getString("address");
					// venue.distance = location.getInt("distance");
					// venue.herenow = item.getJSONObject("hereNow").getInt(
					// "count");
					// Json opt test
					venue.address = location.optString("address");
					venue.distance = location.optInt("distance");
					venue.herenow = item.getJSONObject("hereNow").optInt(
							"count");

					venueList.add(venue);
				}

				return venueList;

			}
		} catch (Exception ex) {
			Log.i("foursquare", ex.getMessage());
		}
		return null;
	}

}
