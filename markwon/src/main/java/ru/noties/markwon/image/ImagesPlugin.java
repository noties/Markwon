package ru.noties.markwon.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.Arrays;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.image.data.DataUriSchemeHandler;
import ru.noties.markwon.image.file.FileSchemeHandler;
import ru.noties.markwon.image.network.NetworkSchemeHandler;

/**
 * @since 3.0.0
 */
public class ImagesPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static ImagesPlugin create(@NonNull Context context) {
        return new ImagesPlugin(context, false);
    }

    /**
     * Special scheme that is used {@code file:///android_asset/}
     * @param context
     * @return
     */
    @NonNull
    public static ImagesPlugin createWithAssets(@NonNull Context context) {
        return new ImagesPlugin(context, true);
    }

    private final Context context;
    private final boolean useAssets;

    protected ImagesPlugin(Context context, boolean useAssets) {
        this.context = context;
        this.useAssets = useAssets;
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {

        final FileSchemeHandler fileSchemeHandler = useAssets
                ? FileSchemeHandler.createWithAssets(context.getAssets())
                : FileSchemeHandler.create();

        builder
                .addSchemeHandler(DataUriSchemeHandler.SCHEME, DataUriSchemeHandler.create())
                .addSchemeHandler(FileSchemeHandler.SCHEME, fileSchemeHandler)
                .addSchemeHandler(
                        Arrays.asList(
                                NetworkSchemeHandler.SCHEME_HTTP,
                                NetworkSchemeHandler.SCHEME_HTTPS),
                        NetworkSchemeHandler.create())
                .defaultMediaDecoder(ImageMediaDecoder.create(context.getResources()));
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(Image.class, new ImageSpanFactory());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Image.class, new MarkwonVisitor.NodeVisitor<Image>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Image image) {

                // if there is no image spanFactory, ignore
                final SpanFactory spanFactory = visitor.configuration().spansFactory().get(image);
                if (spanFactory == null) {
                    visitor.visitChildren(image);
                    return;
                }

                final int length = visitor.length();

                visitor.visitChildren(image);

                // we must check if anything _was_ added, as we need at least one char to render
                if (length == visitor.length()) {
                    visitor.builder().append('\uFFFC');
                }

                final MarkwonConfiguration configuration = visitor.configuration();

                final Node parent = image.getParent();
                final boolean link = parent instanceof Link;

                final String destination = configuration
                        .urlProcessor()
                        .process(image.getDestination());

                final RenderProps props = visitor.renderProps();

                // apply image properties
                // Please note that we explicitly set IMAGE_SIZE to null as we do not clear
                // properties after we applied span (we could though)
                ImageProps.DESTINATION.set(props, destination);
                ImageProps.REPLACEMENT_TEXT_IS_LINK.set(props, link);
                ImageProps.IMAGE_SIZE.set(props, null);

                visitor.setSpans(length, spanFactory.getSpans(configuration, props));
            }
        });
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }
}
