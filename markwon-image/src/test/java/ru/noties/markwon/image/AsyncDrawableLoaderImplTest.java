package ru.noties.markwon.image;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private static class BuilderImpl {

        AsyncDrawableLoaderBuilder builder;
        Handler handler = mock(Handler.class);

        public BuilderImpl executorService(@NonNull ExecutorService executorService) {
            builder.executorService(executorService);
            return this;
        }

        public BuilderImpl addSchemeHandler(@NonNull SchemeHandler schemeHandler) {
            builder.addSchemeHandler(schemeHandler);
            return this;
        }

        public BuilderImpl addMediaDecoder(@NonNull MediaDecoder mediaDecoder) {
            builder.addMediaDecoder(mediaDecoder);
            return this;
        }

        public BuilderImpl defaultMediaDecoder(@Nullable MediaDecoder mediaDecoder) {
            builder.defaultMediaDecoder(mediaDecoder);
            return this;
        }

        public BuilderImpl removeSchemeHandler(@NonNull String scheme) {
            builder.removeSchemeHandler(scheme);
            return this;
        }

        public BuilderImpl removeMediaDecoder(@NonNull String contentType) {
            builder.removeMediaDecoder(contentType);
            return this;
        }

        public BuilderImpl placeholderProvider(@NonNull ImagesPlugin.PlaceholderProvider placeholderDrawableProvider) {
            builder.placeholderProvider(placeholderDrawableProvider);
            return this;
        }

        public BuilderImpl errorHandler(@NonNull ImagesPlugin.ErrorHandler errorHandler) {
            builder.errorHandler(errorHandler);
            return this;
        }

        @NonNull
        public BuilderImpl handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        @NonNull
        AsyncDrawableLoaderImpl build() {
            return new AsyncDrawableLoaderImpl(builder, handler);
        }
    }
}