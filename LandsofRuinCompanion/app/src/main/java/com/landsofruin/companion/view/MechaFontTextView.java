package com.landsofruin.companion.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by juhani on 06/10/15.
 */
public class MechaFontTextView extends TextView {

    private Typeface typeface;

    public MechaFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMechaFont();
    }

    public MechaFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMechaFont();
    }

    public MechaFontTextView(Context context) {
        super(context);
        setMechaFont();
    }

    public void setMechaFont() {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Mecha 08.otf");
        }
        setTypeface(typeface);
    }

}