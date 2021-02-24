package io.noties.markwon.sample.annotations;

import androidx.annotation.NonNull;

import java.util.Locale;

public enum MarkwonArtifact {
    CORE,
    EDITOR,
    EXT_LATEX,
    EXT_STRIKETHROUGH,
    EXT_TABLES,
    EXT_TASKLIST,
    HTML,
    IMAGE,
    IMAGE_COIL,
    IMAGE_GLIDE,
    IMAGE_PICASSO,
    INLINE_PARSER,
    LINKIFY,
    RECYCLER,
    RECYCLER_TABLE,
    SIMPLE_EXT,
    SYNTAX_HIGHLIGHT;

    @NonNull
    public String artifactName() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
