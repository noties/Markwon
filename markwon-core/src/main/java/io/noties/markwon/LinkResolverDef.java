package io.noties.markwon;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

public class LinkResolverDef implements LinkResolver {

    // @since 4.3.0
    private static final String DEFAULT_SCHEME = "https";

    @Override
    public void resolve(@NonNull View view, @NonNull String link) {
        final Uri uri = parseLink(link);
        final Context context = view.getContext();
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("LinkResolverDef", "Actvity was not found for the link: '" + link + "'");
        }
    }

    /**
     * @since 4.3.0
     */
    @NonNull
    private static Uri parseLink(@NonNull String link) {
        final Uri uri = Uri.parse(link);
        if (TextUtils.isEmpty(uri.getScheme())) {
            return uri.buildUpon()
                    .scheme(DEFAULT_SCHEME)
                    .build();
        }
        return uri;
    }
}
