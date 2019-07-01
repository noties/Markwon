package io.noties.markwon.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.image.ImageSize;
import io.noties.markwon.html.CssInlineStyleParser;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageSizeParserImplTest {

    private static final float DELTA = 1e-7F;

    private ImageSizeParserImpl impl;

    @Before
    public void before() {
        impl = new ImageSizeParserImpl(CssInlineStyleParser.create());
    }

    @Test
    public void nothing() {
        Assert.assertNull(impl.parse(Collections.<String, String>emptyMap()));
    }

    @Test
    public void width_height_from_style() {

        final String style = "width: 123; height: 321";

        assertImageSize(
                new ImageSize(dimension(123, null), dimension(321, null)),
                impl.parse(Collections.singletonMap("style", style))
        );
    }

    @Test
    public void style_has_higher_priority_width() {

        // if property is found in styles, do not lookup raw attribute
        final Map<String, String> attributes = new HashMap<String, String>() {{
            put("style", "width: 43");
            put("width", "991");
        }};

        assertImageSize(
                new ImageSize(dimension(43, null), null),
                impl.parse(attributes)
        );
    }

    @Test
    public void style_has_higher_priority_height() {

        // if property is found in styles, do not lookup raw attribute
        final Map<String, String> attributes = new HashMap<String, String>() {{
            put("style", "height: 177");
            put("height", "8");
        }};

        assertImageSize(
                new ImageSize(null, dimension(177, null)),
                impl.parse(attributes)
        );
    }

    @Test
    public void width_style_height_attributes() {

        final Map<String, String> attributes = new HashMap<String, String>() {{
            put("style", "width: 99");
            put("height", "7");
        }};

        assertImageSize(
                new ImageSize(dimension(99, null), dimension(7, null)),
                impl.parse(attributes)
        );
    }

    @Test
    public void height_style_width_attributes() {

        final Map<String, String> attributes = new HashMap<String, String>() {{
            put("style", "height: 15");
            put("width", "88");
        }};

        assertImageSize(
                new ImageSize(dimension(88, null), dimension(15, null)),
                impl.parse(attributes)
        );
    }

    @Test
    public void non_empty_styles_width_height_attributes() {

        final Map<String, String> attributes = new HashMap<String, String>() {{
            put("style", "key1: value1; width0: 123; height0: 99");
            put("width", "40");
            put("height", "77");
        }};

        assertImageSize(
                new ImageSize(dimension(40, null), dimension(77, null)),
                impl.parse(attributes)
        );
    }

    @Test
    public void dimension_units() {

        final Map<String, ImageSize.Dimension> map = new HashMap<String, ImageSize.Dimension>() {{
            put("100", dimension(100, null));
            put("100%", dimension(100, "%"));
            put("1%", dimension(1, "%"));
            put("0.2em", dimension(0.2F, "em"));
            put("155px", dimension(155, "px"));
            put("67blah", dimension(67, "blah"));
            put("-1", dimension(-1, null));
            put("-0.01pt", dimension(-0.01F, "pt"));
        }};

        for (Map.Entry<String, ImageSize.Dimension> entry : map.entrySet()) {
            assertDimension(entry.getKey(), entry.getValue(), impl.dimension(entry.getKey()));
        }
    }

    @Test
    public void bad_dimension() {

        final String[] dimensions = {
                "calc(5px + 10rem)",
                "whataver6",
                "165 165",
                "!@#$%^&*(%"
        };

        for (String dimension : dimensions) {
            Assert.assertNull(dimension, impl.dimension(dimension));
        }
    }

    private static void assertImageSize(@Nullable ImageSize expected, @Nullable ImageSize actual) {
        if (expected == null) {
            Assert.assertNull(actual);
        } else {
            Assert.assertNotNull(actual);
            assertDimension("width", expected.width, actual.width);
            assertDimension("height", expected.height, actual.height);
        }
    }

    private static void assertDimension(
            @NonNull String name,
            @Nullable ImageSize.Dimension expected,
            @Nullable ImageSize.Dimension actual) {
        if (expected == null) {
            Assert.assertNull(name, actual);
        } else {
            Assert.assertNotNull(name, actual);
            Assert.assertEquals(name, expected.value, actual.value, DELTA);
            Assert.assertEquals(name, expected.unit, actual.unit);
        }
    }

    @NonNull
    private static ImageSize.Dimension dimension(float value, @Nullable String unit) {
        return new ImageSize.Dimension(value, unit);
    }
}