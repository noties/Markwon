package ru.noties.markwon.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        for (String type : mediaDecoder.supportedTypes()) {
            mediaDecoders.put(type, mediaDecoder);
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

        // we must have schemeHandlers registered (we will provide
        // default media decoder if it's absent)
        if (schemeHandlers.size() == 0) {
            return new AsyncDrawableLoaderNoOp();
        }

        // @since 4.0.0-SNAPSHOT
        if (defaultMediaDecoder == null) {
            defaultMediaDecoder = DefaultImageMediaDecoder.create();
        }

        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }

        return new AsyncDrawableLoaderImpl(this);
    }

}
