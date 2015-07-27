package world.plus.manager.sns4.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {
	static public String getTodayDateFoursquare() {

		long millis = System.currentTimeMillis();
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(millis);

		return sd.format(calendar.getTime());
	}

}
