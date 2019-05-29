package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Image;

import java.util.concurrent.ExecutorService;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;

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
