package io.noties.markwon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LinkResolverDefTest {

    @Test
    public void no_scheme_https() {
        // when supplied url doesn't have scheme fallback to `https`

        // must be => `https://www.markw.on
        final String link = "www.markw.on";

        final Uri uri = resolve(link);

        final String scheme = uri.getScheme();
        assertNotNull(uri.toString(), scheme);

        assertEquals(uri.toString(), "https", scheme);
    }

    @Test
    public void scheme_present() {
        // when scheme is present, it won't be touched

        final String link = "whatnot://hey/ho";

        final Uri uri = resolve(link);

        final String scheme = uri.getScheme();
        assertEquals(uri.toString(), "whatnot", scheme);

        assertEquals(Uri.parse(link), uri);
    }

    // we could call `parseLink` directly, but this doesn't mean LinkResolverDef uses it
    @NonNull
    private Uri resolve(@NonNull String link) {
        final View view = mock(View.class);
        final Context context = mock(Context.class);
        when(view.getContext()).thenReturn(context);

        final LinkResolverDef def = new LinkResolverDef();
        def.resolve(view, link);

        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);

        verify(context, times(1))
                .startActivity(captor.capture());

        final Intent intent = captor.getValue();
        assertNotNull(intent);

        final Uri uri = intent.getData();
        assertNotNull(uri);

        return uri;
    }
}