package io.noties.markwon.sample.simpleext;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.sample.R;
import io.noties.markwon.simple.ext.SimpleExtPlugin;

public class SimpleExtActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);

        final TextView textView = findViewById(R.id.text_view);

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(SimpleExtPlugin.create(plugin -> plugin
                        // +sometext+
                        .addExtension(1, '+', (_1, _2) -> new EmphasisSpan())
                        // ??sometext??
                        .addExtension(2, '?', (_1, _2) -> new StrongEmphasisSpan())
                        // @@sometext$$
                        .addExtension(2, '@', '$', (_1, _2) -> new ForegroundColorSpan(Color.RED))))
                .build();

        final String markdown = "# SimpleExt\n" +
                "\n" +
                "+let's start with `+`, ??then we can use this, and finally @@this$$??+";

        markwon.setMarkdown(textView, markdown);
    }
}
