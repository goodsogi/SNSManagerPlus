package world.plus.manager.sns4.manage_account;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

/**
 * Custom spinner that can select same item
 * 
 * @author user
 * 
 */
public class SpinnerTrigger extends Spinner {

	public SpinnerTrigger(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SpinnerTrigger(Context context, AttributeSet set) {
		super(context, set);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setSelection(int position, boolean animate) {
		ignoreOldSelectionByReflection();
		super.setSelection(position, animate);
	}

	private void ignoreOldSelectionByReflection() {
		try {
			Class<?> c = this.getClass().getSuperclass().getSuperclass()
					.getSuperclass();
			Field reqField = c.getDeclaredField("mOldSelectedPosition");
			reqField.setAccessible(true);
			reqField.setInt(this, -1);
		} catch (Exception e) {
			Log.d("Exception Private", "ex", e);
			// TODO: handle exception
		}
	}

	@Override
	public void setSelection(int position) {
		ignoreOldSelectionByReflection();
		super.setSelection(position);
	}

}
