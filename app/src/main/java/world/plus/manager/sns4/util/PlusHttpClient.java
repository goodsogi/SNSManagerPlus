package world.plus.manager.sns4.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.MultipartEntity;

public class PlusHttpClient {
	public static String doGet(String urlString) {

		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			urlConnection.setRequestMethod("GET");
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			urlConnection.setDoInput(true);

			urlConnection.connect();

			return HttpUtils.streamToString(urlConnection,
					urlConnection.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String doPostMultipartEntirty(String urlString,
			MultipartEntity entity) {

		// Set up request
		HttpURLConnection httpUrlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			httpUrlConnection = (HttpURLConnection) url.openConnection();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			httpUrlConnection.setReadTimeout(10000);
			httpUrlConnection.setConnectTimeout(15000);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setDoOutput(true);

			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.addRequestProperty("Content-length",
					entity.getContentLength() + "");
			httpUrlConnection.addRequestProperty(entity.getContentType()
					.getName(), entity.getContentType().getValue());

			OutputStream os = httpUrlConnection.getOutputStream();
			entity.writeTo(httpUrlConnection.getOutputStream());
			os.close();
			httpUrlConnection.connect();

			if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				return HttpUtils.streamToString(httpUrlConnection,
						httpUrlConnection.getInputStream());
			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return null;
	}
	
	public static String doPostJson(String urlString,
			String json) {
		HttpsURLConnection httpsUrlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			httpsUrlConnection = (HttpsURLConnection) url.openConnection();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			httpsUrlConnection.setReadTimeout(10000);
			httpsUrlConnection.setConnectTimeout(15000);
			httpsUrlConnection.setRequestMethod("POST");
			httpsUrlConnection.setUseCaches(false);
			httpsUrlConnection.setDoInput(true);
			httpsUrlConnection.setDoOutput(true);
			httpsUrlConnection.setRequestProperty("Content-Type",
					"application/json");
			//!! this can cause error
			httpsUrlConnection.setRequestProperty("x-li-format", "json");

			final byte[] bodyBytes = json.getBytes(
					"UTF-8");
			httpsUrlConnection
					.setFixedLengthStreamingMode(bodyBytes.length);

			final OutputStream outputStream = httpsUrlConnection
					.getOutputStream();
			outputStream.write(bodyBytes);
			outputStream.close();

			httpsUrlConnection.connect();

			if (httpsUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				return HttpUtils.streamToString(httpsUrlConnection,
						httpsUrlConnection.getInputStream());
			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {

		}

		return null;
		
	}

}
