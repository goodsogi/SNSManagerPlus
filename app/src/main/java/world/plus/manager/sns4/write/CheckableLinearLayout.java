package world.plus.manager.sns4.write;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;
/**
 * Custom LinearLayout to implement selection on ListView
 * @author user
 *
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
	final String namespace = "http://schemas.android.com/apk/res/world.plus.manager.sns4";
	final String attr = "checkable";

	int mCheckableId;
	Checkable mCheckable;


	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCheckableId = attrs.getAttributeResourceValue(namespace, attr, 0);


	}

	@Override
	public void setChecked(boolean checked) {
		mCheckable = (Checkable) findViewById(mCheckableId);
		if(mCheckable == null)
			return;
		mCheckable.setChecked(checked);



	}

	@Override
	public boolean isChecked() {
		mCheckable = (Checkable) findViewById(mCheckableId);
		if(mCheckable == null)
			return false;
		return mCheckable.isChecked();


	}

	@Override
	public void toggle() {
		mCheckable = (Checkable) findViewById(mCheckableId);
		if(mCheckable == null)
			return;
		mCheckable.toggle();



	}

}
