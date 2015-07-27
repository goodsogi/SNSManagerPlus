package world.plus.manager.sns4.write;

import java.util.List;

import twitter4j.Place;
import twitter4j.ResponseList;
import world.plus.manager.sns4.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Twitter places list adapter
 * 
 * @author user
 * 
 */
public class TwitterPlacesListAdapter extends ArrayAdapter<Place> {

	static class ViewHolder {

		ImageView icon;
		TextView name;
		TextView checkbox;
	}

	private List<Place> mDatas;
	private LayoutInflater mInflater;

	public TwitterPlacesListAdapter(Context context,
			ResponseList<Place> response) {
		super(context, 0, response);
		this.mDatas = response;

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;
		if (row == null) {
			holder = new ViewHolder();

			row = mInflater.inflate(R.layout.venues_list_item, parent, false);
			holder.icon = (ImageView) row.findViewById(R.id.icon_venue);
			holder.name = (TextView) row.findViewById(R.id.name_venue);
			// holder.checkbox = (TextView)
			// row.findViewById(R.id.checkbox_venue);
			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		holder.icon.setImageResource(R.drawable.empty_photo);
		// !! Need to add category and apply textspan
		holder.name.setText(mDatas.get(position).getName());

		return row;
	}

}
