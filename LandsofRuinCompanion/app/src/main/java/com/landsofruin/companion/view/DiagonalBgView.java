package com.landsofruin.companion.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.landsofruin.gametracker.R;

/**
 * Created by juhani on 27/08/15.
 */
public class DiagonalBgView extends View {


    private Paint paint;
    private Path path;

    public DiagonalBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public DiagonalBgView(Context context) {
        super(context);
        init();
    }


    public DiagonalBgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.bg_colour_1));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawPath(path, paint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        path = new Path();

        path.reset();
        path.setFillType(Path.FillType.WINDING);
        path.moveTo(w, 0);
        path.lineTo(w, h);
        path.lineTo(0, h);
        path.lineTo(w, 0);
        path.setLastPoint(w, 0);
        path.close();


    }
}
