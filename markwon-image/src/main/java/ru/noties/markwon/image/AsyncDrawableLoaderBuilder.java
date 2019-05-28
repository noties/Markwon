package ru.noties.markwon.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AsyncDrawableLoaderBuilder {

    ExecutorService executorService;
    final Map<String, SchemeHandler> schemeHandlers = new HashMap<>(3);
    final Map<String, MediaDecoder> mediaDecoders = new HashMap<>(3);
    MediaDecoder defaultMediaDecoder;
    ImagesPlugin.PlaceholderProvider placeholderProvider;
    ImagesPlugin.ErrorHandler errorHandler;

    boolean isBuilt;

    void executorService(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    void addSchemeHandler(@NonNull SchemeHandler schemeHandler) {
        for (String scheme : schemeHandler.supportedSchemes()) {
            schemeHandlers.put(scheme, schemeHandler);
        }
    }

    void addMediaDecoder(@NonNull MediaDecoder mediaDecoder) {
        final Collection<String> supportedTypes = mediaDecoder.supportedTypes();
        if (supportedTypes.isEmpty()) {
            // todo: we should think about this little _side-effect_... does it worth it?
            defaultMediaDecoder = mediaDecoder;
        } else {
            for (String type : supportedTypes) {
                mediaDecoders.put(type, mediaDecoder);
            }
        }
    }

    void defaultMediaDecoder(@Nullable MediaDecoder mediaDecoder) {
        this.defaultMediaDecoder = mediaDecoder;
    }

    void removeSchemeHandler(@NonNull String scheme) {
        schemeHandlers.remove(scheme);
    }

    void removeMediaDecoder(@NonNull String contentType) {
        mediaDecoders.remove(contentType);
    }

    /**
     * @since 3.0.0
     */
    void placeholderProvider(@NonNull ImagesPlugin.PlaceholderProvider placeholderDrawableProvider) {
        this.placeholderProvider = placeholderDrawableProvider;
    }

    /**
     * @since 3.0.0
     */
    void errorHandler(@NonNull ImagesPlugin.ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @NonNull
    AsyncDrawableLoader build() {

        isBuilt = true;

        // if we have no schemeHandlers -> we cannot show anything
        // OR if we have no media decoders
        if (schemeHandlers.size() == 0
                || (mediaDecoders.size() == 0 && defaultMediaDecoder == null)) {
            return new AsyncDrawableLoaderNoOp();
        }

        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }

        return new AsyncDrawableLoaderImpl(this);
    }

}
