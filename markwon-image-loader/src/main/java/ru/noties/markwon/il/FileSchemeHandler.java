package ru.noties.markwon.il;

import android.content.res.AssetManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @since 2.0.0
 */
public class FileSchemeHandler extends SchemeHandler {

    @NonNull
    public static FileSchemeHandler createWithAssets(@NonNull AssetManager assetManager) {
        return new FileSchemeHandler(assetManager);
    }

    @NonNull
    public static FileSchemeHandler create() {
        return new FileSchemeHandler(null);
    }

    private static final String FILE_ANDROID_ASSETS = "android_asset";

    @Nullable
    private final AssetManager assetManager;

    @SuppressWarnings("WeakerAccess")
    FileSchemeHandler(@Nullable AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Nullable
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        final List<String> segments = uri.getPathSegments();
        if (segments == null
                || segments.size() == 0) {
            // pointing to file & having no path segments is no use
            return null;
        }

        final ImageItem out;

        InputStream inputStream = null;

        final boolean assets = FILE_ANDROID_ASSETS.equals(segments.get(0));
        final String fileName = uri.getLastPathSegment();

        if (assets) {

            // no handling of assets here if we have no assetsManager
            if (assetManager != null) {

                final StringBuilder path = new StringBuilder();
                for (int i = 1, size = segments.size(); i < size; i++) {
                    if (i != 1) {
                        path.append('/');
                    }
                    path.append(segments.get(i));
                }
                // load assets

                try {
                    inputStream = assetManager.open(path.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            try {
                inputStream = new BufferedInputStream(new FileInputStream(new File(uri.getPath())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (inputStream != null) {
            out = new ImageItem(fileName, inputStream, fileName);
        } else {
            out = null;
        }

        return out;
    }

    @Override
    public void cancel(@NonNull String raw) {
        // no op
    }

    @NonNull
    @Override
    public Collection<String> schemes() {
        return Collections.singleton("file");
    }
}
