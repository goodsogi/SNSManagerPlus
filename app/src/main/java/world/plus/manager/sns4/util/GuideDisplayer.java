package world.plus.manager.sns4.util;

import world.plus.manager.sns4.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

public class GuideDisplayer {
	public static void show(final Activity activity, View rootView,
			SharedPreferences sharedPreferences, String key,
			boolean isFirstRun, int guideImageId) {
		if (isFirstRun) {
			ImageView guide = new ImageView(activity);
			guide.setImageResource(guideImageId);
			guide.setBackgroundColor(Color.BLACK);
			guide.getBackground().setAlpha(204);
			guide.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					// Apply animation to make image disappear smooth
					Animation anim = AnimationUtils.loadAnimation(activity,
							android.R.anim.fade_out);
					anim.setDuration(500);
					v.startAnimation(anim);
					anim.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							v.setVisibility(View.GONE);

							// edittext 에 포커스 줌
							EditText editText = (EditText) activity
									.findViewById(R.id.post_edittext);
							editText.setFocusableInTouchMode(true);
							editText.setFocusable(true);
						}
					});

					v.setVisibility(View.GONE);
				}
			});

			((ViewGroup) rootView.getParent()).addView(guide);
			Editor e = sharedPreferences.edit();
			e.putBoolean(key, false);
			e.commit();
		} else {
			// edittext 에 포커스 줌
			EditText editText = (EditText) activity
					.findViewById(R.id.post_edittext);
			editText.setFocusableInTouchMode(true);
			editText.setFocusable(true);

		}
	}

}
