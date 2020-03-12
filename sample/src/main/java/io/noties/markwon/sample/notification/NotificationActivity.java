package io.noties.markwon.sample.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Emphasis;
import org.commonmark.node.Heading;
import org.commonmark.node.ListItem;
import org.commonmark.node.StrongEmphasis;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class NotificationActivity extends ActivityWithMenuOptions {

    private static final String CHANNEL_ID = "whatever";

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("bold-italic", this::bold_italic)
                .add("heading", this::heading)
                .add("lists", this::lists)
                .add("image", this::image)
                .add("link", this::link)
                .add("blockquote", this::blockquote)
                .add("strikethrough", this::strikethrough);
    }

    private void bold_italic() {
        // Unfortunately we cannot just use Markwon created CharSequence in a RemoteViews context
        //  because it requires for spans to be platform ones

        final String md = "Just a **bold** here and _italic_, but what if **it is bold _and italic_**?";
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder
                                .setFactory(StrongEmphasis.class, (configuration, props) -> new StyleSpan(Typeface.BOLD))
                                .setFactory(Emphasis.class, (configuration, props) -> new StyleSpan(Typeface.ITALIC));
                    }
                })
                .build();
        display(markwon.toMarkdown(md));
    }

    private void heading() {

        // please note that heading doesn't seem to be working in remote views,
        //  tried both `RelativeSizeSpan` and `AbsoluteSizeSpan` with no effect

        final float base = 12;

        final float[] sizes = {
                2.F, 1.5F, 1.17F, 1.F, .83F, .67F,
        };

        final String md = "" +
                "# H1\n" +
                "## H2\n" +
                "### H3\n" +
                "#### H4\n" +
                "##### H5\n" +
                "###### H6\n\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(Heading.class, (configuration, props) -> {
                            final Integer level = CoreProps.HEADING_LEVEL.get(props);
                            Debug.i(level);
                            if (level != null && level > 0 && level <= sizes.length) {
//                                return new RelativeSizeSpan(sizes[level - 1]);
                                final Object span = new AbsoluteSizeSpan((int) (base * sizes[level - 1] + .5F), true);
                                return new Object[]{
                                        span,
                                        new StyleSpan(Typeface.BOLD)
                                };
                            }
                            return null;
                        });
                    }
                })
                .build();
        display(markwon.toMarkdown(md));
    }

    private void lists() {
        final String md = "" +
                "* bullet 1\n" +
                "* bullet 2\n" +
                "* * bullet 2 1\n" +
                "  * bullet 2 0 1\n" +
                "1) order 1\n" +
                "1) order 2\n" +
                "1) order 3\n";

        // ordered lists _could_ be translated to raw text representation (`1.`, `1)` etc) in resulting markdown
        //  or they could be _disabled_ all together... (can ordered lists be disabled in parser?)

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(ListItem.class, (configuration, props) -> {
                            final CoreProps.ListItemType type = CoreProps.LIST_ITEM_TYPE.get(props);
                            if (type != null) {
                                // bullet and ordered list share the same markdown node
                                return new BulletSpan();
                            }
                            return null;
                        });
                    }
                })
                .build();

        display(markwon.toMarkdown(md));
    }

    private void image() {
        // please note that image _could_ be supported only if it would be available immediately
        // debugging possibility
        //
        // doesn't seem to be working

        final Bitmap bitmap = Bitmap.createBitmap(128, 256, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xFFAD1457);

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("An image: ");

        final int length = builder.length();
        builder.append("[bitmap]");
        builder.setSpan(
                new ImageSpan(this, bitmap, DynamicDrawableSpan.ALIGN_BOTTOM),
                length,
                builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.append(" okay, and ");

        final int start = builder.length();
        builder.append("[resource]");
        builder.setSpan(
                new ImageSpan(this, R.drawable.ic_memory_black_48dp),
                start,
                builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        display(builder);
    }

    private void link() {
        final String md = "" +
                "[a link](https://isa.link/) is here, styling yes, clicking - no";
        display(Markwon.create(this).toMarkdown(md));
    }

    private void blockquote() {
        final String md = "" +
                "> This was once said by me\n" +
                "> > And this one also\n\n" +
                "Me";
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(BlockQuote.class, (configuration, props) -> new QuoteSpan());
                    }
                })
                .build();
        display(markwon.toMarkdown(md));
    }

    private void strikethrough() {
        final String md = "~~strike that!~~";
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new StrikethroughPlugin())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(Strikethrough.class, (configuration, props) -> new StrikethroughSpan());
                    }
                })
                .build();
        display(markwon.toMarkdown(md));
    }

    private void display(@NonNull CharSequence cs) {
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("No NotificationManager is available");
        }

        ensureChannel(manager);

        final Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Markwon")
                .setContentText(cs)
                .setStyle(new Notification.BigTextStyle().bigText(cs));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        manager.notify(1, builder.build());
    }

    private void ensureChannel(@NonNull NotificationManager manager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        final NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
        if (channel == null) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
