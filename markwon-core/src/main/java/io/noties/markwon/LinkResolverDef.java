package io.noties.markwon;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

public class LinkResolverDef implements LinkResolver {
    @Override
    public void resolve(@NonNull View view, @NonNull String link) {
        final Uri uri = Uri.parse(link);
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
