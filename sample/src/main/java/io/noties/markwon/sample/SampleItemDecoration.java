package io.noties.markwon.sample;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

class SampleItemDecoration extends RecyclerView.ItemDecoration {

    private final Rect rect = new Rect();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int oddItemBackgroundColor;

    private final int bottomPadding;

    private final int dividerHeight;
    private final int dividerColor;

    SampleItemDecoration(
            @ColorInt int oddItemBackgroundColor,
            @Px int bottomPadding,
            @Px int dividerHeight,
            @ColorInt int dividerColor) {
        this.oddItemBackgroundColor = oddItemBackgroundColor;
        this.bottomPadding = bottomPadding;
        this.dividerHeight = dividerHeight;
        this.dividerColor = dividerColor;

        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        // if bottom < parent.getBottom() -> draw bottom background

        paint.setColor(dividerColor);

        View view;

        // we will use this flag afterwards (if we will have to draw bottom background)
        // so, if last item is even (no background) -> draw odd
        // if last item is odd -> draw no background
        //
        // let's start with true, so if we have no items no background will be drawn
        boolean isOdd = true;

        for (int i = 0, count = parent.getChildCount(); i < count; i++) {

            view = parent.getChildAt(i);
            isOdd = parent.getChildAdapterPosition(view) % 2 != 0;

            // odd
            if (isOdd) {
                rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                paint.setColor(oddItemBackgroundColor);
                c.drawRect(rect, paint);

                // set divider color back
                paint.setColor(dividerColor);
            }

            rect.set(0, view.getBottom(), c.getWidth(), view.getBottom() + dividerHeight);
            c.drawRect(rect, paint);
        }

        if (!isOdd && rect.bottom < parent.getBottom()) {

            paint.setColor(oddItemBackgroundColor);

            rect.set(0, rect.bottom, c.getWidth(), parent.getBottom());
            c.drawRect(rect, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        // divider to bottom
        // + {if last} -> bottomPadding

        final int position = parent.getChildAdapterPosition(view);

        final RecyclerView.Adapter<?> adapter = parent.getAdapter();
        final boolean isLast = adapter != null && position == adapter.getItemCount() - 1;

        final int bottom = isLast
                ? bottomPadding + dividerHeight
                : dividerHeight;

        outRect.set(0, 0, 0, bottom);
    }
}
