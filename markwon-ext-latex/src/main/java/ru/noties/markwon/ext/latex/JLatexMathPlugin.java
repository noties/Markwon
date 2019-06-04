package ru.noties.markwon.ext.latex;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import org.commonmark.node.Image;
import org.commonmark.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import ru.noties.jlatexmath.JLatexMathDrawable;
import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.ImageProps;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.image.MediaDecoder;
import ru.noties.markwon.image.SchemeHandler;
import ru.noties.markwon.priority.Priority;

/**
 * @since 3.0.0
 */
public class JLatexMathPlugin extends AbstractMarkwonPlugin {

    public interface BuilderConfigure {
        void configureBuilder(@NonNull Builder builder);
    }

    @NonNull
    public static JLatexMathPlugin create(float textSize) {
        return new JLatexMathPlugin(builder(textSize).build());
    }

    @NonNull
    public static JLatexMathPlugin create(@NonNull Config config) {
        return new JLatexMathPlugin(config);
    }

    @NonNull
    public static JLatexMathPlugin create(float textSize, @NonNull BuilderConfigure builderConfigure) {
        final Builder builder = new Builder(textSize);
        builderConfigure.configureBuilder(builder);
        return new JLatexMathPlugin(builder.build());
    }

    @NonNull
    public static JLatexMathPlugin.Builder builder(float textSize) {
        return new Builder(textSize);
    }

    public static class Config {

        private final float textSize;

        private final Drawable background;

        @JLatexMathDrawable.Align
        private final int align;

        private final boolean fitCanvas;

        private final int padding;

        Config(@NonNull Builder builder) {
            this.textSize = builder.textSize;
            this.background = builder.background;
            this.align = builder.align;
            this.fitCanvas = builder.fitCanvas;
            this.padding = builder.padding;
        }
    }

    @NonNull
    public static String makeDestination(@NonNull String latex) {
        return SCHEME + "://" + latex;
    }

    private static final String SCHEME = "jlatexmath";
    private static final String CONTENT_TYPE = "text/jlatexmath";

    private final Config config;

    JLatexMathPlugin(@NonNull Config config) {
        this.config = config;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new JLatexMathBlockParser.Factory());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(JLatexMathBlock.class, new MarkwonVisitor.NodeVisitor<JLatexMathBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull JLatexMathBlock jLatexMathBlock) {

                final String latex = jLatexMathBlock.latex();

                final int length = visitor.length();
                visitor.builder().append(latex);

                final RenderProps renderProps = visitor.renderProps();

                ImageProps.DESTINATION.set(renderProps, makeDestination(latex));
                ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, false);
                ImageProps.IMAGE_SIZE.set(renderProps, new ImageSize(new ImageSize.Dimension(100, "%"), null));

                visitor.setSpansForNode(Image.class, length);
            }
        });
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
        builder
                .addSchemeHandler(SCHEME, new SchemeHandler() {
                    @Nullable
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

                        ImageItem item = null;

                        try {
                            final byte[] bytes = raw.substring(SCHEME.length()).getBytes("UTF-8");
                            item = new ImageItem(
                                    CONTENT_TYPE,
                                    new ByteArrayInputStream(bytes));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        return item;
                    }
                })
                .addMediaDecoder(CONTENT_TYPE, new MediaDecoder() {
                    @Nullable
                    @Override
                    public Drawable decode(@NonNull InputStream inputStream) {

                        final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                        final String latex = scanner.hasNext()
                                ? scanner.next()
                                : null;

                        if (latex == null) {
                            return null;
                        }

                        return JLatexMathDrawable.builder(latex)
                                .textSize(config.textSize)
                                .background(config.background)
                                .align(config.align)
                                .fitCanvas(config.fitCanvas)
                                .padding(config.padding)
                                .build();
                    }
                });
    }

    @NonNull
    @Override
    public Priority priority() {
        return Priority.after(ImagesPlugin.class);
    }

    public static class Builder {

        private final float textSize;

        private Drawable background;

        @JLatexMathDrawable.Align
        private int align = JLatexMathDrawable.ALIGN_CENTER;

        private boolean fitCanvas = true;

        private int padding;

        Builder(float textSize) {
            this.textSize = textSize;
        }

        @NonNull
        public Builder background(@NonNull Drawable background) {
            this.background = background;
            return this;
        }

        @NonNull
        public Builder align(@JLatexMathDrawable.Align int align) {
            this.align = align;
            return this;
        }

        @NonNull
        public Builder fitCanvas(boolean fitCanvas) {
            this.fitCanvas = fitCanvas;
            return this;
        }

        @NonNull
        public Builder padding(@Px int padding) {
            this.padding = padding;
            return this;
        }

        @NonNull
        public Config build() {
            return new Config(this);
        }
    }
}
