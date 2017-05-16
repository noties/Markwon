package ru.noties.markwon;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import ru.noties.markwon.spans.AsyncDrawable;

public class TextViewTarget implements Target {

    private final TextView view;
    private final AsyncDrawable asyncDrawable;

    public TextViewTarget(TextView view, AsyncDrawable asyncDrawable) {
        this.view = view;
        this.asyncDrawable = asyncDrawable;

        attach();
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null) {
            final Drawable drawable = new BitmapDrawable(view.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            asyncDrawable.setResult(drawable);
        }
        detach();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (errorDrawable != null) {
            asyncDrawable.setResult(errorDrawable);
        }
        detach();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        if (placeHolderDrawable != null) {
            asyncDrawable.setResult(placeHolderDrawable);
        }
    }

    private void attach() {

        // amazing stuff here, in order to keep this target alive (picasso stores target in a WeakReference)
        // we need to do this

        //noinspection unchecked
        List<TextViewTarget> list = (List<TextViewTarget>) view.getTag(R.id.amazing);
        if (list == null) {
            list = new ArrayList<>(2);
            view.setTag(R.id.amazing, list);
        }
        list.add(this);
    }

    private void detach() {
        //noinspection unchecked
        final List<TextViewTarget> list = (List<TextViewTarget>) view.getTag(R.id.amazing);
        if (list != null) {
            list.remove(this);
        }
    }
}
