package ru.noties.markwon;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.markwon.spans.AsyncDrawable;

public class MainActivity extends Activity {

    // markdown, mdown, mkdn, mdwn, mkd, md
    // markdown, mdown, mkdn, mkd, md, text

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

                    final CharSequence text = Markwon.markdown(configuration, md);

                    final long end = SystemClock.uptimeMillis();
                    Debug.i("Rendered: %d ms, length: %d", end - start, text.length());

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            // NB! LinkMovementMethod forces frequent updates...
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            textView.setText(text);
                            Markwon.scheduleDrawables(textView);
                        }
                    });
                }
            }
        }).start();
    }
}
