package ru.noties.markwon.test;

import org.junit.Test;

import java.util.Map;

import ru.noties.markwon.test.TestSpan.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

public class TestSpanTest {

    @Test
    public void args_not_event_throws() {
        try {
            args("key");
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Supplied key-values array must contain "));
        }
    }

    @Test
    public void args_key_not_string_throws() {
        try {
            args("key", 1, 2, 3);
            fail();
        } catch (ClassCastException e) {
            assertTrue(true);
        }
    }

    @Test
    public void args_correct() {

        final Map<String, Object> args = args("key1", true, "key2", 4);

        assertEquals(2, args.size());
        assertEquals(true, args.get("key1"));
        assertEquals(4, args.get("key2"));
    }

    @Test
    public void empty_document() {
        final Document document = document();
        assertEquals(0, document.children().size());
        assertEquals("", document.wholeText());
    }

    @Test
    public void document_single_text_child() {
        final Document document = document(text("Text"));
        assertEquals(1, document.children().size());
        assertEquals("Text", document.wholeText());
    }

    @Test
    public void document_single_span_child() {
        final Document document = document(span("span", text("TextInSpan")));
        assertEquals(1, document.children().size());
        assertTrue(document.children().get(0) instanceof TestSpan.Span);
        assertEquals("TextInSpan", document.wholeText());
    }
}
