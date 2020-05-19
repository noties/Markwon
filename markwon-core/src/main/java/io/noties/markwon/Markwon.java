package io.noties.markwon;

import android.content.Context;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;

import java.util.List;

import io.noties.markwon.core.CorePlugin;

/**
 * Class to parse and render markdown. Since version 3.0.0 instance specific (previously consisted
 * of static stateless methods). An instance of builder can be obtained via {@link #builder(Context)}
 * method.
 *
 * @see #create(Context)
 * @see #builder(Context)
 * @see Builder
 */
public abstract class Markwon {

    /**
     * Factory method to create a <em>minimally</em> functional {@link Markwon} instance. This
     * instance will have <strong>only</strong> {@link CorePlugin} registered. If you wish
     * to configure this instance more consider using {@link #builder(Context)} method.
     *
     * @return {@link Markwon} instance with only CorePlugin registered
     * @since 3.0.0
     */
    @NonNull
    public static Markwon create(@NonNull Context context) {
        return builder(context)
                .usePlugin(CorePlugin.create())
                .build();
    }

    /**
     * Factory method to obtain an instance of {@link Builder} with {@link CorePlugin} added.
     *
     * @see Builder
     * @see #builderNoCore(Context)
     * @since 3.0.0
     */
    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new MarkwonBuilderImpl(context)
                // @since 4.0.0 add CorePlugin
                .usePlugin(CorePlugin.create());
    }

    /**
     * Factory method to obtain an instance of {@link Builder} without {@link CorePlugin}.
     *
     * @since 4.0.0
     */
    @NonNull
    public static Builder builderNoCore(@NonNull Context context) {
        return new MarkwonBuilderImpl(context);
    }

    /**
     * Method to parse markdown (without rendering)
     *
     * @param input markdown input to parse
     * @return parsed via commonmark-java <code>org.commonmark.node.Node</code>
     * @see #render(Node)
     * @since 3.0.0
     */
    @NonNull
    public abstract Node parse(@NonNull String input);

    /**
     * Create Spanned markdown from parsed Node (via {@link #parse(String)} call).
     * <p>
     * Please note that returned Spanned has few limitations. For example, images, tables
     * and ordered lists require TextView to be properly displayed. This is why images and tables
     * most likely won\'t work in this case. Ordered lists might have mis-measurements. Whenever
     * possible use {@link #setMarkdown(TextView, String)} or {@link #setParsedMarkdown(TextView, Spanned)}
     * as these methods will additionally call specific {@link MarkwonPlugin} methods to <em>prepare</em>
     * proper display.
     *
     * @since 3.0.0
     */
    @NonNull
    public abstract Spanned render(@NonNull Node node);

    /**
     * This method will {@link #parse(String)} and {@link #render(Node)} supplied markdown. Returned
     * Spanned has the same limitations as from {@link #render(Node)} method.
     *
     * @param input markdown input
     * @see #parse(String)
     * @see #render(Node)
     * @since 3.0.0
     */
    @NonNull
    public abstract Spanned toMarkdown(@NonNull String input);

    public abstract void setMarkdown(@NonNull TextView textView, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull TextView textView, @NonNull Spanned markdown);

    /**
     * Requests information if certain plugin has been registered. Please note that this
     * method will check for super classes also, so if supplied with {@code markwon.hasPlugin(MarkwonPlugin.class)}
     * this method (if has at least one plugin) will return true. If for example a custom
     * (subclassed) version of a {@link CorePlugin} has been registered and given name
     * {@code CorePlugin2}, then both {@code markwon.hasPlugin(CorePlugin2.class)} and
     * {@code markwon.hasPlugin(CorePlugin.class)} will return true.
     *
     * @param plugin type to query
     * @return true if a plugin is used when configuring this {@link Markwon} instance
     */
    public abstract boolean hasPlugin(@NonNull Class<? extends MarkwonPlugin> plugin);

    @Nullable
    public abstract <P extends MarkwonPlugin> P getPlugin(@NonNull Class<P> type);

    /**
     * @since 4.1.0
     */
    @NonNull
    public abstract <P extends MarkwonPlugin> P requirePlugin(@NonNull Class<P> type);

    /**
     * @return a list of registered {@link MarkwonPlugin}
     * @since 4.1.0
     */
    @NonNull
    public abstract List<? extends MarkwonPlugin> getPlugins();

    @NonNull
    public abstract MarkwonConfiguration configuration();

    /**
     * Interface to set text on a TextView. Primary goal is to give a way to use PrecomputedText
     * functionality
     *
     * @see PrecomputedTextSetterCompat
     * @since 4.1.0
     */
    public interface TextSetter {
        /**
         * @param textView   TextView
         * @param markdown   prepared markdown
         * @param bufferType BufferType specified when building {@link Markwon} instance
         *                   via {@link Builder#bufferType(TextView.BufferType)}
         * @param onComplete action to run when set-text is finished (required to call in order
         *                   to execute {@link MarkwonPlugin#afterSetText(TextView)})
         */
        void setText(
                @NonNull TextView textView,
                @NonNull Spanned markdown,
                @NonNull TextView.BufferType bufferType,
                @NonNull Runnable onComplete);
    }

    /**
     * Builder for {@link Markwon}.
     * <p>
     * Please note that the order in which plugins are supplied is important as this order will be
     * used through the whole usage of built Markwon instance
     *
     * @since 3.0.0
     */
    public interface Builder {

        /**
         * Specify bufferType when applying text to a TextView {@code textView.setText(CharSequence,BufferType)}.
         * By default `BufferType.SPANNABLE` is used
         *
         * @param bufferType BufferType
         */
        @NonNull
        Builder bufferType(@NonNull TextView.BufferType bufferType);

        /**
         * @param textSetter {@link TextSetter} to apply text to a TextView
         * @since 4.1.0
         */
        @NonNull
        Builder textSetter(@NonNull TextSetter textSetter);

        @NonNull
        Builder usePlugin(@NonNull MarkwonPlugin plugin);

        @NonNull
        Builder usePlugins(@NonNull Iterable<? extends MarkwonPlugin> plugins);

        /**
         * Control if small chunks of non-finished markdown sentences (for example, a single `*` character)
         * should be displayed/rendered as raw input instead of an empty string.
         * <p>
         * Since 4.4.0 {@code true} by default, versions prior - {@code false}
         *
         * @since 4.4.0
         */
        @NonNull
        Builder fallbackToRawInputWhenEmpty(boolean fallbackToRawInputWhenEmpty);

        @NonNull
        Markwon build();
    }
}
