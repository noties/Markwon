package io.noties.markwon.recycler;

import static io.noties.markwon.iframe.ext.IFrameUtils.getDesmosId;
import static io.noties.markwon.iframe.ext.IFrameUtils.getVimeoVideoId;
import static io.noties.markwon.iframe.ext.IFrameUtils.getYoutubeVideoId;

import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.iframe.ext.IFrameNode;

/**
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class SimpleEntryWebView extends MarkwonAdapter.Entry<IFrameNode, SimpleEntryWebView.Holder> {

    @NonNull
    public static SimpleEntryWebView create(@LayoutRes int layoutResId, @IdRes int webViewIdRes) {
        return new SimpleEntryWebView(layoutResId, webViewIdRes);
    }

    // small cache for already rendered nodes
    private final Map<Node, Spanned> cache = new HashMap<>();

    private final int layoutResId;
    private final int webViewIdRes;

    public SimpleEntryWebView(@LayoutRes int layoutResId, @IdRes int webViewIdRes) {
        this.layoutResId = layoutResId;
        this.webViewIdRes = webViewIdRes;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(webViewIdRes, inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull Holder holder, @NonNull IFrameNode node, int depth) {
        Spanned spanned = cache.get(node);
        if (spanned == null) {
            spanned = markwon.render(node);
            cache.put(node, spanned);
        }
        renderWebView(holder, node, depth);
    }

    private void renderWebView(Holder holder, IFrameNode node, int depth) {
        String videoLink = "";
        float deviceWidth = 1080;
        float marginH = 10;
        int videoWidth = (int)(deviceWidth - marginH * 2 - depth * marginH);
        int videoHeight = (int)(videoWidth * 9 / 16);
        if (node.link().contains("youtu")) {
            String youtubeVideoId = getYoutubeVideoId(node.link());
            if (!TextUtils.isEmpty(youtubeVideoId)) {
                videoLink = "https://www.youtube.com/embed/" + youtubeVideoId;
            }
        } else if (node.link().contains("vimeo")) {
            String vimeoId = getVimeoVideoId(node.link());
            if (!TextUtils.isEmpty(vimeoId)) {
                videoLink = "https://player.vimeo.com/video/" + vimeoId;
                videoHeight = (int)(videoWidth * 3 / 4);
            }
        } else {
            String desmosId = getDesmosId(node.link());
            if (!TextUtils.isEmpty(desmosId)) {
                videoLink = "https://www.desmos.com/calculator/" + desmosId + "?embed";
            }
        }

        WebSettings webSettings = holder.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) holder.webView.getLayoutParams();
        layoutParams.height = (int) videoHeight;
        holder.webView.setLayoutParams(layoutParams);
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        holder.webView.setWebChromeClient(new WebChromeClient());
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setTextZoom(100);
        holder.webView.setVerticalScrollBarEnabled(false);
        holder.webView.setHorizontalScrollBarEnabled(false);
        holder.webView.setInitialScale(100);
        if (!videoLink.isEmpty()) {
            String dataURL = "<iframe id=\"player\" type=\"text/html\" width=\"" + videoWidth +"\" height=\"" + videoHeight + "\" " +
                    "src=\"" + videoLink + "\"frameborder=\"0\" allowfullscreen webkitallowfullscreen/>";
            holder.webView.loadData(dataURL, "text/html", "utf-8");
        }
        holder.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public static class Holder extends MarkwonAdapter.Holder {

        final WebView webView;

        protected Holder(@IdRes int webViewIdRes, @NonNull View itemView) {
            super(itemView);

            final WebView webView;
            if (webViewIdRes == 0) {
                if (!(itemView instanceof WebView)) {
                    throw new IllegalStateException("WebView is not root of layout " +
                            "(specify TextView ID explicitly): " + itemView);
                }
                webView = (WebView) itemView;
            } else {
                webView = requireView(webViewIdRes);
            }
            this.webView = webView;
        }
    }
}
