package io.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import io.noties.markwon.image.ImagesPlugin.ErrorHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AsyncDrawableLoaderImplTest {

    private BuilderImpl builder;
    private AsyncDrawableLoaderImpl impl;

    @Before
    public void before() {
        builder = new BuilderImpl();
    }

    @Test
    public void placeholder() {
        final ImagesPlugin.PlaceholderProvider placeholderProvider =
                mock(ImagesPlugin.PlaceholderProvider.class);
        impl = builder.placeholderProvider(placeholderProvider)
                .build();
        impl.placeholder(mock(AsyncDrawable.class));
        verify(placeholderProvider, times(1))
                .providePlaceholder(any(AsyncDrawable.class));
    }

    @Test
    public void load_cancel() {
        // verify that load/cancel works as expected

        final ExecutorService executorService = mock(ExecutorService.class);
        final Future future = mock(Future.class);
        {
            //noinspection unchecked
            when(executorService.submit(any(Runnable.class)))
                    .thenReturn(future);
        }

        final Handler handler = mock(Handler.class);

        final AsyncDrawable drawable = mock(AsyncDrawable.class);

        impl = builder
                .executorService(executorService)
                .handler(handler)
                .build();

        impl.load(drawable);

        verify(executorService, times(1)).submit(any(Runnable.class));

        impl.cancel(drawable);

        verify(future, times(1)).cancel(eq(true));
        verify(handler, times(1)).removeCallbacksAndMessages(eq(drawable));
    }

    @Test
    public void load_no_scheme_handler() {
        // when loading is triggered for a scheme which has no registered scheme-handler

        final ErrorHandler errorHandler = mock(ErrorHandler.class);

        impl = builder
                .executorService(immediateExecutorService())
                .errorHandler(errorHandler)
                .build();

        final String destination = "blah://blah.JPEG";

        impl.load(asyncDrawable(destination));

        final ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(errorHandler, times(1))
                .handleError(eq(destination), throwableCaptor.capture());
        final Throwable value = throwableCaptor.getValue();
        assertTrue(value.getClass().getName(), value instanceof IllegalStateException);
        assertTrue(value.getMessage(), value.getMessage().contains("No scheme-handler is found"));
        assertTrue(value.getMessage(), value.getMessage().contains(destination));
    }

    @Test
    public void load_scheme_handler_throws() {

        final ErrorHandler errorHandler = mock(ErrorHandler.class);
        final SchemeHandler schemeHandler = new SchemeHandler() {
            @NonNull
            @Override
            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                throw new RuntimeException("We throw!");
            }

            @NonNull
            @Override
            public Collection<String> supportedSchemes() {
                return Collections.singleton("hey");
            }
        };

        impl = builder
                .executorService(immediateExecutorService())
                .errorHandler(errorHandler)
                .addSchemeHandler(schemeHandler)
                .build();

        final String destination = "hey://whe.er";

        impl.load(asyncDrawable(destination));

        final ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(errorHandler, times(1))
                .handleError(eq(destination), captor.capture());

        final Throwable throwable = captor.getValue();
        assertTrue(throwable.getClass().getName(), throwable instanceof RuntimeException);
        assertEquals("We throw!", throwable.getMessage());
    }

    @Test
    public void load_scheme_handler_returns_result() {

        final Drawable drawable = mock(Drawable.class);
        final SchemeHandler schemeHandler = new SchemeHandler() {
            @NonNull
            @Override
            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                return ImageItem.withResult(drawable);
            }

            @NonNull
            @Override
            public Collection<String> supportedSchemes() {
                return Collections.singleton("*");
            }
        };

        final String destination = "*://yo";

        final Future future = mock(Future.class);
        final ExecutorService executorService = immediateExecutorService(future);
        final Handler handler = mock(Handler.class);

        impl = builder
                .executorService(executorService)
                .handler(handler)
                .addSchemeHandler(schemeHandler)
                .build();

        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        verify(executorService, times(1))
                .submit(any(Runnable.class));

        // we must use captor in order to let the internal (async) logic settle
        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        captor.getValue().run();

        verify(asyncDrawable, times(1))
                .setResult(eq(drawable));

        // now, let's cancel the request (at this point it must be removed from referencing)
        impl.cancel(asyncDrawable);

        verify(future, never()).cancel(anyBoolean());

        // this method will be called anyway (we have no mean to check if token has queue)
//        verify(handler, never()).removeCallbacksAndMessages(eq(asyncDrawable));
    }

    @Test
    public void load_scheme_handler_returns_decoding_default_used() {
        // we won't be registering media decoder, but provide a default one (which must be used)

        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);
        final InputStream inputStream = mock(InputStream.class);
        final Drawable drawable = mock(Drawable.class);

        {
            when(mediaDecoder.decode(any(String.class), any(InputStream.class)))
                    .thenReturn(drawable);
        }

        impl = builder
                .executorService(immediateExecutorService(mock(Future.class)))
                .defaultMediaDecoder(mediaDecoder)
                .addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                        return ImageItem.withDecodingNeeded("no/op", inputStream);
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("whatever");
                    }
                })
                .build();

        final String destination = "whatever://yeah-yeah-yeah";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        verify(mediaDecoder, times(1))
                .decode(eq("no/op"), eq(inputStream));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(builder._handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        captor.getValue().run();

        verify(asyncDrawable, times(1))
                .setResult(eq(drawable));
    }

    @Test
    public void load_no_media_decoder_present() {
        // if some content-type is requested (and it has no registered media-decoder),
        // and default-media-decoder is not added -> throws

        final ErrorHandler errorHandler = mock(ErrorHandler.class);

        impl = builder
                .defaultMediaDecoder(null)
                .executorService(immediateExecutorService())
                .errorHandler(errorHandler)
                .addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                        return ImageItem.withDecodingNeeded("np/op", mock(InputStream.class));
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("ftp");
                    }
                })
                .build();

        final String destination = "ftp://xxx";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        final ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);

        verify(errorHandler, times(1))
                .handleError(eq(destination), captor.capture());

        final Throwable throwable = captor.getValue();
        assertTrue(throwable.getClass().getName(), throwable instanceof IllegalStateException);
        assertTrue(throwable.getMessage(), throwable.getMessage().contains("No media-decoder is found"));
        assertTrue(throwable.getMessage(), throwable.getMessage().contains(destination));
    }

    @Test
    public void load_error_handler_drawable() {
        // error-handler can return optional error-drawable that can be used as a result

        final ErrorHandler errorHandler = mock(ErrorHandler.class);
        final Drawable drawable = mock(Drawable.class);
        {
            when(errorHandler.handleError(any(String.class), any(Throwable.class)))
                    .thenReturn(drawable);
        }

        impl = builder
                .executorService(immediateExecutorService(mock(Future.class)))
                .errorHandler(errorHandler)
                .build();

        // we will rely on _internal_ error, which is also delivered to error-handler
        // in this case -> no scheme-handler

        final String destination = "uo://uo?true=false";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        verify(errorHandler, times(1))
                .handleError(eq(destination), any(Throwable.class));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(builder._handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        captor.getValue().run();

        verify(asyncDrawable, times(1))
                .setResult(eq(drawable));
    }

    @Test
    public void load_success_request_cancelled() {
        // when loading finishes it must check if request had been cancelled and not deliver result

        impl = builder
                .executorService(immediateExecutorService(mock(Future.class)))
                .addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                        return ImageItem.withResult(mock(Drawable.class));
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("ja");
                    }
                })
                .build();

        final String destination = "ja://jajaja";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(builder._handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        // now, cancel
        impl.cancel(asyncDrawable);

        captor.getValue().run();

        verify(asyncDrawable, never())
                .setResult(any(Drawable.class));
    }

    @Test
    public void load_success_async_drawable_not_attached() {
        // when loading finishes, it must check if async-drawable is attached

        impl = builder
                .executorService(immediateExecutorService(mock(Future.class)))
                .addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                        return ImageItem.withResult(mock(Drawable.class));
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("ha");
                    }
                })
                .build();

        final String destination = "ha://hahaha";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);
        when(asyncDrawable.isAttached()).thenReturn(false);

        impl.load(asyncDrawable);

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(builder._handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        captor.getValue().run();

        verify(asyncDrawable, never())
                .setResult(any(Drawable.class));
    }

    @Test
    public void load_success_result_null() {
        // if result is null (but no exception) - no result must be delivered

        // we won't be adding scheme-handler, thus causing internal error
        // (will have to mock error-handler because for the tests we re-throw errors)
        impl = builder
                .executorService(immediateExecutorService(mock(Future.class)))
                .errorHandler(mock(ErrorHandler.class))
                .build();

        final String destination = "xa://xaxaxa";
        final AsyncDrawable asyncDrawable = asyncDrawable(destination);

        impl.load(asyncDrawable);

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(builder._handler, times(1))
                .postAtTime(captor.capture(), eq(asyncDrawable), anyLong());

        captor.getValue().run();

        verify(asyncDrawable, never())
                .setResult(any(Drawable.class));
    }

    @Test
    public void media_decoder_is_used() {

        final MediaDecoder mediaDecoder = mock(MediaDecoder.class);

        {
            when(mediaDecoder.decode(any(String.class), any(InputStream.class)))
                    .thenReturn(mock(Drawable.class));
            when(mediaDecoder.supportedTypes())
                    .thenReturn(Collections.singleton("fa/ke"));
        }

        impl = builder.executorService(immediateExecutorService())
                .addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                        return ImageItem.withDecodingNeeded("fa/ke", mock(InputStream.class));
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("fake");
                    }
                })
                .addMediaDecoder(mediaDecoder)
                .build();

        final String destination = "fake://1234";

        impl.load(asyncDrawable(destination));

        verify(mediaDecoder, times(1))
                .decode(eq("fa/ke"), any(InputStream.class));
    }

    private static class BuilderImpl {

        AsyncDrawableLoaderBuilder _builder = new AsyncDrawableLoaderBuilder();
        Handler _handler = mock(Handler.class);

        {
            // be default it just logs the exception, let's rethrow
            _builder.errorHandler(new ErrorHandler() {
                @Nullable
                @Override
                public Drawable handleError(@NonNull String url, @NonNull Throwable throwable) {
                    throw new AsyncDrawableException(url, throwable);
                }
            });
        }

        BuilderImpl executorService(@NonNull ExecutorService executorService) {
            _builder.executorService(executorService);
            return this;
        }

        BuilderImpl addSchemeHandler(@NonNull SchemeHandler schemeHandler) {
            _builder.addSchemeHandler(schemeHandler);
            return this;
        }

        BuilderImpl addMediaDecoder(@NonNull MediaDecoder mediaDecoder) {
            _builder.addMediaDecoder(mediaDecoder);
            return this;
        }

        BuilderImpl defaultMediaDecoder(@Nullable MediaDecoder mediaDecoder) {
            _builder.defaultMediaDecoder(mediaDecoder);
            return this;
        }

        BuilderImpl placeholderProvider(@NonNull ImagesPlugin.PlaceholderProvider placeholderDrawableProvider) {
            _builder.placeholderProvider(placeholderDrawableProvider);
            return this;
        }

        BuilderImpl errorHandler(@NonNull ErrorHandler errorHandler) {
            _builder.errorHandler(errorHandler);
            return this;
        }

        @NonNull
        BuilderImpl handler(Handler handler) {
            this._handler = handler;
            return this;
        }

        @NonNull
        AsyncDrawableLoaderImpl build() {
            return new AsyncDrawableLoaderImpl(_builder, _handler);
        }

        private static class AsyncDrawableException extends RuntimeException {
            AsyncDrawableException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }

    @NonNull
    private static ExecutorService immediateExecutorService() {
        return immediateExecutorService(null);
    }

    @NonNull
    private static ExecutorService immediateExecutorService(@Nullable final Future future) {
        final ExecutorService service = mock(ExecutorService.class);
        when(service.submit(any(Runnable.class))).then(new Answer<Future>() {
            @Override
            public Future answer(InvocationOnMock invocation) {
                ((Runnable) invocation.getArgument(0)).run();
                return future;
            }
        });
        return service;
    }

    @NonNull
    private static AsyncDrawable asyncDrawable(@NonNull String destination) {
        final AsyncDrawable drawable = mock(AsyncDrawable.class);
        when(drawable.getDestination()).thenReturn(destination);
        when(drawable.isAttached()).thenReturn(true);
        return drawable;
    }
}