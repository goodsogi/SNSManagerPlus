package world.plus.manager.sns4.main;

import world.plus.manager.sns4.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends ArrayAdapter<String> {

	private String[] mValues;
	private LayoutInflater mInflater;
	// icons
	int[] icons = { R.drawable.post, R.drawable.connect_sns,
			R.drawable.manage_accounts, R.drawable.settings };

	public DrawerListAdapter(Context context, String[] values) {
		super(context, R.layout.drawer_list_item, values);
		mValues = values;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.drawer_list_item, parent,
				false);
		TextView label = (TextView) rowView
				.findViewById(R.id.drawer_item_label);
		label.setText(mValues[position]);
		ImageView icon = (ImageView) rowView
				.findViewById(R.id.drawer_item_icon);
		icon.setBackgroundResource(icons[position]);

		return rowView;
	}

}
