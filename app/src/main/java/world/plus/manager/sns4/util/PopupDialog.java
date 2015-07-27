/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package world.plus.manager.sns4.util;

import world.plus.manager.sns4.R;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

public class PopupDialog extends Dialog {

	private TextView mTitle;
	private TextView mFirstMenu;
	private TextView mSecondMenu;
	private TextView mThirdMenu;

	public PopupDialog(Activity activity, int layout) {
		super(activity, R.style.CustomDialog);
		setOwnerActivity(activity);

		setCancelable(true);

		setContentView(layout);
		mTitle = (TextView) findViewById(R.id.popup_title);
		mFirstMenu = (TextView) findViewById(R.id.popup_menu1);
		mSecondMenu = (TextView) findViewById(R.id.popup_menu2);
		mThirdMenu = (TextView) findViewById(R.id.popup_menu3);

	}

	/**
	 * Set title of dialog
	 */
	@Override
	public final void setTitle(CharSequence title) {
		mTitle.setText(title);

	}

	/**
	 * Set title of dialog
	 */
	@Override
	public final void setTitle(int res) {
		mTitle.setText(res);
	}

	/**
	 * Add click listener to first menu
	 * 
	 * @param listener
	 */
	public void setFirstMenuListener(View.OnClickListener listener) {
		mFirstMenu.setOnClickListener(listener);
	}

	/**
	 * Add click listener to second menu
	 * 
	 * @param listener
	 */
	public void setSecondMenuListener(View.OnClickListener listener) {
		mSecondMenu.setOnClickListener(listener);
	}

	/**
	 * Add click listener to third menu
	 * 
	 * @param listener
	 */
	public void setThirdMenuListener(View.OnClickListener listener) {
		setThirdMenuVisible();
		mThirdMenu.setOnClickListener(listener);
	}

	/**
	 * Set third menu visible
	 */
	private void setThirdMenuVisible() {
		mThirdMenu.setVisibility(View.VISIBLE);
		View line = (View) findViewById(R.id.popup_menu3_line);
		line.setVisibility(View.VISIBLE);
	}

	/**
	 * Set text of first menu
	 * 
	 * @param text
	 */
	public void setFirstMenuText(String text) {
		mFirstMenu.setText(text);
	}

	/**
	 * Set text of first menu
	 * 
	 * @param text
	 */
	public void setFirstMenuText(int res) {
		mFirstMenu.setText(res);
	}

	/**
	 * Set text of second menu
	 * 
	 * @param text
	 */
	public void setSecondMenuText(String text) {
		mSecondMenu.setText(text);
	}

	/**
	 * Set text of second menu
	 * 
	 * @param text
	 */
	public void setSecondMenuText(int res) {
		mSecondMenu.setText(res);
	}

	/**
	 * Set text of third menu
	 * 
	 * @param text
	 */
	public void setThirdMenuText(String text) {
		mThirdMenu.setText(text);
	}

	/**
	 * Set text of third menu
	 * 
	 * @param text
	 */
	public void setThirdMenuText(int res) {
		mThirdMenu.setText(res);
	}

	/**
	 * Set cancelable of dialog
	 * 
	 * @param flag
	 * @return
	 */
	public PopupDialog cancelable(boolean flag) {
		setCancelable(flag);
		return this;
	}

}
