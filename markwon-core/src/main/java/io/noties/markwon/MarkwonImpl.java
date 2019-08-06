package io.noties.markwon;

import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @since 3.0.0
 */
class MarkwonImpl extends Markwon {

    private final TextView.BufferType bufferType;
    private final Parser parser;
    private final MarkwonVisitor visitor;
    private final List<MarkwonPlugin> plugins;

    // @since 4.1.0
    @Nullable
    private final TextSetter textSetter;

    MarkwonImpl(
            @NonNull TextView.BufferType bufferType,
            @Nullable TextSetter textSetter,
            @NonNull Parser parser,
            @NonNull MarkwonVisitor visitor,
            @NonNull List<MarkwonPlugin> plugins) {
        this.bufferType = bufferType;
        this.textSetter = textSetter;
        this.parser = parser;
        this.visitor = visitor;
        this.plugins = plugins;
    }

    @NonNull
    @Override
    public Node parse(@NonNull String input) {

        // make sure that all plugins are called `processMarkdown` before parsing
        for (MarkwonPlugin plugin : plugins) {
            input = plugin.processMarkdown(input);
        }

        return parser.parse(input);
    }

    @NonNull
    @Override
    public Spanned render(@NonNull Node node) {

        for (MarkwonPlugin plugin : plugins) {
            plugin.beforeRender(node);
        }

        node.accept(visitor);

        for (MarkwonPlugin plugin : plugins) {
            plugin.afterRender(node, visitor);
        }

        final Spanned spanned = visitor.builder().spannableStringBuilder();

        // clear render props and builder after rendering
        visitor.clear();

        return spanned;
    }

    @NonNull
    @Override
    public Spanned toMarkdown(@NonNull String input) {
        return render(parse(input));
    }

    @Override
    public void setMarkdown(@NonNull TextView textView, @NonNull String markdown) {
        setParsedMarkdown(textView, toMarkdown(markdown));
    }

    @Override
    public void setParsedMarkdown(@NonNull final TextView textView, @NonNull Spanned markdown) {

        for (MarkwonPlugin plugin : plugins) {
            plugin.beforeSetText(textView, markdown);
        }

        // @since 4.1.0
        if (textSetter != null) {
            textSetter.setText(textView, markdown, bufferType, new Runnable() {
                @Override
                public void run() {
                    // on-complete we just must call `afterSetText` on all plugins
                    for (MarkwonPlugin plugin : plugins) {
                        plugin.afterSetText(textView);
                    }
                }
            });
        } else {

            // if no text-setter is specified -> just a regular sync operation
            textView.setText(markdown, bufferType);

            for (MarkwonPlugin plugin : plugins) {
                plugin.afterSetText(textView);
            }
        }
    }

    @Override
    public boolean hasPlugin(@NonNull Class<? extends MarkwonPlugin> type) {
        return getPlugin(type) != null;
    }

    @Nullable
    @Override
    public <P extends MarkwonPlugin> P getPlugin(@NonNull Class<P> type) {
        MarkwonPlugin out = null;
        for (MarkwonPlugin plugin : plugins) {
            if (type.isAssignableFrom(plugin.getClass())) {
                out = plugin;
            }
        }
        //noinspection unchecked
        return (P) out;
    }

    @NonNull
    @Override
    public <P extends MarkwonPlugin> P requirePlugin(@NonNull Class<P> type) {
        final P plugin = getPlugin(type);
        if (plugin == null) {
            throw new IllegalStateException(String.format(Locale.US, "Requested plugin `%s` is not " +
                    "registered with this Markwon instance", type.getName()));
        }
        return plugin;
    }

    @NonNull
    @Override
    public List<? extends MarkwonPlugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
}
