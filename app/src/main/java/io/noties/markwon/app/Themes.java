package io.noties.markwon.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Themes {

    private static final String PREF_NAME = "theme";
    private static final String KEY_THEME_DARK = "key.tD";

    private SharedPreferences preferences;

    @Inject
    Themes(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void apply(@NonNull Context context) {
        final boolean dark = preferences.getBoolean(KEY_THEME_DARK, false);
        // we have only 2 themes and Light one is default
        final int theme;
        if (dark) {
            theme = R.style.AppThemeDark;
        } else {
            theme = R.style.AppThemeLight;
        }

        final Context appContext = context.getApplicationContext();
        if (appContext != context) {
            appContext.setTheme(theme);
        }
        context.setTheme(theme);
    }

    public void toggle() {
        final boolean newValue = !preferences.getBoolean(KEY_THEME_DARK, false);
        preferences.edit()
                .putBoolean(KEY_THEME_DARK, newValue)
                .apply();
    }

    public boolean isLight() {
        return !preferences.getBoolean(KEY_THEME_DARK, false);
    }
}
