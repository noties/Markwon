package io.noties.markwon.sample.recycler;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class TriggerParentHotspot {

    public static void init(@NonNull final View parent, @NonNull final View view) {
        final Drawable background = parent.getBackground();
        if (background == null) {
            view.setBackground(null);
            return;
        }

        final Wrapper wrapper = ensureWrapper(view);
        wrapper.wrapped = background;

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final Point point = relativeTo(parent, view, POINT);
                wrapper.offsetHorizontal = point.x;
                wrapper.offsetVertical = point.y;
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private static final Point POINT = new Point();

    @NonNull
    private static Wrapper ensureWrapper(@NonNull View view) {
        final Drawable drawable = view.getBackground();
        if (!(drawable instanceof Wrapper)) {
            final Wrapper wrapper = new Wrapper();
            view.setBackground(wrapper);
            return wrapper;
        }
        return (Wrapper) drawable;
    }

    @NonNull
    private static Point relativeTo(@NonNull View parent, @NonNull View who, @NonNull Point point) {
        point.x += who.getLeft();
        point.y += who.getTop();
        if (who != parent
                && who.getParent() instanceof View) {
            relativeTo(parent, (View) who.getParent(), point);
        }
        return point;
    }

    private static class Wrapper extends Drawable {

        private Drawable wrapped;

        private int offsetHorizontal;
        private int offsetVertical;

        @Override
        public void draw(@NonNull Canvas canvas) {
            // no op
        }

        @Override
        public void setAlpha(int alpha) {
            // no op
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            // no op
        }

        @Override
        public int getOpacity() {
            if (wrapped == null) {
                return PixelFormat.TRANSPARENT;
            }
            return wrapped.getOpacity();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void setHotspot(float x, float y) {
            if (wrapped != null) {
                wrapped.setHotspot(x + offsetHorizontal, y + offsetVertical);
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void setHotspotBounds(int left, int top, int right, int bottom) {
            if (wrapped != null) {
                wrapped.setHotspotBounds(
                        left + offsetHorizontal,
                        top + offsetVertical,
                        right + offsetHorizontal,
                        bottom + offsetVertical
                );
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @Override
        public void getHotspotBounds(@NonNull Rect outRect) {
            if (wrapped != null) {
                wrapped.getHotspotBounds(outRect);
                if (!outRect.isEmpty()) {
                    outRect.set(
                            outRect.left - offsetHorizontal,
                            outRect.top - offsetVertical,
                            outRect.right - offsetHorizontal,
                            outRect.bottom - offsetVertical
                    );
                }
            }
        }

        @Override
        public boolean isStateful() {
            return wrapped != null && wrapped.isStateful();
        }

        @Override
        public boolean setState(@NonNull int[] stateSet) {
            return wrapped != null && wrapped.setState(stateSet);
        }

        @NonNull
        @Override
        public int[] getState() {
            return wrapped != null ? wrapped.getState() : StateSet.WILD_CARD;
        }
    }
}
