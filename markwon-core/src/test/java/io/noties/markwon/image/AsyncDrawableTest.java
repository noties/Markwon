package io.noties.markwon.image;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import static org.mockito.Mockito.mock;

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