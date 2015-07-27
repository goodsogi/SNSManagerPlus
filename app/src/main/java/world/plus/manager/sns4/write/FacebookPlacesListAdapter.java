package world.plus.manager.sns4.write;

import java.util.List;

import world.plus.manager.sns4.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphPlace;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Facebook places list adapter
 * 
 * @author user
 * 
 */
public class FacebookPlacesListAdapter extends ArrayAdapter<GraphPlace> {
	private DisplayImageOptions mOptions;
	private ImageLoader mImageLoader;

	static class ViewHolder {

		ImageView icon;
		TextView name;
		CheckBox checkbox;
	}

	private List<GraphPlace> mDatas;
	private LayoutInflater mInflater;

	public FacebookPlacesListAdapter(Context context, List<GraphPlace> places) {
		super(context, 0, places);
		this.mDatas = places;
		mImageLoader = ImageLoader.getInstance();
		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;
		if (row == null) {
			holder = new ViewHolder();

			row = mInflater.inflate(R.layout.venues_list_item, parent, false);
			holder.icon = (ImageView) row.findViewById(R.id.icon_venue);
			holder.name = (TextView) row.findViewById(R.id.name_venue);

			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		String iconUrl = "http://graph.facebook.com/"
				+ mDatas.get(position).getId() + "/picture";

		holder.icon.setImageBitmap(null);
		if (iconUrl == null || iconUrl.equals("")) {
			holder.icon.setImageResource(R.drawable.empty_photo);
		} else {
			mImageLoader.displayImage(iconUrl, holder.icon, mOptions, null);

		}
		// !! Need to add category and apply textspan
		holder.name.setText(mDatas.get(position).getName());

		return row;
	}

}
