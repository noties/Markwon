package noties.ru.markwon_samplecustomextension;

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
        setContentView(0);

        final TextView textView = findViewById(0);

        final Parser parser = new Parser.Builder()
                // we will register all known to Markwon extensions
                .extensions(Arrays.asList(
                        StrikethroughExtension.create(),
                        TablesExtension.create(),
                        TaskListExtension.create()
                ))
                // this is the handler for custom icons
                .customDelimiterProcessor(new IconProcessor())
                .build();

        final Node node = parser.parse("# Hello icons! @material-icon-home-black-24@\n\n Your account @material-icon-account_balance-white-26@ is 0.00003");
        final SpannableBuilder builder = new SpannableBuilder();
        final IconVisitor visitor = new IconVisitor(SpannableConfiguration.create(this), builder, new IconSpanProviderImpl(this, R.drawable.ic_home_black_24dp));
        node.accept(visitor);
        textView.setText(builder.text());
    }
}
