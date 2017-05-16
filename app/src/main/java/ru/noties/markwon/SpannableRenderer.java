//package ru.noties.markwon;
//
//import android.graphics.Canvas;
//import android.graphics.ColorFilter;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.annotation.IntRange;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.text.Html;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.StrikethroughSpan;
//import android.text.style.URLSpan;
//
//import org.commonmark.ext.gfm.strikethrough.Strikethrough;
//import org.commonmark.node.AbstractVisitor;
//import org.commonmark.node.BlockQuote;
//import org.commonmark.node.BulletList;
//import org.commonmark.node.Code;
//import org.commonmark.node.CustomBlock;
//import org.commonmark.node.CustomNode;
//import org.commonmark.node.Document;
//import org.commonmark.node.Emphasis;
//import org.commonmark.node.FencedCodeBlock;
//import org.commonmark.node.HardLineBreak;
//import org.commonmark.node.Heading;
//import org.commonmark.node.HtmlBlock;
//import org.commonmark.node.HtmlInline;
//import org.commonmark.node.Image;
//import org.commonmark.node.IndentedCodeBlock;
//import org.commonmark.node.Link;
//import org.commonmark.node.ListItem;
//import org.commonmark.node.Node;
//import org.commonmark.node.OrderedList;
//import org.commonmark.node.Paragraph;
//import org.commonmark.node.SoftLineBreak;
//import org.commonmark.node.StrongEmphasis;
//import org.commonmark.node.Text;
//import org.commonmark.node.ThematicBreak;
//import org.commonmark.renderer.Renderer;
//
//import java.util.ArrayDeque;
//import java.util.Arrays;
//import java.util.Deque;
//
//import ru.noties.debug.Debug;
//import ru.noties.markwon.spans.BlockQuoteSpan;
//import ru.noties.markwon.spans.CodeSpan;
//import ru.noties.markwon.spans.AsyncDrawableSpan;
//import ru.noties.markwon.spans.EmphasisSpan;
//import ru.noties.markwon.spans.BulletListItemSpan;
//import ru.noties.markwon.spans.StrongEmphasisSpan;
//import ru.noties.markwon.spans.SubScriptSpan;
//import ru.noties.markwon.spans.SuperScriptSpan;
//import ru.noties.markwon.spans.ThematicBreakSpan;
//
//public class SpannableRenderer implements Renderer {
//
//    // todo, util to extract all drawables and attach to textView (gif, animations, lazyLoading, etc)
//
//    @Override
//    public void render(Node node, Appendable output) {
//
//    }
//
//    @Override
//    public String render(Node node) {
//        // hm.. doesn't make sense to render to string
//        throw null;
//    }
//
//    public CharSequence _render(Node node) {
//        final SpannableStringBuilder builder = new SpannableStringBuilder();
//        node.accept(new SpannableNodeRenderer(builder));
//        return builder;
//    }
//
//    private static class SpannableNodeRenderer extends AbstractVisitor {
//
////        private static final float[] HEADING_SIZES = {
////                1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
////        };
//
//        private final SpannableStringBuilder builder;
//
//        private int blockQuoteIndent;
//        private int listLevel;
//
//        SpannableNodeRenderer(SpannableStringBuilder builder) {
//            this.builder = builder;
//        }
//
//        @Override
//        public void visit(HardLineBreak hardLineBreak) {
//            // todo
//            Debug.i(hardLineBreak);
//        }
//
//        @Override
//        public void visit(Text text) {
//            builder.append(text.getLiteral());
//        }
//
//        @Override
//        public void visit(StrongEmphasis strongEmphasis) {
//            final int length = builder.length();
//            visitChildren(strongEmphasis);
//            builder.setSpan(new StrongEmphasisSpan(), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(Emphasis emphasis) {
//            final int length = builder.length();
//            visitChildren(emphasis);
//            builder.setSpan(new EmphasisSpan(), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(IndentedCodeBlock indentedCodeBlock) {
//            // todo
//            Debug.i(indentedCodeBlock);
//        }
//
//        @Override
//        public void visit(BlockQuote blockQuote) {
//            builder.append('\n');
//            final int length = builder.length();
//            blockQuoteIndent += 1;
//            visitChildren(blockQuote);
//            builder.setSpan(new BlockQuoteSpan(blockQuoteIndent), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            blockQuoteIndent -= 1;
//        }
//
//        @Override
//        public void visit(Code code) {
//            final int length = builder.length();
//            builder.append(code.getLiteral());
////            builder.setSpan(new ForegroundColorSpan(0xff00ff00), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(new CodeSpan(false, length, builder.length()), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(BulletList bulletList) {
//            Debug.i(bulletList, bulletList.getBulletMarker());
//            visitChildren(bulletList);
//        }
//
//        @Override
//        public void visit(ListItem listItem) {
//            Debug.i(listItem);
////            builder.append('\n');
//            if (builder.charAt(builder.length() - 1) != '\n') {
//                builder.append('\n');
//            }
//            final int length = builder.length();
//            blockQuoteIndent += 1;
//            listLevel += 1;
//            visitChildren(listItem);
////            builder.setSpan(new BulletSpan(4, 0xff0000ff), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(new BulletListItemSpan(blockQuoteIndent, listLevel > 1, length), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            blockQuoteIndent -= 1;
//            listLevel -= 1;
//        }
//
//        @Override
//        public void visit(ThematicBreak thematicBreak) {
//            final int length = builder.length();
//            builder.append('\n')
//                    .append(' '); // without space it won't render
//            builder.setSpan(new ThematicBreakSpan(), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append('\n');
//        }
//
//        @Override
//        public void visit(OrderedList orderedList) {
//            Debug.i(orderedList, orderedList.getDelimiter(), orderedList.getStartNumber());
//            // todo, ordering numbers
//            super.visit(orderedList);
//        }
//
//        @Override
//        public void visit(SoftLineBreak softLineBreak) {
//            Debug.i(softLineBreak);
//        }
//
//        @Override
//        public void visit(Heading heading) {
//            Debug.i(heading);
//            if (builder.length() != 0 && builder.charAt(builder.length() - 1) != '\n') {
//                builder.append('\n');
//            }
//            final int length = builder.length();
//            visitChildren(heading);
//            final int max = 120;
//            final int one = 20; // total is 6
//            final int size = max - ((heading.getLevel() - 1) * one);
//            builder.setSpan(new AbsoluteSizeSpan(size), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append('\n');
//        }
//
//        @Override
//        public void visit(FencedCodeBlock fencedCodeBlock) {
//            builder.append('\n');
//            final int length = builder.length();
//            builder.append(fencedCodeBlock.getLiteral());
//            builder.setSpan(new CodeSpan(true, length, builder.length() - 1), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(Paragraph paragraph) {
//            Debug.i(paragraph);
//            if (listLevel == 0
//                    && blockQuoteIndent == 0) {
//                builder.append('\n')
//                        .append('\n');
//            }
//            visitChildren(paragraph);
//
//            if (listLevel == 0
//                    && blockQuoteIndent == 0) {
//                builder.append('\n')
//                        .append('\n');
//            }
//        }
//
////        private int htmlStart = -1;
//        private final Deque<HtmlInlineItem> htmlStack = new ArrayDeque<>();
//
//        private static class HtmlInlineItem {
//
//            final int start;
//            final String tag;
//
//            private HtmlInlineItem(int start, String tag) {
//                this.start = start;
//                this.tag = tag;
//            }
//        }
//
//        @Override
//        public void visit(HtmlInline htmlInline) {
//
////            Debug.i(htmlInline, htmlStart);
////            Debug.i(htmlInline.getLiteral(), htmlInline.toString());
//
//            // okay, it's seems that we desperately need to understand if it's opening tag or closing
//
//            final HtmlTag tag = parseTag(htmlInline.getLiteral());
//
//            Debug.i(htmlInline.getLiteral(), tag);
//
//            if (tag != null) {
//                Debug.i("tag: %s, closing: %s", tag.tag, tag.closing);
//                if (!tag.closing) {
//                    htmlStack.push(new HtmlInlineItem(builder.length(), tag.tag));
//                    visitChildren(htmlInline);
//                } else {
//                    final HtmlInlineItem item = htmlStack.pop();
//                    final int start = item.start;
//                    final int end = builder.length();
//                    // here, additionally, we can render some tags ourselves (sup/sub)
//                    if ("sup".equals(item.tag)) {
//                         builder.setSpan(new SuperScriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    } else if("sub".equals(item.tag)) {
//                        builder.setSpan(new SubScriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    } else if("del".equals(item.tag)) {
//                        // weird, but `Html` class does not return a spannable for `<del>o</del>`
//                        // seems like a bug
//                        builder.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    } else {
//                        final String html = "<" + item.tag + ">" + (builder.subSequence(start, end).toString()) + "</" + item.tag + ">";
//                        final Spanned spanned = Html.fromHtml(html);
//                        final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
//
//                        Debug.i("html: %s, start: %d, end: %d, spans: %s", html, start, end, Arrays.toString(spans));
//
//                        if (spans != null
//                                && spans.length > 0) {
//                            for (Object span: spans) {
//                                Debug.i(span);
//                                builder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                        }
//                    }
//                }
//            } else {
//                super.visit(htmlInline);
//            }
//        }
//
//        private static class HtmlTag {
//            final String tag;
//            final boolean closing;
//            HtmlTag(String tag, boolean closing) {
//                this.tag = tag;
//                this.closing = closing;
//            }
//            @Override
//            public String toString() {
//                return "HtmlTag{" +
//                        "tag='" + tag + '\'' +
//                        ", closing=" + closing +
//                        '}';
//            }
//        }
//
//        private static HtmlTag parseTag(String in) {
//
//            final HtmlTag out;
//
//            final int length = in != null
//                    ? in.length()
//                    : 0;
//
//            Debug.i(in, length);
//
//            if (length == 0 || length < 3) {
//                out = null;
//            } else {
//
//                final boolean closing = '<' == in.charAt(0) && '/' == in.charAt(1);
//                final String tag = closing
//                        ? in.substring(2, in.length() - 1)
//                        : in.substring(1, in.length() - 1);
//                out = new HtmlTag(tag, closing);
//            }
//
//            return out;
//        }
//
//        @Override
//        public void visit(HtmlBlock htmlBlock) {
//            // interestring thing... what is it also?
//            Debug.i(htmlBlock);
//        }
//
//        @Override
//        public void visit(CustomBlock customBlock) {
//            // not supported, what is it anyway?
//            Debug.i(customBlock);
//        }
//
//        @Override
//        public void visit(Document document) {
//            // the whole document, no need to do anything
//            Debug.i(document);
//            super.visit(document);
//        }
//
//        @Override
//        public void visit(Link link) {
//            Debug.i(link);
//            final int length = builder.length();
//            visitChildren(link);
//            builder.setSpan(new URLSpan(link.getDestination()), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(Image image) {
//            // not supported... maybe for now?
//            Debug.i(image);
//            final int length = builder.length();
//            super.visit(image);
//
////            final int length = builder.length();
//            final TestDrawable drawable = new TestDrawable();
//            final AsyncDrawableSpan span = new AsyncDrawableSpan(drawable);
//            builder.append("  ");
//            builder.setSpan(span, length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        @Override
//        public void visit(CustomNode customNode) {
//
//            Debug.i(customNode);
//
//            if (customNode instanceof Strikethrough) {
//                final int length = builder.length();
//                visitChildren(customNode);
//                builder.setSpan(new StrikethroughSpan(), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } else {
//                super.visit(customNode);
//            }
//        }
//    }
//
//
//    private static class TestDrawable extends Drawable {
//
//        private final Handler handler = new Handler(Looper.getMainLooper());
//        private boolean called;
//
//        TestDrawable() {
//            setBounds(0, 0, 50, 50);
//        }
//
//        @Override
//        public void draw(@NonNull final Canvas canvas) {
//            canvas.clipRect(getBounds());
//            if (!called) {
//                canvas.drawColor(0xFF00ff00);
//                handler.removeCallbacksAndMessages(null);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        called = true;
//                        setBounds(0, 0, 400, 400);
//                        invalidateSelf();
//                    }
//                }, 2000L);
//            } else {
//                canvas.drawColor(0xFFff0000);
//            }
//        }
//
//        @Override
//        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
//
//        }
//
//        @Override
//        public void setColorFilter(@Nullable ColorFilter colorFilter) {
//
//        }
//
//        @Override
//        public int getOpacity() {
//            return 0;
//        }
//
//        @Override
//        public int getIntrinsicWidth() {
//            return called ? 400 : 50;
//        }
//
//        @Override
//        public int getIntrinsicHeight() {
//            return called ? 400 : 50;
//        }
//    }
//}
