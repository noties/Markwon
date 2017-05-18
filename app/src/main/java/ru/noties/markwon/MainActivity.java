package ru.noties.markwon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class MainActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Inject
    MarkdownLoader markdownLoader;

    @Inject
    MarkdownRenderer markdownRenderer;

    @Inject
    Themes themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component(this)
                .mainActivitySubcomponent()
                .inject(this);

        themes.apply(this);

        // how can we obtain SpannableConfiguration after theme was applied?

        setContentView(R.layout.activity_main);


        final AppBarItem.Renderer appBarRenderer
                = new AppBarItem.Renderer(findViewById(R.id.app_bar), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themes.toggle();
                recreate();
            }
        });

        final TextView textView = Views.findView(this, R.id.text);
        final View progress = findViewById(R.id.progress);

        appBarRenderer.render(appBarState());

        markdownLoader.load(uri(), new MarkdownLoader.OnMarkdownTextLoaded() {
            @Override
            public void apply(String text) {
                markdownRenderer.render(MainActivity.this, text, new MarkdownRenderer.MarkdownReadyListener() {
                    @Override
                    public void onMarkdownReady(CharSequence markdown) {
                        Markwon.setText(textView, markdown);
                        Views.setVisible(progress, false);
                    }
                });
            }
        });
    }

    private AppBarItem.State appBarState() {

        final String title;
        final String subtitle;

        // two possible states: just opened from launcher (no subtitle)
        // opened to display external resource (subtitle as a path/url/whatever)

        final Uri uri = uri();

        Debug.i(uri);

        if (uri != null) {
            title = uri.getLastPathSegment();
            subtitle = uri.toString();
        } else {
            title = getString(R.string.app_name);
            subtitle = null;
        }

        return new AppBarItem.State(title, subtitle);
    }

    private Uri uri() {
        final Intent intent = getIntent();
        return intent != null
                ? intent.getData()
                : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        markdownLoader.cancel();
        markdownRenderer.cancel();
    }
}
