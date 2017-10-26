package ru.noties.markwon;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

/**
 * @author pa.gulko zTrap (25.10.2017)
 * @since 1.0.1
 */
public class ImageClickResolverDef implements ImageClickResolver {

    @Override
    public void resolve(View view, @NonNull String link) {
        final Uri uri = Uri.parse(LinkUtils.cropImageSizes(link));
        final Context context = view.getContext();
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("LinkResolverDef", "Actvity was not found for intent, " + intent.toString());
        }
    }
}
