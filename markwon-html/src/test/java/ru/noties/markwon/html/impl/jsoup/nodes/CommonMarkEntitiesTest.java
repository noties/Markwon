package ru.noties.markwon.html.impl.jsoup.nodes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.html.jsoup.nodes.CommonMarkEntities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CommonMarkEntitiesTest {

    @Test
    public void can_access_field() {
        assertTrue("&", CommonMarkEntities.isNamedEntity("amp"));
        final int[] codepoints = new int[1];
        CommonMarkEntities.codepointsForName("amp", codepoints);
        assertEquals('&', codepoints[0]);
    }
}