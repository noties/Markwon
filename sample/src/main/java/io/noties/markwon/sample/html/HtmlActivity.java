package io.noties.markwon.sample.html;

import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.html.tag.SimpleTagHandler;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class HtmlActivity extends ActivityWithMenuOptions {

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("align", this::align)
                .add("randomCharSize", this::randomCharSize)
                .add("enhance", this::enhance);
    }

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);

        // let's define some custom tag-handlers

        textView = findViewById(R.id.text_view);

        align();
    }

    // we can use `SimpleTagHandler` for _simple_ cases (when the whole tag content
    // will have spans from start to end)
    //
    // we can use any tag name, even not defined in HTML spec
    private static class AlignTagHandler extends SimpleTagHandler {

        @Nullable
        @Override
        public Object getSpans(
                @NonNull MarkwonConfiguration configuration,
                @NonNull RenderProps renderProps,
                @NonNull HtmlTag tag) {

            final Layout.Alignment alignment;

            // html attribute without value, <align center></align>
            if (tag.attributes().containsKey("center")) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else if (tag.attributes().containsKey("end")) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else {
                // empty value or any other will make regular alignment
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }

            return new AlignmentSpan.Standard(alignment);
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("align");
        }
    }

    private void align() {

        final String md = "" +
                "<align center>We are centered</align>\n" +
                "\n" +
                "<align end>We are at the end</align>\n" +
                "\n" +
                "<align>We should be at the start</align>\n" +
                "\n";


        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new AlignTagHandler()));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    // each character will have random size
    private static class RandomCharSize extends TagHandler {

        private final Random random;
        private final float base;

        RandomCharSize(@NonNull Random random, float base) {
            this.random = random;
            this.base = base;
        }

        @Override
        public void handle(
                @NonNull MarkwonVisitor visitor,
                @NonNull MarkwonHtmlRenderer renderer,
                @NonNull HtmlTag tag) {

            final SpannableBuilder builder = visitor.builder();

            // text content is already added, we should only apply spans

            for (int i = tag.start(), end = tag.end(); i < end; i++) {
                final int size = (int) (base * (random.nextFloat() + 0.5F) + 0.5F);
                builder.setSpan(new AbsoluteSizeSpan(size, false), i, i + 1);
            }
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("random-char-size");
        }
    }

    private void randomCharSize() {

        final String md = "" +
                "<random-char-size>\n" +
                "This message should have a jumpy feeling because of different sizes of characters\n" +
                "</random-char-size>\n\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new RandomCharSize(new Random(42L), textView.getTextSize())));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private static class EnhanceTagHandler extends TagHandler {

        private final int enhanceTextSize;

        EnhanceTagHandler(@Px int enhanceTextSize) {
            this.enhanceTextSize = enhanceTextSize;
        }

        @Override
        public void handle(
                @NonNull MarkwonVisitor visitor,
                @NonNull MarkwonHtmlRenderer renderer,
                @NonNull HtmlTag tag) {

            // we require start and end to be present
            final int start = parsePosition(tag.attributes().get("start"));
            final int end = parsePosition(tag.attributes().get("end"));

            if (start > -1 && end > -1) {
                visitor.builder().setSpan(
                        new AbsoluteSizeSpan(enhanceTextSize),
                        tag.start() + start,
                        tag.start() + end
                );
            }
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("enhance");
        }

        private static int parsePosition(@Nullable String value) {
            int position;
            if (!TextUtils.isEmpty(value)) {
                try {
                    position = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    position = -1;
                }
            } else {
                position = -1;
            }
            return position;
        }
    }

    private void enhance() {

        final String md = "" +
                "<enhance start=\"5\" end=\"12\">This is text that must be enhanced, at least a part of it</enhance>";


        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new EnhanceTagHandler((int) (textView.getTextSize() * 2 + .05F))));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }
}
