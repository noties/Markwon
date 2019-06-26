package io.noties.markwon.image;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static io.noties.markwon.image.ImageSizeResolverDef.UNIT_EM;
import static io.noties.markwon.image.ImageSizeResolverDef.UNIT_PERCENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageSizeResolverDefTest {

    private ImageSizeResolverDef def;

    @Before
    public void before() {
        def = new ImageSizeResolverDef();
    }

    @Test
    public void correct_redirect() {
        // @since 4.0.0 the main method is changed to accept AsyncDrawable

        final ImageSizeResolverDef def = mock(ImageSizeResolverDef.class, Mockito.CALLS_REAL_METHODS);
        final AsyncDrawable drawable = mock(AsyncDrawable.class);

        final ImageSize imageSize = mock(ImageSize.class);
        final Drawable result = mock(Drawable.class);
        final Rect rect = mock(Rect.class);
        when(result.getBounds()).thenReturn(rect);

        when(drawable.getImageSize()).thenReturn(imageSize);
        when(drawable.getResult()).thenReturn(result);
        when(drawable.getLastKnownCanvasWidth()).thenReturn(111);
        when(drawable.getLastKnowTextSize()).thenReturn(24.0F);

        def.resolveImageSize(drawable);

        final ArgumentCaptor<ImageSize> imageSizeArgumentCaptor = ArgumentCaptor.forClass(ImageSize.class);
        final ArgumentCaptor<Rect> rectArgumentCaptor = ArgumentCaptor.forClass(Rect.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<Float> floatArgumentCaptor = ArgumentCaptor.forClass(Float.class);

        verify(def).resolveImageSize(
                imageSizeArgumentCaptor.capture(),
                rectArgumentCaptor.capture(),
                integerArgumentCaptor.capture(),
                floatArgumentCaptor.capture());

        assertEquals(imageSize, imageSizeArgumentCaptor.getValue());
        assertEquals(rect, rectArgumentCaptor.getValue());
        assertEquals((Integer) 111, integerArgumentCaptor.getValue());
        assertEquals((Float) 24.0F, floatArgumentCaptor.getValue());
    }

    @Test
    public void no_image_size() {
        // no image size returns image original bounds
        final Rect rect = new Rect(0, 0, 15, 43);
        assertEquals(rect, def.resolveImageSize(null, rect, 15, Float.NaN));
    }

    @Test
    public void no_image_size_width_greater_than_canvas() {
        // image must be scaled (with ratio) wo fit canvas width
        final Rect rect = new Rect(0, 0, 10, 20);
        assertEquals(
                new Rect(0, 0, 8, 16),
                def.resolveImageSize(
                        null,
                        rect,
                        8,
                        Float.NaN
                )
        );
    }

    @Test
    public void height_percent_not_used() {
        final Rect rect = new Rect(1, 2, 3, 4);
        assertEquals(
                rect,
                def.resolveImageSize(
                        new ImageSize(null, new ImageSize.Dimension(100.F, UNIT_PERCENT)),
                        rect,
                        -1,
                        Float.NaN
                )
        );
    }

    @Test
    public void width_percent_scales_keeps_ratio() {
        final Rect rect = new Rect(0, 0, 10, 20);
        assertEquals(
                new Rect(0, 0, 50, 100),
                def.resolveImageSize(
                        new ImageSize(new ImageSize.Dimension(50.F, UNIT_PERCENT), null),
                        rect,
                        100,
                        Float.NaN
                )
        );
    }

    @Test
    public void unknown_dimension_considered_absolute() {
        final Rect rect = new Rect(0, 0, 22, 33);
        assertEquals(
                new Rect(0, 0, 7, 9),
                def.resolveImageSize(
                        new ImageSize(new ImageSize.Dimension(7, "width"), new ImageSize.Dimension(9, "height")),
                        rect,
                        90,
                        Float.NaN
                )
        );
    }

    @Test
    public void width_height_text_size_relative() {
        final Rect rect = new Rect(0, 0, 100, 200);
        assertEquals(
                new Rect(0, 0, 20, 40),
                def.resolveImageSize(
                        new ImageSize(new ImageSize.Dimension(2.f, UNIT_EM), new ImageSize.Dimension(4.F, UNIT_EM)),
                        rect,
                        999,
                        10.F
                )
        );
    }

    @Test
    public void width_text_size_relative_height_keeps_ratio() {
        final Rect rect = new Rect(0, 0, 15, 30);
        assertEquals(
                new Rect(0, 0, 10, 20),
                def.resolveImageSize(
                        new ImageSize(new ImageSize.Dimension(1.F, UNIT_EM), null),
                        rect,
                        42,
                        10.F
                )
        );
    }

    @Test
    public void absolute_height_keeps_width_ratio() {
        final Rect rect = new Rect(0, 0, 50, 25);
        assertEquals(
                new Rect(0, 0, 100, 50),
                def.resolveImageSize(
                        new ImageSize(null, new ImageSize.Dimension(50, "px")),
                        rect,
                        200,
                        Float.NaN
                )
        );
    }

    @Test
    public void relative_text_size_height_keeps_width_ratio() {
        final Rect rect = new Rect(0, 0, 4, 12);
        assertEquals(
                new Rect(0, 0, 10, 30),
                def.resolveImageSize(
                        new ImageSize(null, new ImageSize.Dimension(3.F, UNIT_EM)),
                        rect,
                        40,
                        10.F
                )
        );
    }
}