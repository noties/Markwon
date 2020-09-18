package io.noties.markwon.ext.tables;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * @since 4.6.0
 */
public class TableAwareMovementMethod implements MovementMethod {

    @NonNull
    public static TableAwareMovementMethod wrap(@NonNull MovementMethod movementMethod) {
        return new TableAwareMovementMethod(movementMethod);
    }

    /**
     * Wraps LinkMovementMethod
     */
    @NonNull
    public static TableAwareMovementMethod create() {
        return new TableAwareMovementMethod(LinkMovementMethod.getInstance());
    }

    public static boolean handleTableRowTouchEvent(
            @NonNull TextView widget,
            @NonNull Spannable buffer,
            @NonNull MotionEvent event) {
        // handle only action up (originally action down is used in order to handle selection,
        //  which tables do no have)
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();
        x += widget.getScrollX();
        y += widget.getScrollY();

        final Layout layout = widget.getLayout();
        final int line = layout.getLineForVertical(y);
        final int off = layout.getOffsetForHorizontal(line, x);

        final TableRowSpan[] spans = buffer.getSpans(off, off, TableRowSpan.class);
        if (spans.length == 0) {
            return false;
        }

        final TableRowSpan span = spans[0];

        // okay, we can calculate the x to obtain span, but what about y?
        final Layout rowLayout = span.findLayoutForHorizontalOffset(x);
        if (rowLayout != null) {
            // line top as basis
            final int rowY = layout.getLineTop(line);
            final int rowLine = rowLayout.getLineForVertical(y - rowY);
            final int rowOffset = rowLayout.getOffsetForHorizontal(rowLine, x % span.cellWidth());
            final ClickableSpan[] rowClickableSpans = ((Spanned) rowLayout.getText())
                    .getSpans(rowOffset, rowOffset, ClickableSpan.class);
            if (rowClickableSpans.length > 0) {
                rowClickableSpans[0].onClick(widget);
                return true;
            }
        }

        return false;
    }

    private final MovementMethod wrapped;

    public TableAwareMovementMethod(@NonNull MovementMethod wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        wrapped.initialize(widget, text);
    }

    @Override
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return wrapped.onKeyDown(widget, text, keyCode, event);
    }

    @Override
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return wrapped.onKeyUp(widget, text, keyCode, event);
    }

    @Override
    public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
        return wrapped.onKeyOther(view, text, event);
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int direction) {
        wrapped.onTakeFocus(widget, text, direction);
    }

    @Override
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return wrapped.onTrackballEvent(widget, text, event);
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        // let wrapped handle first, then if super handles nothing, search for table row spans
        return wrapped.onTouchEvent(widget, buffer, event)
                || handleTableRowTouchEvent(widget, buffer, event);
    }

    @Override
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        return wrapped.onGenericMotionEvent(widget, text, event);
    }

    @Override
    public boolean canSelectArbitrarily() {
        return wrapped.canSelectArbitrarily();
    }
}
