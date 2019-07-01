package io.noties.markwon.image;

import androidx.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Image;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.image.data.DataUriSchemeHandler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImagesPluginTest {

    private ImagesPlugin plugin;

    @Before
    public void before() {
        plugin = ImagesPlugin.create();
    }

    @Test
    public void build_state() {
        // it's not possible to mutate images-plugin after `configureConfiguration` call

        // validate that it doesn't throw here
        plugin.addSchemeHandler(DataUriSchemeHandler.create());

        // mark the state
        plugin.configureConfiguration(mock(MarkwonConfiguration.Builder.class));

        final class Throws {
            private void assertThrows(@NonNull Runnable action) {
                //noinspection CatchMayIgnoreException
                try {
                    action.run();
                    fail();
                } catch (Throwable t) {
                    assertTrue(t.getMessage(), t.getMessage().contains("ImagesPlugin has already been configured"));
                }
            }
        }
        final Throws check = new Throws();

        // executor-service
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.executorService(mock(ExecutorService.class));
            }
        });

        // add-scheme-handler
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.addSchemeHandler(mock(SchemeHandler.class));
            }
        });

        // add-media-decoder
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.addMediaDecoder(mock(MediaDecoder.class));
            }
        });

        // default-media-decoder
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.defaultMediaDecoder(mock(MediaDecoder.class));
            }
        });

        // remove-scheme-handler
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.removeSchemeHandler("mock");
            }
        });

        // remove-media-decoder
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.removeMediaDecoder("mock/type");
            }
        });

        // placeholder-provider
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.placeholderProvider(mock(ImagesPlugin.PlaceholderProvider.class));
            }
        });

        // error-handler
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.errorHandler(mock(ImagesPlugin.ErrorHandler.class));
            }
        });

        // final check if for actual `configureConfiguration` call (must be called only once)
        check.assertThrows(new Runnable() {
            @Override
            public void run() {
                plugin.configureConfiguration(mock(MarkwonConfiguration.Builder.class));
            }
        });
    }

    @Test
    public void image_span_factory_registered() {

        final MarkwonSpansFactory.Builder builder = mock(MarkwonSpansFactory.Builder.class);

        plugin.configureSpansFactory(builder);

        final ArgumentCaptor<SpanFactory> captor = ArgumentCaptor.forClass(SpanFactory.class);

        verify(builder, times(1))
                .setFactory(eq(Image.class), captor.capture());

        assertNotNull(captor.getValue());
    }

    @Test
    public void before_set_text() {
        // verify that AsyncDrawableScheduler is called

        final TextView textView = mock(TextView.class);

        plugin.beforeSetText(textView, mock(Spanned.class));

        verify(textView, times(1))
                .getTag(ArgumentMatchers.eq(R.id.markwon_drawables_scheduler_last_text_hashcode));
    }

    @Test
    public void after_set_text() {
        // verify that AsyncDrawableScheduler is called

        final TextView textView = mock(TextView.class);
        when(textView.getText()).thenReturn("some text");

        plugin.afterSetText(textView);

        verify(textView, times(1))
                .getTag(eq(R.id.markwon_drawables_scheduler_last_text_hashcode));
    }

    @Test
    public void methods_redirected_to_builder() {

        final AsyncDrawableLoaderBuilder builder = mock(AsyncDrawableLoaderBuilder.class);
        final ImagesPlugin plugin = new ImagesPlugin(builder);

        // executor service
        {
            final ExecutorService executorService = mock(ExecutorService.class);
            plugin.executorService(executorService);
            verify(builder, times(1)).executorService(eq(executorService));
        }

        // add scheme-handler
        {
            final SchemeHandler schemeHandler = mock(SchemeHandler.class);
            plugin.addSchemeHandler(schemeHandler);
            verify(builder, times(1)).addSchemeHandler(eq(schemeHandler));
        }

        // add media-decoder
        {
            final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
            plugin.addMediaDecoder(mediaDecoder);
            verify(builder, times(1)).addMediaDecoder(eq(mediaDecoder));
        }

        // default-media-decoder
        {
            final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
            plugin.defaultMediaDecoder(mediaDecoder);
            verify(builder, times(1)).defaultMediaDecoder(eq(mediaDecoder));
        }

        // remove scheme-handler
        {
            final String scheme = "yo";
            plugin.removeSchemeHandler(scheme);
            verify(builder, times(1)).removeSchemeHandler(eq(scheme));
        }

        // remove media-decoder
        {
            final String contentType = "fa/ke";
            plugin.removeMediaDecoder(contentType);
            verify(builder, times(1)).removeMediaDecoder(eq(contentType));
        }

        // placeholder provider
        {
            final ImagesPlugin.PlaceholderProvider placeholderProvider =
                    mock(ImagesPlugin.PlaceholderProvider.class);
            plugin.placeholderProvider(placeholderProvider);
            verify(builder, times(1)).placeholderProvider(eq(placeholderProvider));
        }

        // error-handler
        {
            final ImagesPlugin.ErrorHandler errorHandler = mock(ImagesPlugin.ErrorHandler.class);
            plugin.errorHandler(errorHandler);
            verify(builder, times(1)).errorHandler(eq(errorHandler));
        }

        verifyNoMoreInteractions(builder);
    }
}