package ru.noties.markwon.image.okhttp;

import android.support.annotation.NonNull;

import java.util.Arrays;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.image.network.NetworkSchemeHandler;
import ru.noties.markwon.priority.Priority;

/**
 * Plugin to use OkHttpClient to obtain images from network (http and https schemes)
 *
 * @see #create()
 * @see #create(OkHttpClient)
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class OkHttpImagesPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static OkHttpImagesPlugin create() {
        return new OkHttpImagesPlugin(new OkHttpClient());
    }

    @NonNull
    public static OkHttpImagesPlugin create(@NonNull OkHttpClient okHttpClient) {
        return create((Call.Factory) okHttpClient);
    }

    @NonNull
    public static OkHttpImagesPlugin create(@NonNull Call.Factory callFactory) {
        return new OkHttpImagesPlugin(callFactory);
    }

    private final Call.Factory callFactory;

    OkHttpImagesPlugin(@NonNull Call.Factory callFactory) {
        this.callFactory = callFactory;
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
        builder.addSchemeHandler(
                Arrays.asList(NetworkSchemeHandler.SCHEME_HTTP, NetworkSchemeHandler.SCHEME_HTTPS),
                new OkHttpSchemeHandler(callFactory)
        );
    }

    @NonNull
    @Override
    public Priority priority() {
        return Priority.after(ImagesPlugin.class);
    }
}
