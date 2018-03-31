package com.landsofruin.companion.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.landsofruin.companion.utils.UIUtils;

/**
 * Created by juhani on 27/08/15.
 */
public class ClipedCommandPanelFrameLayout extends FrameLayout {
    private static final int CORNER_CUT_X = 34; //dp
    private static final int CORNER_CUT_Y = 33; //dp

    private float cornerCutX = 0;
    private float cornerCutY = 0;

    private Path path;

    public ClipedCommandPanelFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ClipedCommandPanelFrameLayout(Context context) {
        super(context);
    }


    public ClipedCommandPanelFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            canvas.save();
            canvas.clipPath(path);
            super.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
            super.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        cornerCutX = UIUtils.convertDpToPixel(CORNER_CUT_X, getContext());
        cornerCutY = UIUtils.convertDpToPixel(CORNER_CUT_Y, getContext());


        path = new Path();

        path.reset();
        path.setFillType(Path.FillType.WINDING);
        path.moveTo(0, 0);
        path.lineTo(w, 0);
        path.lineTo(w, h - cornerCutY);
        path.lineTo(w - cornerCutX, h);
        path.lineTo(0, h);
        path.lineTo(0, 0);
        path.close();


    }

}
