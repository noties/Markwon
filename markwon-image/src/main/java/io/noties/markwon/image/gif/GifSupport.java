package io.noties.markwon.image.gif;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @since 4.0.0
 */
public abstract class GifSupport {

    private static boolean HAS_GIF;

    static {
        boolean result;
        try {
            pl.droidsonroids.gif.GifDrawable.class.getName();
            result = true;
        } catch (Throwable t) {
            // @since 4.1.1-SNAPSHOT instead of printing full stacktrace of the exception,
            // just print a warning to the console
            Log.w("MarkwonImagesPlugin", missingMessage());
            result = false;
        }
        HAS_GIF = result;
    }

    public static boolean hasGifSupport() {
        return HAS_GIF;
    }

    /**
     * @since 4.1.1-SNAPSHOT
     */
    @NonNull
    static String missingMessage() {
        return "`pl.droidsonroids.gif:android-gif-drawable:*` " +
                "dependency is missing, please add to your project explicitly if you " +
                "wish to use GIF media-decoder";
    }

    private GifSupport() {
    }
}
