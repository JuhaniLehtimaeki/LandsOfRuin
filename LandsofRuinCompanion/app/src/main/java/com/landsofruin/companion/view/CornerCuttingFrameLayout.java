package com.landsofruin.companion.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.landsofruin.gametracker.R;

/**
 * Created by juhani on 27/08/15.
 */
public class CornerCuttingFrameLayout extends FrameLayout {


    private float cornerCut1 = 0;
    private float cornerCut2 = 0;
    private float cornerCut3 = 0;
    private float cornerCut4 = 0;


    private Path path;

    public CornerCuttingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CornerCutterView);
        cornerCut1 = arr.getDimension(R.styleable.CornerCutterView_corner1, 0f);
        cornerCut2 = arr.getDimension(R.styleable.CornerCutterView_corner2, 0f);
        cornerCut3 = arr.getDimension(R.styleable.CornerCutterView_corner3, 0f);
        cornerCut4 = arr.getDimension(R.styleable.CornerCutterView_corner4, 0f);

        arr.recycle();
    }


    public CornerCuttingFrameLayout(Context context) {
        super(context);
    }


    public CornerCuttingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CornerCutterView);
        cornerCut1 = arr.getDimension(R.styleable.CornerCutterView_corner1, 0f);
        cornerCut2 = arr.getDimension(R.styleable.CornerCutterView_corner2, 0f);
        cornerCut3 = arr.getDimension(R.styleable.CornerCutterView_corner3, 0f);
        cornerCut4 = arr.getDimension(R.styleable.CornerCutterView_corner4, 0f);

        arr.recycle();
    }

//    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int size = width > height ? height : width;
//        setMeasuredDimension(size, size);
//    }

    @Override
    public void draw(Canvas canvas) {
        try {
            canvas.save();
            canvas.clipPath(path);
            super.draw(canvas);
            canvas.restore();
        }catch (Exception e){
            super.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        path = new Path();

        path.reset();
        path.setFillType(Path.FillType.WINDING);

        path.moveTo(cornerCut1, 0);
        path.lineTo(w - cornerCut2, 0);
        path.lineTo(w, cornerCut2);
        path.lineTo(w, h - cornerCut3);
        path.lineTo(w - cornerCut3, h);
        path.lineTo(cornerCut4, h);
        path.lineTo(0, h - cornerCut4);
        path.lineTo(0, cornerCut1);
        path.lineTo(cornerCut1, 0);

        path.close();


    }

}
