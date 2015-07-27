package world.plus.manager.sns4.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.util.Log;

public class HttpUtils {
	public static String streamToString(HttpURLConnection httpUrlConnection,
			InputStream is) throws IOException {
		String string = "";

		if (is != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}

				reader.close();
			} finally {
				is.close();
				httpUrlConnection.disconnect();
			}

			string = stringBuilder.toString();
		}

		return string;
	}
	
	public static byte[] readBytes(String fileUri) throws IOException {
		// this dynamically extends to take the bytes you read
		File file = new File(fileUri);
		InputStream in = null;
		ByteArrayOutputStream byteBuffer = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));

			byteBuffer = new ByteArrayOutputStream();

			// this is storage overwritten on each iteration with bytes
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			// we need to know how may bytes were read to write them to the
			// byteBuffer
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
		} catch (Exception e) {
		}

		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}


}
