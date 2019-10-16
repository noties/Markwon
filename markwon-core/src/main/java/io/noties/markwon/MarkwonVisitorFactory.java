package io.noties.markwon;

import androidx.annotation.NonNull;

/**
 * @since 4.1.1
 */
abstract class MarkwonVisitorFactory {

    @NonNull
    abstract MarkwonVisitor create();

    @NonNull
    static MarkwonVisitorFactory create(
            @NonNull final MarkwonVisitorImpl.Builder builder,
            @NonNull final MarkwonConfiguration configuration) {
        return new MarkwonVisitorFactory() {
            @NonNull
            @Override
            MarkwonVisitor create() {
                return builder.build(configuration, new RenderPropsImpl());
            }
        };
    }
}
