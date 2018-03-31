package com.landsofruin.companion.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.PointState;

public class MapBadgeLayout extends FrameLayout {

	private MapView map;

	public MapBadgeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMap(MapView map) {
		this.map = map;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);

			PointState center = ((CharacterState) v.getTag()).getCenterPoint();

			if(center == null){
				v.setVisibility(View.GONE);
				continue;
			}
			
			int x = (int) (center.x * map.getZoomScale());
			int y = (int) (center.y * map.getZoomScale());

			x = x - map.getScrollX();
			y = y - map.getScrollY();
			
			if(map.isFlipped()){

				v.setRotation(180);

			}
			

			v.layout(x, y, x + v.getWidth(), y + v.getHeight());
		}

	}

}
