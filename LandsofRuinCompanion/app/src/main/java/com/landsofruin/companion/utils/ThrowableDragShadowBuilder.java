package com.landsofruin.companion.utils;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.landsofruin.gametracker.R;

public class ThrowableDragShadowBuilder extends View.DragShadowBuilder {
    private static Drawable shadow;

    public ThrowableDragShadowBuilder(View v) {
        super(v);

        shadow = v.getContext().getResources().getDrawable(R.drawable.throwable_drag_shadow);
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        int width = (int) (shadow.getIntrinsicWidth() * 1.5);
        int height = (int) (shadow.getIntrinsicHeight() * 1.5);

        shadow.setBounds(0, 0, width, height);

        size.set(width, height);
        touch.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
