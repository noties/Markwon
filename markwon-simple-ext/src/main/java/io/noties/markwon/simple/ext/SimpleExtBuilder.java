package io.noties.markwon.simple.ext;

import androidx.annotation.NonNull;

import org.commonmark.parser.delimiter.DelimiterProcessor;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.SpanFactory;

// @since 4.0.0
class SimpleExtBuilder {

    private final List<DelimiterProcessor> extensions = new ArrayList<>(2);

    private boolean isBuilt;

    void addExtension(
            int length,
            char character,
            @NonNull SpanFactory spanFactory) {

        checkState();

        extensions.add(new SimpleExtDelimiterProcessor(
                character,
                character,
                length,
                spanFactory));
    }

    void addExtension(
            int length,
            char openingCharacter,
            char closingCharacter,
            @NonNull SpanFactory spanFactory) {

        checkState();

        extensions.add(new SimpleExtDelimiterProcessor(
                openingCharacter,
                closingCharacter,
                length,
                spanFactory));
    }

    @NonNull
    List<DelimiterProcessor> build() {

        checkState();

        isBuilt = true;

        return extensions;
    }

    private void checkState() {
        if (isBuilt) {
            throw new IllegalStateException("SimpleExtBuilder is already built, " +
                    "do not mutate SimpleExtPlugin after configuration is finished");
        }
    }
}
