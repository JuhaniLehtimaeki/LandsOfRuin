package com.landsofruin.gametracker.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SimpleBindingAdapter<ViewClass extends View> extends
		BaseAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public final ViewClass getView(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(parent);
		}
		bindView(position, (ViewClass) convertView);
		return (ViewClass) convertView;
	}

	/** Create a new instance of a view */
	public abstract ViewClass newView(ViewGroup parent);

	/** Bind the data for the specified {@code position} to the {@code view}. */
	public abstract void bindView(int position, ViewClass view);

}
