package world.plus.manager.sns4.write;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

public class UrlShortener {
	private String googUrl = "https://www.googleapis.com/urlshortener/v1/url?&key=";
	private String apikey = "AIzaSyD6bJy6xIRNjyq9JBzd81VqyMs12y-OgPI";
	private EditText mEditText;

	public UrlShortener() {
		this.googUrl += this.apikey;
	}

	public void shorten(EditText editText, String longUrl) {
		mEditText = editText;
		new GetShortUrlTask().execute(longUrl);
	}

	/**
	 * Get short url
	 * 
	 * @author user
	 * 
	 */
	class GetShortUrlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String longUrl = params[0];
			String shortUrl = "";

			try {
				URLConnection conn = new URL(googUrl).openConnection();
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write("{\"longUrl\":\"" + longUrl + "\"}");
				wr.flush();

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;

				while ((line = rd.readLine()) != null) {
					if (line.indexOf("id") > -1) {
						shortUrl = line.substring(8, line.length() - 2);
						break;
					}
				}

				wr.close();
				rd.close();
				return shortUrl;
			} catch (MalformedURLException ex) {
			} catch (IOException ex) {
				Log.i("shortUrl", ex.getMessage());
			}

			return null;
		}

		protected void onPostExecute(String shortUrl) {
			if (shortUrl == null)
				return;
			mEditText.append(shortUrl);
		}

	}
}