package ru.noties.markwon;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
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
        return new OkHttpClient();
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
    Picasso picasso(Context context) {
        return Picasso.with(context);
    }
}
