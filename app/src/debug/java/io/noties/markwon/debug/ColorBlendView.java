package io.noties.markwon.debug;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import io.noties.markwon.app.R;
import io.noties.markwon.utils.ColorUtils;

public class ColorBlendView extends View {

    private final Rect rect = new Rect();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int background;
    private int foreground;

    public ColorBlendView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorBlendView);
            try {
                background = array.getColor(R.styleable.ColorBlendView_cbv_background, 0);
                foreground = array.getColor(R.styleable.ColorBlendView_cbv_foreground, 0);
            } finally {
                array.recycle();
            }
        }

        paint.setStyle(Paint.Style.FILL);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int side = getWidth() / 11;

        rect.set(0, 0, side, getHeight());

        canvas.translate(getPaddingLeft(), 0F);

        for (int i = 0; i < 11; i++) {
            final float alpha = i / 10F;
            paint.setColor(ColorUtils.blend(foreground, background, alpha));
            canvas.drawRect(rect, paint);
            canvas.translate(side, 0F);
        }
    }
}
