package io.noties.markwon.sample.table;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class TableActivity extends ActivityWithMenuOptions {

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("tableAndLinkify", this::tableAndLinkify);
    }

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        textView = findViewById(R.id.text_view);

        tableAndLinkify();
    }

    private void tableAndLinkify() {
        final String md = "" +
                "| HEADER | HEADER | HEADER |\n" +
                "|:----:|:----:|:----:|\n" +
                "|   测试  |   测试   |   测试   |\n" +
                "|  测试  |   测试   |  测测测12345试测试测试   |\n" +
                "|   测试  |   测试   |   123445   |\n" +
                "|   测试  |   测试   |   (650) 555-1212   |\n" +
                "|   测试  |   测试   |   [link](#)   |\n" +
                "\n" +
                "测试\n" +
                "\n" +
                "[https://www.baidu.com](https://www.baidu.com)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(TablePlugin.create(this))
                .build();

        markwon.setMarkdown(textView, md);
    }
}
