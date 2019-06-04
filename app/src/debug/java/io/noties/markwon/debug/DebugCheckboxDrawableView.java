package io.noties.markwon.debug;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import io.noties.markwon.app.R;
import io.noties.markwon.ext.tasklist.TaskListDrawable;

public class DebugCheckboxDrawableView extends View {

    private Drawable drawable;

    public DebugCheckboxDrawableView(Context context) {
        super(context);
        init(context, null);
    }

    public DebugCheckboxDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        int checkedColor = 0;
        int normalColor = 0;
        int checkMarkColor = 0;

        boolean checked = false;

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DebugCheckboxDrawableView);
            try {

                checkedColor = array.getColor(R.styleable.DebugCheckboxDrawableView_fcdv_checkedFillColor, checkedColor);
                normalColor = array.getColor(R.styleable.DebugCheckboxDrawableView_fcdv_normalOutlineColor, normalColor);
                checkMarkColor = array.getColor(R.styleable.DebugCheckboxDrawableView_fcdv_checkMarkColor, checkMarkColor);

                //noinspection ConstantConditions
                checked = array.getBoolean(R.styleable.DebugCheckboxDrawableView_fcdv_checked, checked);
            } finally {
                array.recycle();
            }
        }

        final TaskListDrawable drawable = new TaskListDrawable(checkedColor, normalColor, checkMarkColor);
        final int[] state;
        if (checked) {
            state = new int[]{android.R.attr.state_checked};
        } else {
            state = new int[0];
        }
        drawable.setState(state);

        this.drawable = drawable;

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }
}
