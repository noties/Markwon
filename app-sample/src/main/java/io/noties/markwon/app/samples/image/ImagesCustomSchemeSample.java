package io.noties.markwon.app.samples.image;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImageItem;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.SchemeHandler;
import io.noties.markwon.image.network.NetworkSchemeHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181124201",
  title = "Image destination custom scheme",
  description = "Example of handling custom scheme " +
    "(`https`, `ftp`, `whatever`, etc.) for images destination URLs " +
    "with `ImagesPlugin`",
  artifacts = {MarkwonArtifact.IMAGE},
  tags = {Tags.image}
)
public class ImagesCustomSchemeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "![image](myownscheme://en.wikipedia.org/static/images/project-logos/enwiki-2x.png)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {

          // use registry.require to obtain a plugin, does also
          // a runtime validation if this plugin is registered
          registry.require(ImagesPlugin.class, plugin -> plugin.addSchemeHandler(new SchemeHandler() {

            // it's a sample only, most likely you won't need to
            // use existing scheme-handler, this for demonstration purposes only
            final NetworkSchemeHandler handler = NetworkSchemeHandler.create();

            @NonNull
            @Override
            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
              // just replace it with https for the sack of sample
              final String url = raw.replace("myownscheme", "https");
              return handler.handle(url, Uri.parse(url));
            }

            @NonNull
            @Override
            public Collection<String> supportedSchemes() {
              return Collections.singleton("myownscheme");
            }
          }));
        }
      })
      // or we can init plugin with this factory method
//                .usePlugin(ImagesPlugin.create(plugin -> {
//                    plugin.addSchemeHandler(/**/)
//                }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
