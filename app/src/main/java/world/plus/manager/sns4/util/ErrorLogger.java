package world.plus.manager.sns4.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class ErrorLogger {

	public void write(String string) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"snsManagerPlus");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, "Log.txt");
			//Pass true to append
			FileWriter writer = new FileWriter(gpxfile,true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.newLine();
			bw.newLine();
			bw.append(string);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
