package ru.noties.markwon.sample.theme;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import ru.noties.markwon.sample.R;

public class ThemeActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);
    }
}
