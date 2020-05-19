package io.noties.markwon.image.destination;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link ImageDestinationProcessor} that treats all destinations <strong>without scheme</strong>
 * information as pointing to the {@code assets} folder of an application. Please note that this
 * processor only adds required {@code file:///android_asset/} prefix to destinations and
 * actual image loading must take that into account (implement this functionality).
 * <p>
 * {@code FileSchemeHandler} from the {@code image} module supports asset images when created with
 * {@code createWithAssets} factory method
 *
 * @since 4.4.0
 */
public class ImageDestinationProcessorAssets extends ImageDestinationProcessor {

    @NonNull
    public static ImageDestinationProcessorAssets create(@Nullable ImageDestinationProcessor parent) {
        return new ImageDestinationProcessorAssets(parent);
    }

    static final String MOCK = "https://android.asset/";
    static final String BASE = "file:///android_asset/";

    private final ImageDestinationProcessorRelativeToAbsolute assetsProcessor
            = new ImageDestinationProcessorRelativeToAbsolute(MOCK);

    private final ImageDestinationProcessor processor;

    public ImageDestinationProcessorAssets() {
        this(null);
    }

    public ImageDestinationProcessorAssets(@Nullable ImageDestinationProcessor parent) {
        this.processor = parent;
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {
        final String out;
        final Uri uri = Uri.parse(destination);
        if (TextUtils.isEmpty(uri.getScheme())) {
            out = assetsProcessor.process(destination).replace(MOCK, BASE);
        } else {
            if (processor != null) {
                out = processor.process(destination);
            } else {
                out = destination;
            }
        }
        return out;
    }
}
