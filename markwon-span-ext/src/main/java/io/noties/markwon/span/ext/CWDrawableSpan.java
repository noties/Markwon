package io.noties.markwon.span.ext;

import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

public class CWDrawableSpan extends DynamicDrawableSpan {
    private Drawable drawable;
    private int requiredWidth = 0;
    private int requiredHeight = 0;

    public CWDrawableSpan(Drawable drawable){
        this.drawable = drawable;
    }

    public CWDrawableSpan(Drawable drawable, int requiredWidth, int requiredHeight){
        this.requiredWidth = requiredWidth;
        this.requiredHeight = requiredHeight;
        this.drawable = drawable;
    }

    @Override
    public Drawable getDrawable() {
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        if (requiredHeight != 0 && requiredWidth != 0) {
            drawable.setBounds(0, 0, requiredWidth, requiredHeight);
        } else {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        return drawable;
    }
}
