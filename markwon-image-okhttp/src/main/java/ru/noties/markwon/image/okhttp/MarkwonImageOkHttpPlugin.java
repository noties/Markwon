package ru.noties.markwon.image.okhttp;

import android.support.annotation.NonNull;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.network.NetworkSchemeHandler;

/**
 * Plugin to use OkHttpClient to obtain images from network (http and https schemes)
 *
 * @see #create()
 * @see #create(OkHttpClient)
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class MarkwonImageOkHttpPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static MarkwonImageOkHttpPlugin create() {
        return new MarkwonImageOkHttpPlugin(new OkHttpClient());
    }

    @NonNull
    public static MarkwonImageOkHttpPlugin create(@NonNull OkHttpClient okHttpClient) {
        return new MarkwonImageOkHttpPlugin(okHttpClient);
    }

    private final OkHttpClient client;

    MarkwonImageOkHttpPlugin(@NonNull OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
        builder.addSchemeHandler(
                Arrays.asList(NetworkSchemeHandler.SCHEME_HTTP, NetworkSchemeHandler.SCHEME_HTTPS),
                new OkHttpSchemeHandler(client)
        );
    }
}
