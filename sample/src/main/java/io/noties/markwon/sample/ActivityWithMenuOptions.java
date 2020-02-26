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
        return menuOptions.onOptionsItemSelected(item);
    }
}
