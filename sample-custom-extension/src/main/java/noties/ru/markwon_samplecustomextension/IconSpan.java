package noties.ru.markwon_samplecustomextension;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class IconSpan extends ReplacementSpan {

    @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface Alignment {
    }

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2; // will only center if drawable height is less than text line height


    private final Drawable drawable;

    private final int alignment;

    public IconSpan(@NonNull Drawable drawable, @Alignment int alignment) {
        this.drawable = drawable;
        this.alignment = alignment;
        if (drawable.getBounds().isEmpty()) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {

        final Rect rect = drawable.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        final int b = bottom - drawable.getBounds().bottom;

        final int save = canvas.save();
        try {
            final int translationY;
            if (ALIGN_CENTER == alignment) {
                translationY = b - ((bottom - top - drawable.getBounds().height()) / 2);
            } else if (ALIGN_BASELINE == alignment) {
                translationY = b - paint.getFontMetricsInt().descent;
            } else {
                translationY = b;
            }
            canvas.translate(x, translationY);
            drawable.draw(canvas);
        } finally {
            canvas.restoreToCount(save);
        }
    }
}
