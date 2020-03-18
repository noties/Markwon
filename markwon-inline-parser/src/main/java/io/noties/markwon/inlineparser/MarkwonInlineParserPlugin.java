package io.noties.markwon.inlineparser;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;

/**
 * @since 4.3.0
 */
public class MarkwonInlineParserPlugin extends AbstractMarkwonPlugin {

    public interface BuilderConfigure<B extends MarkwonInlineParser.FactoryBuilder> {
        void configureBuilder(@NonNull B factoryBuilder);
    }

    @NonNull
    public static MarkwonInlineParserPlugin create() {
        return create(MarkwonInlineParser.factoryBuilder());
    }

    @NonNull
    public static MarkwonInlineParserPlugin create(@NonNull BuilderConfigure<MarkwonInlineParser.FactoryBuilder> configure) {
        final MarkwonInlineParser.FactoryBuilder factoryBuilder = MarkwonInlineParser.factoryBuilder();
        configure.configureBuilder(factoryBuilder);
        return new MarkwonInlineParserPlugin(factoryBuilder);
    }

    @NonNull
    public static MarkwonInlineParserPlugin create(@NonNull MarkwonInlineParser.FactoryBuilder factoryBuilder) {
        return new MarkwonInlineParserPlugin(factoryBuilder);
    }

    @NonNull
    public static <B extends MarkwonInlineParser.FactoryBuilder> MarkwonInlineParserPlugin create(
            @NonNull B factoryBuilder,
            @NonNull BuilderConfigure<B> configure) {
        configure.configureBuilder(factoryBuilder);
        return new MarkwonInlineParserPlugin(factoryBuilder);
    }

    private final MarkwonInlineParser.FactoryBuilder factoryBuilder;

    @SuppressWarnings("WeakerAccess")
    MarkwonInlineParserPlugin(@NonNull MarkwonInlineParser.FactoryBuilder factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.inlineParserFactory(factoryBuilder.build());
    }

    @NonNull
    public MarkwonInlineParser.FactoryBuilder factoryBuilder() {
        return factoryBuilder;
    }
}
