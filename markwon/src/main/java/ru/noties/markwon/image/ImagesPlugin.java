package ru.noties.markwon.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.Arrays;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.core.spans.AsyncDrawableSpan;
import ru.noties.markwon.image.data.DataUriSchemeHandler;
import ru.noties.markwon.image.file.FileSchemeHandler;
import ru.noties.markwon.image.network.NetworkSchemeHandler;

public class ImagesPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static ImagesPlugin create(@NonNull Context context) {
        return new ImagesPlugin(context, false);
    }

    @NonNull
    public static ImagesPlugin createWithAssets(@NonNull Context context) {
        return new ImagesPlugin(context, true);
    }

    private final Context context;
    private final boolean useAssets;

    private ImagesPlugin(Context context, boolean useAssets) {
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
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Image.class, new MarkwonVisitor.NodeVisitor<Image>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Image image) {

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

                final Object spans = imageSpan(
                        visitor.theme(),
                        destination,
                        configuration.asyncDrawableLoader(),
                        configuration.imageSizeResolver(),
                        link);

                visitor.setSpans(length, spans);
            }
        });
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    @Nullable
    protected Object imageSpan(
            @NonNull MarkwonTheme theme,
            @NonNull String destination,
            @NonNull AsyncDrawableLoader loader,
            @NonNull ImageSizeResolver imageSizeResolver,
            boolean replacementTextIsLink) {
        return new AsyncDrawableSpan(
                theme,
                new AsyncDrawable(
                        destination,
                        loader,
                        imageSizeResolver,
                        null
                ),
                AsyncDrawableSpan.ALIGN_BOTTOM,
                replacementTextIsLink
        );
    }
}
