package io.noties.markwon.sample.basicplugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.debug.Debug;

@SuppressLint("AppCompatCustomView")
public class CodeTextView extends TextView {

    static class CodeSpan {
    }

    private int paddingHorizontal;
    private int paddingVertical;

    private float cornerRadius;
    private float strokeWidth;
    private int strokeColor;
    private int backgroundColor;

    public CodeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CodeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        paint.setColor(0xFFff0000);
        paint.setStyle(Paint.Style.FILL);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        final Layout layout = getLayout();
        if (layout != null) {
            draw(this, canvas, layout);
        }
        super.onDraw(canvas);
    }

    private void draw(
            @NonNull View view,
            @NonNull Canvas canvas,
            @NonNull Layout layout
    ) {

        final CharSequence cs = layout.getText();
        if (!(cs instanceof Spanned)) {
            return;
        }
        final Spanned spanned = (Spanned) cs;

        final int save = canvas.save();
        try {
            canvas.translate(view.getPaddingLeft(), view.getPaddingTop());

            // TODO: block?
            // TODO: we must remove _original_ spans
            // TODO: cache (attach a listener?)
            // TODO: editor?

            final CodeSpan[] spans = spanned.getSpans(0, spanned.length(), CodeSpan.class);
            if (spans != null && spans.length > 0) {
                for (CodeSpan span : spans) {

                    final int startOffset = spanned.getSpanStart(span);
                    final int endOffset = spanned.getSpanEnd(span);

                    final int startLine = layout.getLineForOffset(startOffset);
                    final int endLine = layout.getLineForOffset(endOffset);

                    // do we need to round them?
                    final float left = layout.getPrimaryHorizontal(startOffset)
                            + (-1 * layout.getParagraphDirection(startLine) * paddingHorizontal);

                    final float right = layout.getPrimaryHorizontal(endOffset)
                            + (layout.getParagraphDirection(endLine) * paddingHorizontal);

                    final float top = getLineTop(layout, startLine, paddingVertical);
                    final float bottom = getLineBottom(layout, endLine, paddingVertical);

                    Debug.i(new RectF(left, top, right, bottom).toShortString());

                    if (startLine == endLine) {
                        canvas.drawRect(left, top, right, bottom, paint);
                    } else {
                        // draw first line (start until the lineEnd)
                        // draw everything in-between (startLine - endLine)
                        // draw last line (lineStart until the end

                        canvas.drawRect(
                                left,
                                top,
                                layout.getLineRight(startLine),
                                getLineBottom(layout, startLine, paddingVertical),
                                paint
                        );

                        for (int line = startLine + 1; line < endLine; line++) {
                            canvas.drawRect(
                                    layout.getLineLeft(line),
                                    getLineTop(layout, line, paddingVertical),
                                    layout.getLineRight(line),
                                    getLineBottom(layout, line, paddingVertical),
                                    paint
                            );
                        }

                        canvas.drawRect(
                                layout.getLineLeft(endLine),
                                getLineTop(layout, endLine, paddingVertical),
                                right,
                                getLineBottom(layout, endLine, paddingVertical),
                                paint
                        );
                    }
                }
            }
        } finally {
            canvas.restoreToCount(save);
        }
    }

    private static float getLineTop(@NonNull Layout layout, int line, float padding) {
        float value = layout.getLineTop(line) - padding;
        if (line == 0) {
            value -= layout.getTopPadding();
        }
        return value;
    }

    private static float getLineBottom(@NonNull Layout layout, int line, float padding) {
        float value = getLineBottomWithoutSpacing(layout, line) - padding;
        if (line == (layout.getLineCount() - 1)) {
            value -= layout.getBottomPadding();
        }
        return value;
    }

    private static float getLineBottomWithoutSpacing(@NonNull Layout layout, int line) {
        final float value = layout.getLineBottom(line);

        final boolean isLastLineSpacingNotAdded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        final boolean isLastLine = line == (layout.getLineCount() - 1);

        final float lineBottomWithoutSpacing;

        final float lineSpacingExtra = layout.getSpacingAdd();
        final float lineSpacingMultiplier = layout.getSpacingMultiplier();

        final boolean hasLineSpacing = Float.compare(lineSpacingExtra, .0F) != 0
                || Float.compare(lineSpacingMultiplier, 1F) != 0;

        if (!hasLineSpacing || isLastLine && isLastLineSpacingNotAdded) {
            lineBottomWithoutSpacing = value;
        } else {
            final float extra;
            if (Float.compare(lineSpacingMultiplier, 1F) != 0) {
                final float lineHeight = getLineHeight(layout, line);
                extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier;
            } else {
                extra = lineSpacingExtra;
            }
            lineBottomWithoutSpacing = value - extra;
        }

        return lineBottomWithoutSpacing;
    }

    private static float getLineHeight(@NonNull Layout layout, int line) {
        return layout.getLineTop(line + 1) - layout.getLineTop(line);
    }
}
