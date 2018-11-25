package ru.noties.markwon.image;

import android.content.Context;
import android.support.annotation.NonNull;

import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.Arrays;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
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

                final Object spans = visitor.factory().image(
                        visitor.theme(),
                        destination,
                        configuration.asyncDrawableLoader(),
                        configuration.imageSizeResolver(),
                        null,
                        link);

                visitor.setSpans(length, spans);
            }
        });
    }
}
