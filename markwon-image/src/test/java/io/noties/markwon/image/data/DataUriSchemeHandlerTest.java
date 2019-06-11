package io.noties.markwon.image.data;

import android.net.Uri;
import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import io.noties.markwon.image.ImageItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DataUriSchemeHandlerTest {

    private DataUriSchemeHandler handler;

    @Before
    public void before() {
        handler = DataUriSchemeHandler.create();
    }

    @Test
    public void scheme_specific_part_is_empty() {
        try {
            handler.handle("data:", Uri.parse("data:"));
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().contains("Invalid data-uri"));
        }
    }

    @Test
    public void data_uri_is_empty() {
        try {
            handler.handle("data:whatever", Uri.parse("data:whatever"));
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().contains("Invalid data-uri"));
        }
    }

    @Test
    public void no_data() {
        try {
            handler.handle("data:,", Uri.parse("data:,"));
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().contains("Decoding data-uri failed"));
        }
    }

    @Test
    public void correct() {

        final class Item {

            final String contentType;
            final String data;

            Item(String contentType, String data) {
                this.contentType = contentType;
                this.data = data;
            }
        }

        final Map<String, Item> expected = new HashMap<String, Item>() {{
            put("data:text/plain;,123", new Item("text/plain", "123"));
            put("data:image/svg+xml;base64,MTIz", new Item("image/svg+xml", "123"));
        }};

        for (Map.Entry<String, Item> entry : expected.entrySet()) {
            final ImageItem item = handler.handle(entry.getKey(), Uri.parse(entry.getKey()));
            assertNotNull(entry.getKey(), item);
            assertTrue(item.hasDecodingNeeded());

            final ImageItem.WithDecodingNeeded withDecodingNeeded = item.getAsWithDecodingNeeded();
            assertEquals(entry.getKey(), entry.getValue().contentType, withDecodingNeeded.contentType());
            assertEquals(entry.getKey(), entry.getValue().data, readStream(withDecodingNeeded.inputStream()));
        }
    }

    @NonNull
    private static String readStream(@NonNull InputStream stream) {
        try {
            final Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext()
                    ? scanner.next()
                    : "";
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}