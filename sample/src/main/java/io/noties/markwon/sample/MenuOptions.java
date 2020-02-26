package io.noties.markwon.sample;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuOptions {

    @NonNull
    public static MenuOptions create() {
        return new MenuOptions();
    }

    static class Option {
        final String title;
        final Runnable action;

        Option(@NonNull String title, @NonNull Runnable action) {
            this.title = title;
            this.action = action;
        }
    }

    // to preserve order use LinkedHashMap
    private final Map<String, Runnable> actions = new LinkedHashMap<>();

    @NonNull
    public MenuOptions add(@NonNull String title, @NonNull Runnable action) {
        actions.put(title, action);
        return this;
    }

    boolean onCreateOptionsMenu(Menu menu) {
        if (!actions.isEmpty()) {
            for (String key : actions.keySet()) {
                menu.add(key);
            }
            return true;
        }
        return false;
    }

    @Nullable
    Option onOptionsItemSelected(MenuItem item) {
        final String title = String.valueOf(item.getTitle());
        final Runnable action = actions.get(title);
        if (action != null) {
            return new Option(title, action);
        }
        return null;
    }
}
