package ru.noties.markwon.sample.extension;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.core.MarkwonTheme;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text_view);

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(IconPlugin.create(IconSpanProvider.create(this, 0)))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        // this part has nothing to do with actual IconPlugin
                        // this part is used to showcase that headers can be controlled via Theme
                        final float[] textSizeMultipliers = new float[]{3f, 2f, 1.5f, 1f, .5f, .25f};
                        builder
                                .headingTypeface(Typeface.MONOSPACE)
                                .headingTextSizeMultipliers(textSizeMultipliers);
                    }
                })
                .build();

        markwon.setMarkdown(textView, getString(R.string.input));
    }
}
