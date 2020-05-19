package io.noties.markwon.sample.core;

import android.os.Bundle;
import android.text.Spanned;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Set;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class CoreActivity extends ActivityWithMenuOptions {

    private TextView textView;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("simple", this::simple)
                .add("toast", this::toast)
                .add("alreadyParsed", this::alreadyParsed)
                .add("enabledBlockTypes", this::enabledBlockTypes);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);

//        step_1();

        simple();

//        toast();
//
//        alreadyParsed();
    }

    /**
     * Create a simple instance of Markwon with only Core plugin registered
     * this will handle all _natively_ supported by commonmark-java nodes:
     * <ul>
     * <li>StrongEmphasis</li>
     * <li>Emphasis</li>
     * <li>BlockQuote</li>
     * <li>Code</li>
     * <li>FencedCodeBlock</li>
     * <li>IndentedCodeBlock</li>
     * <li>ListItem (bullet-list and ordered list</li>
     * <li>Heading</li>
     * <li>Link</li>
     * <li>ThematicBreak</li>
     * <li>Paragraph (please note that there is no default span for a paragraph registered)</li>
     * </ul>
     * <p>
     * and basic core functionality:
     * <ul>
     * <li>Append text</li>
     * <li>Insert new lines (soft and hard breaks)</li>
     * </ul>
     */
    private void step_1() {

        // short call
        final Markwon markwon = Markwon.create(this);

        // this is the same as calling
        final Markwon markwon2 = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .build();
    }

    /**
     * To simply apply raw (non-parsed) markdown call {@link Markwon#setMarkdown(TextView, String)}
     */
    private void simple() {

        // this is raw markdown
        final String markdown = "Hello **markdown**!";

        final Markwon markwon = Markwon.create(this);

        // this will parse raw markdown and set parsed content to specified TextView
        markwon.setMarkdown(textView, markdown);
    }

    /**
     * To apply markdown in a different context (other than textView) use {@link Markwon#toMarkdown(String)}
     * <p>
     * Please note that some features won't work unless they are used in a TextView context. For example
     * there might be misplaced ordered lists (ordered list must have TextPaint in order to properly measure
     * its number). But also images and tables (they belong to independent modules now). Images and tables
     * are using some work-arounds in order to be displayed in relatively limited context without proper way
     * of invalidation. But if a Toast for example is created with a custom view
     * ({@code new Toast(this).setView(...) }) and has access to a TextView everything <em>should</em> work.
     */
    private void toast() {

        final String markdown = "*Toast* __here__!\n\n> And a quote!";

        final Markwon markwon = Markwon.create(this);

        final Spanned spanned = markwon.toMarkdown(markdown);

        Toast.makeText(this, spanned, Toast.LENGTH_LONG).show();
    }

    /**
     * To apply already parsed markdown use {@link Markwon#setParsedMarkdown(TextView, Spanned)}
     */
    private void alreadyParsed() {

        final String markdown = "This **is** pre-parsed [markdown](#)";

        final Markwon markwon = Markwon.create(this);

        // parse markdown to obtain a Node
        final Node node = markwon.parse(markdown);

        // create a spanned content from parsed node
        final Spanned spanned = markwon.render(node);

        // apply parsed markdown
        markwon.setParsedMarkdown(textView, spanned);
    }

    private void enabledBlockTypes() {

        final String md = "" +
                "# Head\n\n" +
                "> and disabled quote\n\n" +
                "```\n" +
                "yep\n" +
                "```";

        final Set<Class<? extends Block>> blocks = CorePlugin.enabledBlockTypes();
        blocks.remove(BlockQuote.class);

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder.enabledBlockTypes(blocks);
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }
}
