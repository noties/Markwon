package io.noties.markwon.image;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

import io.noties.markwon.image.data.DataUriSchemeHandler;
import io.noties.markwon.image.network.NetworkSchemeHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AsyncDrawableLoaderBuilderTest {

    private AsyncDrawableLoaderBuilder builder;

    @Before
    public void before() {
        builder = new AsyncDrawableLoaderBuilder();
    }

    @Test
    public void default_scheme_handlers() {
        // builder adds default data-uri and network scheme-handlers

        final String[] registered = {
                DataUriSchemeHandler.SCHEME,
                NetworkSchemeHandler.SCHEME_HTTP,
                NetworkSchemeHandler.SCHEME_HTTPS
        };

        for (String scheme : registered) {
            assertNotNull(scheme, builder.schemeHandlers.get(scheme));
        }
    }

    @Test
    public void built_flag() {
        // isBuilt flag must be set after `build` method call

        assertFalse(builder.isBuilt);

        builder.build();

        assertTrue(builder.isBuilt);
    }

    @Test
    public void defaults_initialized() {
        // default-media-decoder and executor-service must be initialized

        assertNotNull(builder.defaultMediaDecoder);
        assertNull(builder.executorService);

        builder.build();

        assertNotNull(builder.defaultMediaDecoder);
        assertNotNull(builder.executorService);
    }

    @Test
    public void default_media_decoder_removed() {
        // we init default-media-decoder right away, but further it can be removed (nulled-out)

        assertNotNull(builder.defaultMediaDecoder);

        builder.defaultMediaDecoder(null);
        builder.build();

        assertNull(builder.defaultMediaDecoder);
    }

    @Test
    public void executor() {
        // supplied executor-service must be used

        assertNull(builder.executorService);

        final ExecutorService service = mock(ExecutorService.class);
        builder.executorService(service);

        builder.build();

        assertEquals(service, builder.executorService);
    }

    @Test
    public void add_scheme_handler() {

        final String scheme = "mock";
        assertNull(builder.schemeHandlers.get(scheme));

        final SchemeHandler schemeHandler = mock(SchemeHandler.class);
        when(schemeHandler.supportedSchemes()).thenReturn(Collections.singleton(scheme));

        builder.addSchemeHandler(schemeHandler);
        builder.build();

        assertEquals(schemeHandler, builder.schemeHandlers.get(scheme));
    }

    @Test
    public void add_scheme_handler_multiple_types() {
        // all supported types are registered

        final String[] schemes = {
                "mock-1",
                "mock-2"
        };

        final SchemeHandler schemeHandler = mock(SchemeHandler.class);
        when(schemeHandler.supportedSchemes()).thenReturn(Arrays.asList(schemes));

        builder.addSchemeHandler(schemeHandler);

        for (String scheme : schemes) {
            assertEquals(scheme, schemeHandler, builder.schemeHandlers.get(scheme));
        }
    }

    @Test
    public void add_media_decoder() {

        final String media = "mocked/type";
        assertNull(builder.mediaDecoders.get(media));

        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
        when(mediaDecoder.supportedTypes()).thenReturn(Collections.singleton(media));

        builder.addMediaDecoder(mediaDecoder);
        builder.build();

        assertEquals(mediaDecoder, builder.mediaDecoders.get(media));
    }

    @Test
    public void add_media_decoder_multiple_types() {

        final String[] types = {
                "mock/type1",
                "mock/type2"
        };

        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
        when(mediaDecoder.supportedTypes()).thenReturn(Arrays.asList(types));

        builder.addMediaDecoder(mediaDecoder);

        for (String type : types) {
            assertEquals(type, mediaDecoder, builder.mediaDecoders.get(type));
        }
    }

    @Test
    public void default_media_decoder() {

        assertNotNull(builder.defaultMediaDecoder);

        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
        builder.defaultMediaDecoder(mediaDecoder);
        builder.build();

        assertEquals(mediaDecoder, builder.defaultMediaDecoder);
    }

    @Test
    public void remove_scheme_handler() {

        final String scheme = "mock";
        final SchemeHandler schemeHandler = mock(SchemeHandler.class);
        when(schemeHandler.supportedSchemes()).thenReturn(Collections.singleton(scheme));

        assertNull(builder.schemeHandlers.get(scheme));
        builder.addSchemeHandler(schemeHandler);
        assertNotNull(builder.schemeHandlers.get(scheme));
        builder.removeSchemeHandler(scheme);
        assertNull(builder.schemeHandlers.get(scheme));
    }

    @Test
    public void remove_media_decoder() {

        final String media = "mock/type";
        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
        when(mediaDecoder.supportedTypes()).thenReturn(Collections.singleton(media));

        assertNull(builder.mediaDecoders.get(media));
        builder.addMediaDecoder(mediaDecoder);
        assertNotNull(builder.mediaDecoders.get(media));
        builder.removeMediaDecoder(media);
        assertNull(builder.mediaDecoders.get(media));
    }

    @Test
    public void cannot_build_twice() {

        builder.build();
        try {
            builder.build();
            fail();
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().contains("has already been configured"));
        }
    }
}