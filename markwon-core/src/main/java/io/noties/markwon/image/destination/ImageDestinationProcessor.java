package io.noties.markwon.image.destination;

import androidx.annotation.NonNull;

/**
 * Process destination of image nodes
 *
 * @since 4.4.0
 */
public abstract class ImageDestinationProcessor {
    @NonNull
    public abstract String process(@NonNull String destination);

    @NonNull
    public static ImageDestinationProcessor noOp() {
        return new NoOp();
    }

    private static class NoOp extends ImageDestinationProcessor {

        @NonNull
        @Override
        public String process(@NonNull String destination) {
            return destination;
        }
    }
}
