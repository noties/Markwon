package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.MediaDecoder;
import ru.noties.markwon.image.SchemeHandler;

/**
 * Class represents a plugin (extension) to Markwon to configure how parsing and rendering
 * of markdown is carried on.
 *
 * @see AbstractMarkwonPlugin
 * @see ru.noties.markwon.core.CorePlugin
 * @see ru.noties.markwon.image.ImagesPlugin
 * @since 3.0.0
 */
public interface MarkwonPlugin {

    /**
     * Method to configure <code>org.commonmark.parser.Parser</code> (for example register custom
     * extension, etc).
     */
    void configureParser(@NonNull Parser.Builder builder);

    /**
     * Modify {@link MarkwonTheme} that is used for rendering of markdown.
     *
     * @see MarkwonTheme
     * @see MarkwonTheme.Builder
     */
    void configureTheme(@NonNull MarkwonTheme.Builder builder);

    /**
     * Configure image loading functionality. For example add new content-types
     * {@link AsyncDrawableLoader.Builder#addMediaDecoder(String, MediaDecoder)}, a transport
     * layer (network, file, etc) {@link AsyncDrawableLoader.Builder#addSchemeHandler(String, SchemeHandler)}
     * or modify existing properties.
     *
     * @see AsyncDrawableLoader
     * @see AsyncDrawableLoader.Builder
     */
    void configureImages(@NonNull AsyncDrawableLoader.Builder builder);

    /**
     * Configure {@link MarkwonConfiguration}
     *
     * @see MarkwonConfiguration
     * @see MarkwonConfiguration.Builder
     */
    void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder);

    /**
     * Configure {@link MarkwonVisitor} to accept new node types or override already registered nodes.
     *
     * @see MarkwonVisitor
     * @see MarkwonVisitor.Builder
     */
    void configureVisitor(@NonNull MarkwonVisitor.Builder builder);

    /**
     * Configure {@link MarkwonSpansFactory} to change what spans are used for certain node types.
     *
     * @see MarkwonSpansFactory
     * @see MarkwonSpansFactory.Builder
     */
    void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder);

    // can be used to configure own properties and use between plugins

    /**
     * A method to store some arbitrary data in {@link RenderProps}. Although it won\'t make
     * much sense to use existing {@link Prop} keys for {@link SpanFactory}, it can be helpful
     * to establish a communication channel between multiple plugins in decoupled way (provide
     * some initial properties for example or indicate that certain plugin is registered).
     * <p>
     * This method will be called before <em>each</em> rendering step (after rendering {@link RenderProps}
     * will be cleared. This method <strong>won\'t</strong> be called during initialization stage.
     *
     * @see RenderProps
     */
    void configureRenderProps(@NonNull RenderProps renderProps);

    /**
     * Process input markdown and return new string to be used in parsing stage further.
     * Can be described as <code>pre-processing</code> of markdown String.
     *
     * @param markdown String to process
     * @return processed markdown String
     */
    @NonNull
    String processMarkdown(@NonNull String markdown);

    /**
     * This method will be called <strong>before</strong> rendering will occur thus making possible
     * to <code>post-process</code> parsed node (make changes for example).
     *
     * @param node root parsed org.commonmark.node.Node
     */
    void beforeRender(@NonNull Node node);

    /**
     * This method will be called <strong>after</strong> rendering (but before applying markdown to a
     * TextView, if such action will happen). It can be used to clean some
     * internal state, or trigger certain action. Please note that modifying <code>node</code> won\'t
     * have any effect as it has been already <i>visited</i> at this stage.
     *
     * @param node    root parsed org.commonmark.node.Node
     * @param visitor {@link MarkwonVisitor} instance used to render markdown
     */
    void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor);

    /**
     * This method will be called <strong>before</strong> calling <code>TextView#setText</code>.
     * <p>
     * It can be useful to prepare a TextView for markdown. For example {@link ru.noties.markwon.image.ImagesPlugin}
     * uses this method to unregister previously registered {@link ru.noties.markwon.image.AsyncDrawableSpan}
     * (if there are such spans in this TextView at this point). Or {@link ru.noties.markwon.core.CorePlugin}
     * which measures ordered list numbers
     *
     * @param textView TextView to which <code>markdown</code> will be applied
     * @param markdown Parsed markdown
     */
    void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown);

    /**
     * This method will be called <strong>after</strong> markdown was applied.
     * <p>
     * It can be useful to trigger certain action on spans/textView. For example {@link ru.noties.markwon.image.ImagesPlugin}
     * uses this method to register {@link ru.noties.markwon.image.AsyncDrawableSpan} and start
     * asynchronously loading images.
     * <p>
     * Unlike {@link #beforeSetText(TextView, Spanned)} this method does not receive parsed markdown
     * as at this point spans must be queried by calling <code>TextView#getText#getSpans</code>.
     *
     * @param textView TextView to which markdown was applied
     */
    void afterSetText(@NonNull TextView textView);
}
