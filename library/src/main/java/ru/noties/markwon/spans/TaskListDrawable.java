package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class TaskListDrawable extends Drawable {

    // represent ratios (not exact coordinates)
    private static final Point POINT_0 = new Point(2.75F / 18, 8.25F / 18);
    private static final Point POINT_1 = new Point(7.F / 18, 12.5F / 18);
    private static final Point POINT_2 = new Point(15.25F / 18, 4.75F / 18);

    private final int mCheckedFillColor;
    private final int mNormalOutlineColor;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mRectF = new RectF();

    private final Paint mCheckMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mCheckMarkPath = new Path();

    private boolean mIsChecked;

    // unfortunately we cannot rely on TextView to be LAYER_TYPE_SOFTWARE
    // if we could we would draw our mCheckMarkPath with PorterDuff.CLEAR
    public TaskListDrawable(@ColorInt int checkedFillColor, @ColorInt int normalOutlineColor, @ColorInt int checkMarkColor) {
        mCheckedFillColor = checkedFillColor;
        mNormalOutlineColor = normalOutlineColor;

        mCheckMarkPaint.setColor(checkMarkColor);
        mCheckMarkPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        // we should exclude stroke with from final bounds (half of the strokeWidth from all sides)

        // we should have square shape
        final float min = Math.min(bounds.width(), bounds.height());
        final float stroke = min / 8;

        final float side = min - stroke;
        mRectF.set(0, 0, side, side);

        mPaint.setStrokeWidth(stroke);
        mCheckMarkPaint.setStrokeWidth(stroke);

        mCheckMarkPath.reset();

        POINT_0.moveTo(mCheckMarkPath, side);
        POINT_1.lineTo(mCheckMarkPath, side);
        POINT_2.lineTo(mCheckMarkPath, side);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        final Paint.Style style;
        final int color;

        if (mIsChecked) {
            style = Paint.Style.FILL_AND_STROKE;
            color = mCheckedFillColor;
        } else {
            style = Paint.Style.STROKE;
            color = mNormalOutlineColor;
        }
        mPaint.setStyle(style);
        mPaint.setColor(color);

        final Rect bounds = getBounds();

        final float left = (bounds.width() - mRectF.width()) / 2;
        final float top = (bounds.height() - mRectF.height()) / 2;

        final float radius = mRectF.width() / 8;

        final int save = canvas.save();
        try {

            canvas.translate(left, top);

            canvas.drawRoundRect(mRectF, radius, radius, mPaint);

            if (mIsChecked) {
                canvas.drawPath(mCheckMarkPath, mCheckMarkPaint);
            }
        } finally {
            canvas.restoreToCount(save);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state) {

        final boolean checked;

        final int length = state != null
                ? state.length
                : 0;

        if (length > 0) {

            boolean inner = false;

            for (int i = 0; i < length; i++) {
                if (android.R.attr.state_checked == state[i]) {
                    inner = true;
                    break;
                }
            }
            checked = inner;
        } else {
            checked = false;
        }

        final boolean result = checked != mIsChecked;
        if (result) {
            invalidateSelf();
            mIsChecked = checked;
        }

        return result;
    }

    private static class Point {

        final float x;
        final float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        void moveTo(@NonNull Path path, float side) {
            path.moveTo(side * x, side * y);
        }

        void lineTo(@NonNull Path path, float side) {
            path.lineTo(side * x, side * y);
        }
    }
}
