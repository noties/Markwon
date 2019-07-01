package io.noties.markwon.html.jsoup.nodes;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CommonMarkEntitiesTest {

    @Test
    public void can_access_field() {
        Assert.assertTrue("&", CommonMarkEntities.isNamedEntity("amp"));
        final int[] codepoints = new int[1];
        CommonMarkEntities.codepointsForName("amp", codepoints);
        Assert.assertEquals('&', codepoints[0]);
    }
}