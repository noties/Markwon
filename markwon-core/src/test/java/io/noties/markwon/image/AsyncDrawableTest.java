package io.noties.markwon.image;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AsyncDrawableTest {

    private ImageSizeResolver imageSizeResolver;

    @Before
    public void before() {
        imageSizeResolver = new ImageSizeResolverDef();
    }

    @Test
    public void no_dimensions_await() {
        // when drawable have no known dimensions yet, it will await for them

        final AsyncDrawable drawable = new AsyncDrawable("",
                mock(AsyncDrawableLoader.class),
                imageSizeResolver,
                new ImageSize(new ImageSize.Dimension(100.F, "%"), null));

        final Drawable result = new AbstractDrawable();
        result.setBounds(0, 0, 0, 0);

        assertFalse(drawable.hasResult());
        drawable.setResult(result);
        assertTrue(drawable.hasResult());

        assertTrue(result.getBounds().isEmpty());

        drawable.initWithKnownDimensions(100, 1);
        assertEquals(
                new Rect(0, 0, 100, 0),
                result.getBounds()
        );
    }

    @Test
    public void previous_result_detached() {
        // when result is present it will be detached (setCallback(null))

        final AsyncDrawable drawable = new AsyncDrawable("",
                mock(AsyncDrawableLoader.class),
                imageSizeResolver,
                null);

        drawable.setCallback2(mock(Drawable.Callback.class));
        drawable.initWithKnownDimensions(100, 1);

        final Drawable result1 = new AbstractDrawable();
        final Drawable result2 = new AbstractDrawable();

        drawable.setResult(result1);
        assertNotNull(result1.getCallback());
        drawable.setResult(result2);
        assertNull(result1.getCallback());
        assertNotNull(result2.getCallback());
    }

    @Test
    public void placeholder_no_bounds_no_intrinsic_bounds() {
        // when there is a placeholder and its
        // * bounds are empty
        // * intrinsic bounds are empty
        // AsyncDrawable.this must have any non-empty bounds (otherwise result won't be rendered,
        //  due to missing invalidation call)

        final Drawable placeholder = new AbstractDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return 0;
            }

            @Override
            public int getIntrinsicHeight() {
                return 0;
            }
        };

        assertTrue(placeholder.getBounds().isEmpty());

        final AsyncDrawableLoader loader = mock(AsyncDrawableLoader.class);
        when(loader.placeholder(any(AsyncDrawable.class))).thenReturn(placeholder);

        final AsyncDrawable drawable = new AsyncDrawable(
                "",
                loader,
                mock(ImageSizeResolver.class),
                null
        );

        final Rect bounds = drawable.getBounds();
        assertFalse(bounds.toShortString(), bounds.isEmpty());
        assertEquals(bounds.toShortString(), bounds, placeholder.getBounds());
    }

    @Test
    public void placeholder_no_bounds_has_intrinsic() {
        // placeholder has no bounds, but instead has intrinsic bounds

        final Drawable placeholder = new AbstractDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return 42;
            }

            @Override
            public int getIntrinsicHeight() {
                return 24;
            }
        };

        assertTrue(placeholder.getBounds().isEmpty());

        final AsyncDrawableLoader loader = mock(AsyncDrawableLoader.class);
        when(loader.placeholder(any(AsyncDrawable.class))).thenReturn(placeholder);

        final AsyncDrawable drawable = new AsyncDrawable(
                "",
                loader,
                mock(ImageSizeResolver.class),
                null
        );

        final Rect bounds = drawable.getBounds();
        assertFalse(bounds.isEmpty());
        assertEquals(0, bounds.left);
        assertEquals(42, bounds.right);
        assertEquals(0, bounds.top);
        assertEquals(24, bounds.bottom);

        assertEquals(bounds, placeholder.getBounds());
    }

    @Test
    public void placeholder_has_bounds() {

        final Rect rect = new Rect(0, 0, 12, 99);
        final Drawable placeholder = mock(Drawable.class);
        when(placeholder.getBounds()).thenReturn(rect);

        assertFalse(rect.isEmpty());

        final AsyncDrawableLoader loader = mock(AsyncDrawableLoader.class);
        when(loader.placeholder(any(AsyncDrawable.class))).thenReturn(placeholder);

        final AsyncDrawable drawable = new AsyncDrawable(
                "",
                loader,
                mock(ImageSizeResolver.class),
                null
        );

        final Rect bounds = drawable.getBounds();
        assertEquals(rect, bounds);

        verify(placeholder, times(1)).getBounds();
        verify(placeholder, never()).getIntrinsicWidth();
        verify(placeholder, never()).getIntrinsicHeight();
        verify(placeholder, never()).setBounds(any(Rect.class));
    }

    @Test
    public void no_placeholder_empty_bounds() {
        // when AsyncDrawable has no placeholder, then its bounds must be empty at the start

        final AsyncDrawable drawable = new AsyncDrawable(
                "",
                mock(AsyncDrawableLoader.class),
                mock(ImageSizeResolver.class),
                null
        );

        assertTrue(drawable.getBounds().isEmpty());
    }

    private static class AbstractDrawable extends Drawable {

        @Override
        public void draw(@NonNull Canvas canvas) {

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}