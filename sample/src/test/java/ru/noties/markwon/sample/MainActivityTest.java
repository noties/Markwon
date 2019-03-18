package ru.noties.markwon.sample;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MainActivityTest {

    @Test
    public void all_sample_items_have_activity_associated() {
        final Context context = RuntimeEnvironment.application;
        for (SampleItem item : SampleItem.values()) {
            // we assert as not null, but in case of an error this method should throw
            assertNotNull(MainActivity.sampleItemIntent(context, item));
        }
    }
}