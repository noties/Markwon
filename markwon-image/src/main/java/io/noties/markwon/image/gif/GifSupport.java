package io.noties.markwon.image.gif;

/**
 * @since 4.0.0-SNAPSHOT
 */
public abstract class GifSupport {

    private static boolean HAS_GIF;

    static {
        boolean result;
        try {
            pl.droidsonroids.gif.GifDrawable.class.getName();
            result = true;
        } catch (Throwable t) {
            t.printStackTrace();
            result = false;
        }
        HAS_GIF = result;
    }

    public static boolean hasGifSupport() {
        return HAS_GIF;
    }

    private GifSupport() {
    }
}
