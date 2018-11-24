package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Arrays;

import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.spans.OrderedListItemSpan;
import ru.noties.markwon.tasklist.TaskListExtension;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Markwon {

    /**
     * Helper method to obtain a Parser with registered strike-through &amp; table extensions
     * &amp; task lists (added in 1.0.1)
     *
     * @return a Parser instance that is supported by this library
     * @since 1.0.0
     */
    @NonNull
    public static Parser createParser() {
        return new Parser.Builder()
                .extensions(Arrays.asList(
                        StrikethroughExtension.create(),
                        TablesExtension.create(),
                        TaskListExtension.create()
                ))
                .build();
    }

    /**
     * @see #setMarkdown(TextView, MarkwonConfiguration, String)
     * @since 1.0.0
     */
    public static void setMarkdown(@NonNull TextView view, @NonNull String markdown) {
        setMarkdown(view, MarkwonConfiguration.create(view.getContext()), markdown);
    }

    /**
     * Parses submitted raw markdown, converts it to CharSequence (with Spannables)
     * and applies it to view
     *
     * @param view          {@link TextView} to set markdown into
     * @param configuration a {@link MarkwonConfiguration} instance
     * @param markdown      raw markdown String (for example: {@code `**Hello**`})
     * @see #markdown(MarkwonConfiguration, String)
     * @see #setText(TextView, CharSequence)
     * @see MarkwonConfiguration
     * @since 1.0.0
     */
    public static void setMarkdown(
            @NonNull TextView view,
            @NonNull MarkwonConfiguration configuration,
            @NonNull String markdown
    ) {

        setText(view, markdown(configuration, markdown));
    }

    /**
     * Helper method to apply parsed markdown.
     * <p>
     * Since 1.0.6 redirects it\'s call to {@link #setText(TextView, CharSequence, MovementMethod)}
     * with LinkMovementMethod as an argument to preserve current API.
     *
     * @param view {@link TextView} to set markdown into
     * @param text parsed markdown
     * @see #setText(TextView, CharSequence, MovementMethod)
     * @since 1.0.0
     */
    public static void setText(@NonNull TextView view, CharSequence text) {
        setText(view, text, LinkMovementMethod.getInstance());
    }

    /**
     * Helper method to apply parsed markdown with additional argument of a MovementMethod. Used
     * to workaround problems that occur when using system LinkMovementMethod (for example:
     * https://issuetracker.google.com/issues/37068143). As a better alternative to it consider
     * using: https://github.com/saket/Better-Link-Movement-Method
     *
     * @param view           TextView to set markdown into
     * @param text           parsed markdown
     * @param movementMethod an implementation if MovementMethod or null
     * @see #scheduleDrawables(TextView)
     * @see #scheduleTableRows(TextView)
     * @since 1.0.6
     */
    public static void setText(@NonNull TextView view, CharSequence text, @Nullable MovementMethod movementMethod) {

        unscheduleDrawables(view);
        unscheduleTableRows(view);

        // @since 2.0.1 we must measure ordered-list-item-spans before applying text to a TextView.
        // if markdown has a lot of ordered list items (or text size is relatively big, or block-margin
        // is relatively small) then this list won't be rendered properly: it will take correct
        // layout (width and margin) but will be clipped if margin is not _consistent_ between calls.
        OrderedListItemSpan.measure(view, text);

        // update movement method (for links to be clickable)
        view.setMovementMethod(movementMethod);
        view.setText(text);

        // schedule drawables (dynamic drawables that can change bounds/animate will be correctly updated)
        scheduleDrawables(view);
        scheduleTableRows(view);
    }

    /**
     * Returns parsed markdown with default {@link MarkwonConfiguration} obtained from {@link Context}
     *
     * @param context  {@link Context}
     * @param markdown raw markdown
     * @return parsed markdown
     * @since 1.0.0
     */
    @NonNull
    public static CharSequence markdown(@NonNull Context context, @NonNull String markdown) {
        final MarkwonConfiguration configuration = MarkwonConfiguration.create(context);
        return markdown(configuration, markdown);
    }

    /**
     * Returns parsed markdown with provided {@link MarkwonConfiguration}
     *
     * @param configuration a {@link MarkwonConfiguration}
     * @param markdown      raw markdown
     * @return parsed markdown
     * @see MarkwonConfiguration
     * @since 1.0.0
     */
    @NonNull
    public static CharSequence markdown(@NonNull MarkwonConfiguration configuration, @NonNull String markdown) {
        final Parser parser = createParser();
        final Node node = parser.parse(markdown);
        final SpannableRenderer renderer = new SpannableRenderer();
        return renderer.render(configuration, node);
    }

    /**
     * This method adds support for {@link ru.noties.markwon.spans.AsyncDrawable} to be used. As
     * textView seems not to support drawables that change bounds (and gives no means
     * to update the layout), we create own {@link android.graphics.drawable.Drawable.Callback}
     * and apply it. So, textView can display drawables, that are: async (loading from disk, network);
     * dynamic (requires `invalidate`) - GIF, animations.
     * Please note, that this method should be preceded with {@link #unscheduleDrawables(TextView)}
     * in order to avoid keeping drawables in memory after they have been removed from layout
     *
     * @param view a {@link TextView}
     * @see ru.noties.markwon.spans.AsyncDrawable
     * @see ru.noties.markwon.spans.AsyncDrawableSpan
     * @see DrawablesScheduler#schedule(TextView)
     * @see DrawablesScheduler#unschedule(TextView)
     * @since 1.0.0
     */
    public static void scheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.schedule(view);
    }

    /**
     * De-references previously scheduled {@link ru.noties.markwon.spans.AsyncDrawableSpan}&#39;s
     *
     * @param view a {@link TextView}
     * @see #scheduleDrawables(TextView)
     * @since 1.0.0
     */
    public static void unscheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.unschedule(view);
    }

    /**
     * This method is required in order to use tables. A bit of background:
     * this library uses a {@link android.text.style.ReplacementSpan} to
     * render tables, but the flow is not really flexible. We are required
     * to return `size` (width) of our replacement, but we are not provided
     * with the total one (canvas width). In order to correctly calculate height of our
     * table cell text, we must have available width first. This method gives
     * ability for {@link ru.noties.markwon.spans.TableRowSpan} to invalidate
     * `view` when it encounters such a situation (when available width is not known or have changed).
     * Precede this call with {@link #unscheduleTableRows(TextView)} in order to
     * de-reference previously scheduled {@link ru.noties.markwon.spans.TableRowSpan}&#39;s
     *
     * @param view a {@link TextView}
     * @see #unscheduleTableRows(TextView)
     * @since 1.0.0
     */
    public static void scheduleTableRows(@NonNull TextView view) {
        TableRowsScheduler.schedule(view);
    }

    /**
     * De-references previously scheduled {@link ru.noties.markwon.spans.TableRowSpan}&#39;s
     *
     * @param view a {@link TextView}
     * @see #scheduleTableRows(TextView)
     * @since 1.0.0
     */
    public static void unscheduleTableRows(@NonNull TextView view) {
        TableRowsScheduler.unschedule(view);
    }

    private Markwon() {
    }
}
