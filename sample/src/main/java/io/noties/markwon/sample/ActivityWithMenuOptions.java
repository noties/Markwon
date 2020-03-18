package io.noties.markwon.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ActivityWithMenuOptions extends Activity {

    @NonNull
    public abstract MenuOptions menuOptions();

    protected void beforeOptionSelected(@NonNull String option) {
        // no op, override to customize
    }

    protected void afterOptionSelected(@NonNull String option) {
        // no op, override to customize
    }

    private MenuOptions menuOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptions = menuOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuOptions.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuOptions.Option option = menuOptions.onOptionsItemSelected(item);
        if (option != null) {
            beforeOptionSelected(option.title);
            option.action.run();
            afterOptionSelected(option.title);
            return true;
        }
        return false;
    }
}
