package ru.noties.markwon;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.markwon.renderer.*;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.AsyncDrawableSpanUtils;

public class MainActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    private List<Target> targets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.activity_main);


        final Picasso picasso = new Picasso.Builder(this)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Debug.i(exception, uri);
                    }
                })
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                Scanner scanner = null;
                String md = null;
                try {
//                    stream = getAssets().open("scrollable.md");
                    stream = getAssets().open("test.md");
                    scanner = new Scanner(stream).useDelimiter("\\A");
                    if (scanner.hasNext()) {
                        md = scanner.next();
                    }
                } catch (Throwable t) {
                    Debug.e(t);
                } finally {
                    if (stream != null) {
                        try { stream.close(); } catch (IOException e) {}
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

//                    final SpannableConfiguration configuration = SpannableConfiguration.builder(MainActivity.this)
//                            .setAsyncDrawableLoader(new AsyncDrawable.Loader() {
//                                @Override
//                                public void load(@NonNull String destination, @NonNull final AsyncDrawable drawable) {
//                                    Debug.i(destination);
//                                    final Target target = new Target() {
//                                        @Override
//                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                            Debug.i();
//                                            final Drawable d = new BitmapDrawable(getResources(), bitmap);
//                                            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                                            drawable.setResult(d);
////                                            textView.setText(textView.getText());
//                                        }
//
//                                        @Override
//                                        public void onBitmapFailed(Drawable errorDrawable) {
//                                            Debug.i();
//                                        }
//
//                                        @Override
//                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                            Debug.i();
//                                        }
//                                    };
//                                    targets.add(target);
//
//                                            picasso.load(destination)
//                                            .tag(destination)
//                                            .into(target);
//
//                                }
//
//                                @Override
//                                public void cancel(@NonNull String destination) {
//                                    Debug.i(destination);
//                                    picasso
//                                            .cancelTag(destination);
//                                }
//                            })
//                            .setCodeConfig(CodeSpan.Config.builder().setTextSize(
//                                    (int) (getResources().getDisplayMetrics().density * 14 + .5F)
//                            ).setMultilineMargin((int) (getResources().getDisplayMetrics().density * 8 + .5F)).build())
//                            .build();

                    final SpannableConfiguration configuration = SpannableConfiguration.create(MainActivity.this);

                    final CharSequence text = new ru.noties.markwon.renderer.SpannableRenderer().render(
                            configuration,
                            node
                    );

//                    final CharSequence text = new SpannableRenderer()._render(node/*, new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText(textView.getText());
//                            final Drawable drawable = null;
//                            drawable.setCallback(textView);
//                        }
//                    }*/);
                    final long end = SystemClock.uptimeMillis();
                    Debug.i("Rendered: %d ms, length: %d", end - start, text.length());
//                    Debug.i(text);
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            // NB! LinkMovementMethod forces frequent updates...
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            textView.setText(text);
                            AsyncDrawableSpanUtils.scheduleDrawables(textView);
                        }
                    });
                }
            }
        }).start();
    }
}
