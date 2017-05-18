package ru.noties.markwon;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ru.noties.markwon.spans.AsyncDrawable;

public class AsyncDrawableTarget implements Target {

    interface DoneListener {
        void onLoadingDone(AsyncDrawableTarget target);
    }

    private final Resources resources;
    private final AsyncDrawable asyncDrawable;
    private final DoneListener listener;

    public AsyncDrawableTarget(Resources resources, AsyncDrawable asyncDrawable, DoneListener listener) {
        this.resources = resources;
        this.asyncDrawable = asyncDrawable;
        this.listener = listener;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null) {
            final Drawable drawable = new BitmapDrawable(resources, bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            asyncDrawable.setResult(drawable);
        }
        notifyDone();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (errorDrawable != null) {
            asyncDrawable.setResult(errorDrawable);
        }
        notifyDone();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        if (placeHolderDrawable != null) {
            asyncDrawable.setResult(placeHolderDrawable);
        }
    }

    private void notifyDone() {
        if (listener != null) {
            listener.onLoadingDone(this);
        }
    }
//
//    private void attach() {
//
//        // amazing stuff here, in order to keep this target alive (picasso stores target in a WeakReference)
//        // we need to do this
//
//        //noinspection unchecked
//        List<AsyncDrawableTarget> list = (List<AsyncDrawableTarget>) view.getTag(R.id.amazing);
//        if (list == null) {
//            list = new ArrayList<>(2);
//            view.setTag(R.id.amazing, list);
//        }
//        list.add(this);
//    }
//
//    private void detach() {
//        //noinspection unchecked
//        final List<AsyncDrawableTarget> list = (List<AsyncDrawableTarget>) view.getTag(R.id.amazing);
//        if (list != null) {
//            list.remove(this);
//        }
//    }
}
