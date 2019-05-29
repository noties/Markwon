package ru.noties.markwon.sample.customextension;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.sample.R;

public class CustomExtensionActivity extends Activity {

    // please note that this sample won't work on a device with SDK level < 21
    // as we are using vector drawables for the sake of brevity. Other than resources
    // used, this is fully functional sample on all SDK levels
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);

        final TextView textView = findViewById(R.id.text_view);

        // note that we haven't registered CorePlugin, as it's the only one that can be
        // implicitly deducted and added automatically. All other plugins require explicit
        // `usePlugin` call
        final Markwon markwon = Markwon.builder(this)
                // try commenting out this line to see runtime dependency resolution
//                .usePlugin(ImagesPlugin.create(this))
                .usePlugin(IconPlugin.create(IconSpanProvider.create(this, 0)))
                .build();

        markwon.setMarkdown(textView, getString(R.string.input));
    }
}
