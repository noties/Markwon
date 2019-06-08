package io.noties.markwon;

import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.noties.markwon.core.MarkwonTheme;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonBuilderImplTest {

    @Test
    public void no_plugins_added_throws() {
        // there is no sense in having an instance with no plugins registered

        try {
            new MarkwonBuilderImpl(RuntimeEnvironment.application).build();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), e.getMessage(), containsString("No plugins were added"));
        }
    }

    @Test
    public void plugin_configured() {
        // verify that all configuration methods (applicable) are called

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);

        final MarkwonBuilderImpl impl = new MarkwonBuilderImpl(RuntimeEnvironment.application);
        impl.usePlugin(plugin).build();

        verify(plugin, times(1)).configure(any(MarkwonPlugin.Registry.class));

        verify(plugin, times(1)).configureParser(any(Parser.Builder.class));
        verify(plugin, times(1)).configureTheme(any(MarkwonTheme.Builder.class));
        verify(plugin, times(1)).configureConfiguration(any(MarkwonConfiguration.Builder.class));
        verify(plugin, times(1)).configureVisitor(any(MarkwonVisitor.Builder.class));
        verify(plugin, times(1)).configureSpansFactory(any(MarkwonSpansFactory.Builder.class));

        // note, no render props -> they must be configured on render stage
        verify(plugin, times(0)).processMarkdown(anyString());
        verify(plugin, times(0)).beforeRender(any(Node.class));
        verify(plugin, times(0)).afterRender(any(Node.class), any(MarkwonVisitor.class));
        verify(plugin, times(0)).beforeSetText(any(TextView.class), any(Spanned.class));
        verify(plugin, times(0)).afterSetText(any(TextView.class));
    }
}