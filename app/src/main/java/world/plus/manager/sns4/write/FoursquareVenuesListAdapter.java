package world.plus.manager.sns4.write;

import java.util.List;

import world.plus.manager.sns4.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Foursquare venues list adapter
 * 
 * @author user
 * 
 */
public class FoursquareVenuesListAdapter extends ArrayAdapter<FsqVenue> {
	private DisplayImageOptions mOptions;
	private ImageLoader mImageLoader;

	static class ViewHolder {

		ImageView icon;
		TextView name;
		TextView checkbox;
	}

	private List<FsqVenue> mDatas;
	private LayoutInflater mInflater;

	public FoursquareVenuesListAdapter(Context context, List<FsqVenue> objects) {
		super(context, 0, objects);
		this.mDatas = objects;
		mImageLoader = ImageLoader.getInstance();
		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
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

		holder.icon.setImageBitmap(null);
		if (mDatas.get(position).iconUrl == null
				|| mDatas.get(position).iconUrl.equals("")) {
			holder.icon.setImageResource(R.drawable.empty_photo);
		} else {
			mImageLoader.displayImage(mDatas.get(position).iconUrl,
					holder.icon, mOptions, null);

		}
		// !! Need to add category and apply textspan
		holder.name.setText(mDatas.get(position).name);

		return row;
	}

}
