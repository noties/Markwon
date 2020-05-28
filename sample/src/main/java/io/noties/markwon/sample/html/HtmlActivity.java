package io.noties.markwon.sample.html;

import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlEmptyTagReplacement;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.html.tag.SimpleTagHandler;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class HtmlActivity extends ActivityWithMenuOptions {

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("align", this::align)
                .add("randomCharSize", this::randomCharSize)
                .add("enhance", this::enhance)
                .add("image", this::image)
//                .add("elegantUnderline", this::elegantUnderline)
                .add("iframe", this::iframe)
                .add("emptyTagReplacement", this::emptyTagReplacement)
                .add("centerTag", this::centerTag);
    }

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);

        // let's define some custom tag-handlers

        textView = findViewById(R.id.text_view);

        emptyTagReplacement();
    }

    // we can use `SimpleTagHandler` for _simple_ cases (when the whole tag content
    // will have spans from start to end)
    //
    // we can use any tag name, even not defined in HTML spec
    private static class AlignTagHandler extends SimpleTagHandler {

        @Nullable
        @Override
        public Object getSpans(
                @NonNull MarkwonConfiguration configuration,
                @NonNull RenderProps renderProps,
                @NonNull HtmlTag tag) {

            final Layout.Alignment alignment;

            // html attribute without value, <align center></align>
            if (tag.attributes().containsKey("center")) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else if (tag.attributes().containsKey("end")) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else {
                // empty value or any other will make regular alignment
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }

            return new AlignmentSpan.Standard(alignment);
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("align");
        }
    }

    private void align() {

        final String md = "" +
                "<align center>We are centered</align>\n" +
                "\n" +
                "<align end>We are at the end</align>\n" +
                "\n" +
                "<align>We should be at the start</align>\n" +
                "\n";


        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new AlignTagHandler()));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    // each character will have random size
    private static class RandomCharSize extends TagHandler {

        private final Random random;
        private final float base;

        RandomCharSize(@NonNull Random random, float base) {
            this.random = random;
            this.base = base;
        }

        @Override
        public void handle(
                @NonNull MarkwonVisitor visitor,
                @NonNull MarkwonHtmlRenderer renderer,
                @NonNull HtmlTag tag) {

            final SpannableBuilder builder = visitor.builder();

            // text content is already added, we should only apply spans

            for (int i = tag.start(), end = tag.end(); i < end; i++) {
                final int size = (int) (base * (random.nextFloat() + 0.5F) + 0.5F);
                builder.setSpan(new AbsoluteSizeSpan(size, false), i, i + 1);
            }
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("random-char-size");
        }
    }

    private void randomCharSize() {

        final String md = "" +
                "<random-char-size>\n" +
                "This message should have a jumpy feeling because of different sizes of characters\n" +
                "</random-char-size>\n\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new RandomCharSize(new Random(42L), textView.getTextSize())));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private static class EnhanceTagHandler extends TagHandler {

        private final int enhanceTextSize;

        EnhanceTagHandler(@Px int enhanceTextSize) {
            this.enhanceTextSize = enhanceTextSize;
        }

        @Override
        public void handle(
                @NonNull MarkwonVisitor visitor,
                @NonNull MarkwonHtmlRenderer renderer,
                @NonNull HtmlTag tag) {

            // we require start and end to be present
            final int start = parsePosition(tag.attributes().get("start"));
            final int end = parsePosition(tag.attributes().get("end"));

            if (start > -1 && end > -1) {
                visitor.builder().setSpan(
                        new AbsoluteSizeSpan(enhanceTextSize),
                        tag.start() + start,
                        tag.start() + end
                );
            }
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("enhance");
        }

        private static int parsePosition(@Nullable String value) {
            int position;
            if (!TextUtils.isEmpty(value)) {
                try {
                    position = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    position = -1;
                }
            } else {
                position = -1;
            }
            return position;
        }
    }

    private void enhance() {

        final String md = "" +
                "<enhance start=\"5\" end=\"12\">This is text that must be enhanced, at least a part of it</enhance>";


        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                                .addHandler(new EnhanceTagHandler((int) (textView.getTextSize() * 2 + .05F))));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void image() {
        // treat unclosed/void `img` tag as HTML inline
        final String md = "" +
                "## Try CommonMark\n" +
                "\n" +
                "Markwon IMG:\n" +
                "\n" +
                "![](https://upload.wikimedia.org/wikipedia/it/thumb/c/c5/GTA_2.JPG/220px-GTA_2.JPG)\n" +
                "\n" +
                "New lines...\n" +
                "\n" +
                "HTML IMG:\n" +
                "\n" +
                "<img src=\"https://upload.wikimedia.org/wikipedia/it/thumb/c/c5/GTA_2.JPG/220px-GTA_2.JPG\"></img>\n" +
                "\n" +
                "New lines\n\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(ImagesPlugin.create())
                .usePlugin(HtmlPlugin.create())
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void elegantUnderline() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Toast.makeText(
                    this,
                    "Elegant underline is supported on KitKat and up",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        final String underline = "Well well wel, and now <u>Gogogo, quite **perfect** yeah</u> and nice and elegant";

        final String md = "" +
                underline + "\n\n" +
                "<b>" + underline + "</b>\n\n" +
                "<font name=serif>" + underline + "</font>\n\n" +
                "<font name=sans-serif>" + underline + underline + underline + "</font>\n\n" +
                "<font name=monospace>" + underline + "</font>\n\n" +
                "";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create(plugin -> plugin
                        .addHandler(new HtmlFontTagHandler())
                        .addHandler(new HtmlElegantUnderlineTagHandler())))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void iframe() {
        final String md = "" +
                "# Hello iframe\n\n" +
                "<p class=\"p1\"><img title=\"JUMP FORCE\" src=\"https://img1.ak.crunchyroll.com/i/spire1/f0c009039dd9f8dff5907fff148adfca1587067000_full.jpg\" alt=\"JUMP FORCE\" width=\"640\" height=\"362\" /></p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\">Switch owners will soon get to take part in the ultimate <em>Shonen Jump </em>rumble. Bandai Namco announced plans to bring <strong><em>Jump Force </em></strong>to <strong>Switch</strong> as <strong><em>Jump Force Deluxe Edition</em></strong>, with a release set for sometime this year. This version will include all of the original playable characters and those from Character Pass 1, and <strong>Character Pass 2 is also in the works </strong>for all versions, starting with <strong>Shoto Todoroki from </strong><span style=\"color: #ff9900;\"><a href=\"/my-hero-academia?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><strong><em>My Hero Academia</em></strong></span></a></span>.</p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\">Other than Todoroki, Bandai Namco hinted that the four other Character Pass 2 characters will hail from <span style=\"color: #ff9900;\"><a href=\"/hunter-x-hunter?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>Hunter x Hunter</em></span></a></span>, <em>Yu Yu Hakusho</em>, <span style=\"color: #ff9900;\"><a href=\"/bleach?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>Bleach</em></span></a></span>, and <span style=\"color: #ff9900;\"><a href=\"/jojos-bizarre-adventure?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>JoJo's Bizarre Adventure</em></span></a></span>. Character Pass 2 will be priced at $17.99, and Todoroki launches this spring.<span class=\"Apple-converted-space\">&nbsp;</span></p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\"><iframe style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://www.youtube.com/embed/At1qTj-LWCc\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe></p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\">Character Pass 2 promo:</p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\"><iframe style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://www.youtube.com/embed/CukwN6kV4R4\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe></p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\"><a href=\"https://got.cr/PremiumTrial-NewsBanner4\"><img style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://img1.ak.crunchyroll.com/i/spire4/78f5441d927cf160a93e037b567c2b1f1587067041_full.png\" alt=\"\" width=\"640\" height=\"43\" /></a></p>\n" +
                "<p class=\"p2\">&nbsp;</p>\n" +
                "<p class=\"p1\">-------</p>\n" +
                "<p class=\"p1\"><em>Joseph Luster is the Games and Web editor at </em><a href=\"http://www.otakuusamagazine.com/ME2/Default.asp\"><em>Otaku USA Magazine</em></a><em>. You can read his webcomic, </em><a href=\"http://subhumanzoids.com/comics/big-dumb-fighting-idiots/\">BIG DUMB FIGHTING IDIOTS</a><em> at </em><a href=\"http://subhumanzoids.com/\"><em>subhumanzoids</em></a><em>. Follow him on Twitter </em><a href=\"https://twitter.com/Moldilox\"><em>@Moldilox</em></a><em>.</em><span class=\"Apple-converted-space\">&nbsp;</span></p>";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(ImagesPlugin.create())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new IFrameHtmlPlugin())
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void emptyTagReplacement() {

        final String md = "" +
                "<empty></empty> the `<empty></empty>` is replaced?";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create(plugin -> {
                    plugin.emptyTagReplacement(new HtmlEmptyTagReplacement() {
                        @Nullable
                        @Override
                        public String replace(@NonNull HtmlTag tag) {
                            if ("empty".equals(tag.name())) {
                                return "REPLACED_EMPTY_WITH_IT";
                            }
                            return super.replace(tag);
                        }
                    });
                }))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void centerTag() {
        final String html = "<html>\n" +
                "\n" +
                "<head></head>\n" +
                "\n" +
                "<body>\n" +
                "    <p></p>\n" +
                "    <h3>LiSA's Sword Art Online: Alicization OP Song \"ADAMAS\" Certified Platinum with 250,000 Downloads</h3>\n" +
                "    <p></p>\n" +
                "    <h5>The upper tune was already certified Gold one month after its digital release</h5>\n" +
                "    <p>According to The Recording Industry Association of Japan (RIAJ)'s monthly report for April 2020, one of the <span\n" +
                "            style=\"color: #ff9900;\"><strong><a href=\"http://www.lxixsxa.com/\" target=\"_blank\"><span\n" +
                "                        style=\"color: #ff9900;\">LiSA</span></a></strong></span>'s 14th single songs,\n" +
                "        <strong>\"ADAMAS\"</strong>&nbsp;(the first OP theme for the TV anime <a href=\"/sword-art-online\"\n" +
                "            target=\"_blank\"><span style=\"color: #ff9900;\"><strong><em>Sword Art Online:\n" +
                "                        Alicization</em></strong></span></a>) has been certified <strong>Platinum</strong> for\n" +
                "        surpassing 250,000 downloads.</p>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <p>As a double A-side single with <strong>\"Akai Wana (who loves it?),\"</strong> <strong>\"ADAMAS\"</strong> was\n" +
                "        released from SACRA Music in Japan on December 12, 2018. Its CD single ranked second in Oricon's weekly single\n" +
                "        chart by selling 35,000 copies in its first week. Meanwhile, the song was released digitally two months prior to\n" +
                "        its CD release, October 8, then reached Gold (100,000 downloads) in the following month.</p>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <center>\n" +
                "        <p><strong>\"ADAMAS\"</strong> MV YouTube EDIT ver.:</p>\n" +
                "        <p><iframe src=\"https://www.youtube.com/embed/UeEIl4JlE-g\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe>\n" +
                "        </p>\n" +
                "        <p>&nbsp;</p>\n" +
                "        <p>Standard edition CD jacket:</p>\n" +
                "        <p><img src=\"https://img1.ak.crunchyroll.com/i/spire2/d7b1d6bc7563224388ef5ffc04a967581589950464_full.jpg\"\n" +
                "                alt=\"\" width=\"640\" height=\"635\"></p>\n" +
                "    </center>\n" +
                "    <p>&nbsp;&nbsp;</p>\n" +
                "    <hr>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <p>Source: RIAJ press release</p>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <p><em>©SACRA MUSIC</em></p>\n" +
                "    <p>&nbsp;</p>\n" +
                "    <p style=\"text-align: center;\"><a href=\"https://got.cr/PremiumTrial-NewsBanner4\"><em><img\n" +
                "                    src=\"https://img1.ak.crunchyroll.com/i/spire4/78f5441d927cf160a93e037b567c2b1f1559091520_full.png\"\n" +
                "                    alt=\"\" width=\"640\" height=\"43\"></em></a></p>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create(new HtmlPlugin.HtmlConfigure() {
                    @Override
                    public void configureHtml(@NonNull HtmlPlugin plugin) {
                        plugin.addHandler(new CenterTagHandler());
                    }
                }))
                .usePlugin(new IFrameHtmlPlugin())
                .usePlugin(ImagesPlugin.create())
                .build();

        markwon.setMarkdown(textView, html);
    }
}
