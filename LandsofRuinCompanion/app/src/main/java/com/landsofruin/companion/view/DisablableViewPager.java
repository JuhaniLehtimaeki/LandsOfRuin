package com.landsofruin.companion.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DisablableViewPager extends ViewPager {
	private boolean isPagingEnabled;

	public DisablableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.isPagingEnabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.isPagingEnabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.isPagingEnabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	@Override
	public void setCurrentItem(int item) {
		if (this.isPagingEnabled) {
			super.setCurrentItem(item);
		}
	}

	public void setPagingEnabled(boolean b) {
		this.isPagingEnabled = b;
	}
}
