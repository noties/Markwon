package io.noties.markwon.image.destination;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @since 4.4.0
 */
public class ImageDestinationProcessorRelativeToAbsolute extends ImageDestinationProcessor {

    @NonNull
    public static ImageDestinationProcessorRelativeToAbsolute create(@NonNull String base) {
        return new ImageDestinationProcessorRelativeToAbsolute(base);
    }

    public static ImageDestinationProcessorRelativeToAbsolute create(@NonNull URL base) {
        return new ImageDestinationProcessorRelativeToAbsolute(base);
    }

    private final URL base;

    public ImageDestinationProcessorRelativeToAbsolute(@NonNull String base) {
        this.base = obtain(base);
    }

    public ImageDestinationProcessorRelativeToAbsolute(@NonNull URL base) {
        this.base = base;
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {

        String out = destination;

        if (base != null) {
            try {
                final URL u = new URL(base, destination);
                out = u.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Nullable
    private static URL obtain(String base) {
        try {
            return new URL(base);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
