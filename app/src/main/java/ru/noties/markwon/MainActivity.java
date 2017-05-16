package ru.noties.markwon;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.markwon.renderer.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.spans.AsyncDrawable;

public class MainActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

//    private List<Target> targets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.activity_main);

//
//        final Picasso picasso = new Picasso.Builder(this)
//                .listener(new Picasso.Listener() {
//                    @Override
//                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                        Debug.i(exception, uri);
//                    }
//                })
//                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                Scanner scanner = null;
                String md = null;
                try {
                    stream = getAssets().open("scrollable.md");
//                    stream = getAssets().open("test.md");
                    scanner = new Scanner(stream).useDelimiter("\\A");
                    if (scanner.hasNext()) {
                        md = scanner.next();
                    }
                } catch (Throwable t) {
                    Debug.e(t);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                        }
                    }
                    if (scanner != null) {
                        scanner.close();
                    }
                }

                if (md != null) {
                    final long start = SystemClock.uptimeMillis();
                    final Parser parser = new Parser.Builder()
                            .extensions(Arrays.asList(StrikethroughExtension.create()))
                            .build();
                    final Node node = parser.parse(md);

                    final SpannableConfiguration configuration = SpannableConfiguration.builder(MainActivity.this)
                            .asyncDrawableLoader(new AsyncDrawable.Loader() {
                                @Override
                                public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {
                                    Debug.i("destination: %s, drawable: %s", destination, drawable);
                                }

                                @Override
                                public void cancel(@NonNull String destination) {
                                    Debug.i("destination: %s", destination);
                                }
                            })
                            .build();

                    final CharSequence text = new SpannableRenderer().render(
                            configuration,
                            node
                    );

                    final long end = SystemClock.uptimeMillis();
                    Debug.i("Rendered: %d ms, length: %d", end - start, text.length());

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            // NB! LinkMovementMethod forces frequent updates...
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            textView.setText(text);
                            SpannableRenderer.scheduleDrawables(textView);
//                            AsyncDrawableSpanUtils.scheduleDrawables(textView);
                        }
                    });
                }
            }
        }).start();
    }
}
