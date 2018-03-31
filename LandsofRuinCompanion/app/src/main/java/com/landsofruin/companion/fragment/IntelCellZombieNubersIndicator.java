package com.landsofruin.companion.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.landsofruin.companion.utils.UIUtils;

/**
 * Created by juhani on 03/03/15.
 */
public class IntelCellZombieNubersIndicator extends View {
    private static final int MIN_RADIUS = 2;
    private static final int MAX_ZOMBIES = 15;
    private final Paint paint;
    private float minRadius = 0;
    private int count;

    public IntelCellZombieNubersIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        minRadius = UIUtils.convertDpToPixel(MIN_RADIUS, context);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
    }

    public void setZombieCount(int count) {
        this.count = count;

        if (this.count > MAX_ZOMBIES) {
            this.count = MAX_ZOMBIES;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.count <= 0) {
            return;
        }

        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        float radius = (getHeight() / 2) * (this.count / (float) MAX_ZOMBIES);
        if (radius < minRadius) {
            radius = minRadius;
        }

        canvas.drawCircle(x / 2, y / 2, radius, paint);
    }
}
