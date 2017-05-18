package ru.noties.markwon;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent component(@NonNull Context context) {
        return ((App) context.getApplicationContext()).component;
    }
}
