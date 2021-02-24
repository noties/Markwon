package io.noties.markwon.app.samples.table;

import android.graphics.Color;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;
import io.noties.markwon.utils.ColorUtils;
import io.noties.markwon.utils.Dip;

@MarkwonSampleInfo(
  id = "20200702135621",
  title = "Customize table theme",
  artifacts = MarkwonArtifact.EXT_TABLES,
  tags = {Tag.theme}
)
public class TableCustomizeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "| HEADER | HEADER | HEADER |\n" +
      "|:----:|:----:|:----:|\n" +
      "|   测试  |   测试   |   测试   |\n" +
      "|  测试  |   测试   |  测测测12345试测试测试   |\n" +
      "|   测试  |   测试   |   123445   |\n" +
      "|   测试  |   测试   |   (650) 555-1212   |\n" +
      "|   测试  |   测试   |   [link](#)   |\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TablePlugin.create(builder -> {
        final Dip dip = Dip.create(context);
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
}
