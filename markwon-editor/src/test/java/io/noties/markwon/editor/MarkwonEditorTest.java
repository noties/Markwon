package io.noties.markwon.editor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor.Builder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonEditorTest {

    @Test
    public void builder_no_config() {
        // must create a default instance without exceptions

        try {
            new Builder(mock(Markwon.class)).build();
            assertTrue(true);
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void builder_with_edit_spans_but_no_handler() {
        // if edit spans are specified, but no edit span handler is present -> exception is thrown

        try {
            //noinspection unchecked
            new Builder(mock(Markwon.class))
                    .includeEditSpan(Object.class, mock(MarkwonEditor.EditSpanFactory.class))
                    .build();
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("There is no need to include edit spans "));
        }
    }
}