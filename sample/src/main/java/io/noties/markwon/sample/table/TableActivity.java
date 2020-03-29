package io.noties.markwon.sample.table;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tables.TableTheme;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;
import io.noties.markwon.utils.ColorUtils;
import io.noties.markwon.utils.Dip;

public class TableActivity extends ActivityWithMenuOptions {

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("customize", this::customize)
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

    private void customize() {
        final String md = "" +
                "| HEADER | HEADER | HEADER |\n" +
                "|:----:|:----:|:----:|\n" +
                "|   测试  |   测试   |   测试   |\n" +
                "|  测试  |   测试   |  测测测12345试测试测试   |\n" +
                "|   测试  |   测试   |   123445   |\n" +
                "|   测试  |   测试   |   (650) 555-1212   |\n" +
                "|   测试  |   测试   |   [link](#)   |\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(builder -> {
                    final Dip dip = Dip.create(this);
                    builder
                            .tableBorderWidth(dip.toPx(2))
                            .tableBorderColor(Color.YELLOW)
                            .tableCellPadding(dip.toPx(4))
                            .tableHeaderRowBackgroundColor(ColorUtils.applyAlpha(Color.RED, 80))
                            .tableEvenRowBackgroundColor(ColorUtils.applyAlpha(Color.GREEN, 80))
                            .tableOddRowBackgroundColor(ColorUtils.applyAlpha(Color.BLUE, 80));
                }))
                .build();

        markwon.setMarkdown(textView, md);
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
                "[link link](https://link.link)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(TablePlugin.create(this))
                .build();

        markwon.setMarkdown(textView, md);
    }
}
