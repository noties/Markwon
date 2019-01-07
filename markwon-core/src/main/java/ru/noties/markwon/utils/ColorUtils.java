package ru.noties.markwon.utils;

public abstract class ColorUtils {

    public static int applyAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private ColorUtils() {
    }
}
