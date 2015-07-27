package world.plus.manager.sns4.util;

import android.os.Handler;
import android.view.View;

/**
 * 이중 클릭 방지
 * 
 * @author user
 * 
 */
public class ClickGuard {
	public static void work(final View v) {
		v.setEnabled(false);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				v.setEnabled(true);
			}
		}, 1000);
	}
}
