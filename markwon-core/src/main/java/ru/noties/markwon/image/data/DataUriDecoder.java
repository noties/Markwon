package ru.noties.markwon.image.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

public abstract class DataUriDecoder {

    @Nullable
    public abstract byte[] decode(@NonNull DataUri dataUri);

    @NonNull
    public static DataUriDecoder create() {
        return new Impl();
    }

    static class Impl extends DataUriDecoder {

        @Nullable
        @Override
        public byte[] decode(@NonNull DataUri dataUri) {

            final String data = dataUri.data();

            if (!TextUtils.isEmpty(data)) {
                try {
                    if (dataUri.base64()) {
                        return Base64.decode(data.getBytes("UTF-8"), Base64.DEFAULT);
                    } else {
                        return data.getBytes("UTF-8");
                    }
                } catch (Throwable t) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
