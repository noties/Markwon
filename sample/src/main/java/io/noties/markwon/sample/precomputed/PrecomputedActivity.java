package io.noties.markwon.sample.precomputed;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.PrecomputedTextSetterCompat;
import io.noties.markwon.sample.R;

public class PrecomputedActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        final Markwon markwon = Markwon.builder(this)
                // please note that precomputedTextCompat is no-op on devices lower than L (21)
                .textSetter(PrecomputedTextSetterCompat.create(Executors.newCachedThreadPool()))
                .build();

        final TextView textView = findViewById(R.id.text_view);
        final String markdown = "# Hello!\n\n" +
                "This _displays_ how to implement and use `PrecomputedTextCompat` with the **Markwon**\n\n" +
                "> consider using PrecomputedText only if your markdown content is large enough\n> \n" +
                "> **please note** that it works badly with `markwon-recycler` due to asynchronous nature";

        // please note that _sometimes_ (if done without `post` here) further `textView.post`
        // (that is used in PrecomputedTextSetterCompat to deliver result to main-thread) won't be called
        // making the result of pre-computation absent and text-view clear (no text)
        textView.post(() -> markwon.setMarkdown(textView, markdown));
    }
}
