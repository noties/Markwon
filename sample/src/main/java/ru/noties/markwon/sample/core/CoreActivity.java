package ru.noties.markwon.sample.core;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Spanned;
import android.widget.TextView;
import android.widget.Toast;

import org.commonmark.node.Node;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.core.CorePlugin;

public class CoreActivity extends Activity {

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        setContentView(textView);

        step_1();

        step_2();

        step_3();

        step_4();
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
    private void step_2() {

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
    private void step_3() {

        final String markdown = "*Toast* __here__!\n\n> And a quote!";

        final Markwon markwon = Markwon.create(this);

        final Spanned spanned = markwon.toMarkdown(markdown);

        Toast.makeText(this, spanned, Toast.LENGTH_LONG).show();
    }

    /**
     * To apply already parsed markdown use {@link Markwon#setParsedMarkdown(TextView, Spanned)}
     */
    private void step_4() {

        final String markdown = "This **is** pre-parsed [markdown](#)";

        final Markwon markwon = Markwon.create(this);

        // parse markdown to obtain a Node
        final Node node = markwon.parse(markdown);

        // create a spanned content from parsed node
        final Spanned spanned = markwon.render(node);

        // apply parsed markdown
        markwon.setParsedMarkdown(textView, spanned);
    }
}
