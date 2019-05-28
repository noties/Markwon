package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.concurrent.ExecutorService;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;

public class ImagesPlugin extends AbstractMarkwonPlugin {

    /**
     * @since 4.0.0-SNAPSHOT
     */
    public interface ImagesConfigure {
        void configureImages(@NonNull ImagesPlugin plugin);
    }

    /**
     * @since 4.0.0-SNAPSHOT
     */
    public interface PlaceholderProvider {
        @Nullable
        Drawable providePlaceholder(@NonNull AsyncDrawable drawable);
    }

    /**
     * @since 4.0.0-SNAPSHOT
     */
    public interface ErrorHandler {

        /**
         * Can optionally return a Drawable that will be displayed in case of an error
         */
        @Nullable
        Drawable handleError(@NonNull String url, @NonNull Throwable throwable);
    }

    /**
     * Factory method to create an empty {@link ImagesPlugin} instance with no {@link SchemeHandler}s
     * nor {@link MediaDecoder}s registered. Can be used to further configure via instance methods or
     * via {@link ru.noties.markwon.MarkwonPlugin#configure(Registry)}
     */
    @NonNull
    public static ImagesPlugin createEmpty() {
        return new ImagesPlugin();
    }

    @NonNull
    public static ImagesPlugin create(@NonNull ImagesConfigure configure) {
        final ImagesPlugin plugin = new ImagesPlugin();
        configure.configureImages(plugin);
        return plugin;
    }

    private final AsyncDrawableLoaderBuilder builder = new AsyncDrawableLoaderBuilder();

    /**
     * Optional (by default new cached thread executor will be used)
     *
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public ImagesPlugin executorService(@NonNull ExecutorService executorService) {
        checkBuilderState();
        builder.executorService(executorService);
        return this;
    }

    /**
     * @see SchemeHandler
     * @see ru.noties.markwon.image.data.DataUriSchemeHandler
     * @see ru.noties.markwon.image.file.FileSchemeHandler
     * @see ru.noties.markwon.image.network.NetworkSchemeHandler
     * @see ru.noties.markwon.image.network.OkHttpNetworkSchemeHandler
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public ImagesPlugin addSchemeHandler(@NonNull SchemeHandler schemeHandler) {
        checkBuilderState();
        builder.addSchemeHandler(schemeHandler);
        return this;
    }

    @NonNull
    public ImagesPlugin addMediaDecoder(@NonNull MediaDecoder mediaDecoder) {
        checkBuilderState();
        builder.addMediaDecoder(mediaDecoder);
        return this;
    }

    @NonNull
    public ImagesPlugin defaultMediaDecoder(@Nullable MediaDecoder mediaDecoder) {
        checkBuilderState();
        builder.defaultMediaDecoder(mediaDecoder);
        return this;
    }

    @NonNull
    public ImagesPlugin removeSchemeHandler(@NonNull String scheme) {
        checkBuilderState();
        builder.removeSchemeHandler(scheme);
        return this;
    }

    @NonNull
    public ImagesPlugin removeMediaDecoder(@NonNull String contentType) {
        checkBuilderState();
        builder.removeMediaDecoder(contentType);
        return this;
    }

    @NonNull
    public ImagesPlugin placeholderProvider(@NonNull PlaceholderProvider placeholderProvider) {
        checkBuilderState();
        builder.placeholderProvider(placeholderProvider);
        return this;
    }

    @NonNull
    public ImagesPlugin errorHandler(@NonNull ErrorHandler errorHandler) {
        checkBuilderState();
        builder.errorHandler(errorHandler);
        return this;
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        checkBuilderState();
        builder.asyncDrawableLoader(this.builder.build());
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
                final SpanFactory spanFactory = visitor.configuration().spansFactory().get(Image.class);
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

    private void checkBuilderState() {
        if (builder.isBuilt) {
            throw new IllegalStateException("ImagesPlugin has already been configured " +
                    "and cannot be modified any further");
        }
    }
}
