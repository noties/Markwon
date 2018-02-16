package ru.noties.markwon.sample.extension;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Arrays;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.tasklist.TaskListExtension;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text_view);

        // obtain an instance of parser
        final Parser parser = new Parser.Builder()
                // we will register all known to Markwon extensions
                .extensions(Arrays.asList(
                        StrikethroughExtension.create(),
                        TablesExtension.create(),
                        TaskListExtension.create()
                ))
                // this is the handler for custom icons
                .customDelimiterProcessor(IconProcessor.create())
                .build();

        // we process input to wrap icon definitions with `@` on both ends
        // if your input already does it, there is not need for `IconProcessor.prepare()` call.
        final String markdown = IconProcessor.prepare(getString(R.string.input));

        final Node node = parser.parse(markdown);

        final SpannableBuilder builder = new SpannableBuilder();

        // please note that here I am passing `0` as fallback it means that if markdown references
        // unknown icon, it will try to load fallback one and will fail with ResourceNotFound. It's
        // better to provide a valid fallback option
        final IconSpanProvider spanProvider = IconSpanProvider.create(this, 0);

        // create an instance of visitor to process parsed markdown
        final IconVisitor visitor = new IconVisitor(
                SpannableConfiguration.create(this),
                builder,
                spanProvider
        );

        // trigger visit
        node.accept(visitor);

        // apply
        textView.setText(builder.text());
    }
}
