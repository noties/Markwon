package io.noties.markwon.app;

import android.app.Application;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(true));
    }
}
