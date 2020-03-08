package io.noties.markwon.sample.images;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class ImagesActivity extends ActivityWithMenuOptions {

    private TextView textView;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        // todo: same for other plugins
        return MenuOptions.create()
                .add("glide-singleImage", this::glideSingleImage)
                .add("glide-singleImageWithPlaceholder", this::glideSingleImageWithPlaceholder);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_view);
        textView = findViewById(R.id.text_view);

        glideSingleImageWithPlaceholder();
    }

    private void glideSingleImage() {
        final String md = "[![undefined](https://img.youtube.com/vi/gs1I8_m4AOM/0.jpg)](https://www.youtube.com/watch?v=gs1I8_m4AOM)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(GlideImagesPlugin.create(this))
                .build();

        markwon.setMarkdown(textView, md);
    }

    // can be checked when used first, otherwise works as expected...
    private void glideSingleImageWithPlaceholder() {
        final String md = "[![undefined](https://img.youtube.com/vi/gs1I8_m4AOM/0.jpg)](https://www.youtube.com/watch?v=gs1I8_m4AOM)";

        final Context context = this;

        final Markwon markwon = Markwon.builder(context)
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
//                        final Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp);
//                        placeholder.setBounds(0, 0, 100, 100);
                        return Glide.with(context)
                                .load(drawable.getDestination())
//                                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp));
//                                .placeholder(placeholder);
                                .placeholder(R.drawable.ic_home_black_36dp);
                    }

                    @Override
                    public void cancel(@NonNull Target<?> target) {
                        Glide.with(context)
                                .clear(target);
                    }
                }))
                .build();

        markwon.setMarkdown(textView, md);
    }
}
