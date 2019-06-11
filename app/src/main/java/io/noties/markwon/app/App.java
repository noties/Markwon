package io.noties.markwon.app;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent component(@NonNull Context context) {
        return ((App) context.getApplicationContext()).component;
    }
}
