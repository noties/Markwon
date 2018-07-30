package ru.noties.markwon;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import ru.noties.markwon.il.AsyncDrawableLoader;
import ru.noties.markwon.il.GifMediaDecoder;
import ru.noties.markwon.il.ImageMediaDecoder;
import ru.noties.markwon.il.SvgMediaDecoder;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.syntax.Prism4jThemeDarkula;
import ru.noties.markwon.syntax.Prism4jThemeDefault;
import ru.noties.prism4j.Prism4j;
import ru.noties.prism4j.annotations.PrismBundle;

@Module
@PrismBundle(includeAll = true)
class AppModule {

    private final App app;

    AppModule(App app) {
        this.app = app;
    }

    @Provides
    Context context() {
        return app.getApplicationContext();
    }

    @Provides
    Resources resources() {
        return app.getResources();
    }

    @Provides
    @Singleton
    OkHttpClient client() {
        return new OkHttpClient.Builder()
                .cache(new Cache(app.getCacheDir(), 1024L * 1024 * 20)) // 20 mb
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Singleton
    @Provides
    ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Singleton
    @Provides
    Handler mainThread() {
        return new Handler(Looper.getMainLooper());
    }

    @Singleton
    @Provides
    UriProcessor uriProcessor() {
        return new UriProcessorImpl();
    }

    @Provides
    AsyncDrawable.Loader asyncDrawableLoader(
            OkHttpClient client,
            ExecutorService executorService,
            Resources resources) {
        return AsyncDrawableLoader.builder()
                .client(client)
                .executorService(executorService)
                .resources(resources)
                .mediaDecoders(
                        SvgMediaDecoder.create(resources),
                        GifMediaDecoder.create(false),
                        ImageMediaDecoder.create(resources)
                )
                .build();
    }

    @Provides
    @Singleton
    Prism4j prism4j() {
        return new Prism4j(new GrammarLocatorDef());
    }

    @Singleton
    @Provides
    Prism4jThemeDefault prism4jThemeDefault() {
        return Prism4jThemeDefault.create();
    }

    @Singleton
    @Provides
    Prism4jThemeDarkula prism4jThemeDarkula() {
        return Prism4jThemeDarkula.create();
    }

    @Singleton
    @Provides
    GifProcessor gifProcessor() {
        return GifProcessor.create();
    }
}
