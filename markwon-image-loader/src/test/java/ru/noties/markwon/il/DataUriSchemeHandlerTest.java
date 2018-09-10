package ru.noties.markwon.il;

import android.net.Uri;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        assertNull(handler.handle("data:", Uri.parse("data:")));
    }

    @Test
    public void data_uri_is_empty() {
        assertNull(handler.handle("data://whatever", Uri.parse("data://whatever")));
    }

    @Test
    public void no_data() {
        assertNull(handler.handle("data://,", Uri.parse("data://,")));
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
            put("data://text/plain;,123", new Item("text/plain", "123"));
            put("data://image/svg+xml;base64,MTIz", new Item("image/svg+xml", "123"));
        }};

        for (Map.Entry<String, Item> entry : expected.entrySet()) {
            final ImageItem item = handler.handle(entry.getKey(), Uri.parse(entry.getKey()));
            assertNotNull(entry.getKey(), item);
            assertEquals(entry.getKey(), entry.getValue().contentType, item.contentType());
            assertEquals(entry.getKey(), entry.getValue().data, readStream(item.inputStream()));
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